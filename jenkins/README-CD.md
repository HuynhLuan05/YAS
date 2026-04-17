# Jenkins CD Setup (Intern Guide)

This guide sets up Jenkins CD for this repo with:
- Jenkins on Kubernetes (Helm)
- `developer_build` pipeline job
- `teardown` pipeline job
- GitHub webhook trigger for `main`

## 1) Prerequisites

- Access to Kubernetes cluster (`kubectl get nodes` works)
- Helm 3 installed
- Jenkins namespace permission
- Repo access: `https://github.com/HuynhLuan05/Project-1.git`
- Container registry already stores images by tag:
  - `latest`
  - commit SHA (short SHA used by pipeline)

## 2) Deploy Jenkins on K8s (Helm)

Files:
- `k8s/deploy/jenkins/values.yaml`
- `k8s/deploy/jenkins/install-jenkins.sh`

Run:

```bash
cd k8s/deploy
chmod +x jenkins/install-jenkins.sh
./jenkins/install-jenkins.sh
```

Verify:

```bash
kubectl get pods -n jenkins
kubectl get svc -n jenkins
```

Default NodePort in values is `32080`.

## 3) Option A — Install `helm` and `kubectl` inside Jenkins pod

Use this option when you keep the default Jenkins image and want the quickest setup for class/demo.

1. Wait until Jenkins pod is ready:

```bash
kubectl --kubeconfig=kubeconfig.yaml get pods -n jenkins -w
```

2. Install tools in the `jenkins` container:

```bash
kubectl --kubeconfig=kubeconfig.yaml exec -n jenkins jenkins-0 -c jenkins -- bash -lc '
  set -e
  apt-get update
  apt-get install -y curl ca-certificates
  ARCH=$(dpkg --print-architecture)
  K8S_ARCH=$([ "$ARCH" = "arm64" ] && echo arm64 || echo amd64)
  KUBECTL_VERSION=$(curl -sL https://dl.k8s.io/release/stable.txt)
  curl -sL "https://dl.k8s.io/release/${KUBECTL_VERSION}/bin/linux/${K8S_ARCH}/kubectl" -o /usr/local/bin/kubectl
  chmod +x /usr/local/bin/kubectl

  HELM_VERSION=v3.18.4
  curl -sL "https://get.helm.sh/helm-${HELM_VERSION}-linux-${K8S_ARCH}.tar.gz" -o /tmp/helm.tgz
  tar -xzf /tmp/helm.tgz -C /tmp
  mv /tmp/linux-${K8S_ARCH}/helm /usr/local/bin/helm
  chmod +x /usr/local/bin/helm
'
```

3. Verify installed tools:

```bash
kubectl --kubeconfig=kubeconfig.yaml exec -n jenkins jenkins-0 -c jenkins -- helm version
kubectl --kubeconfig=kubeconfig.yaml exec -n jenkins jenkins-0 -c jenkins -- kubectl version --client
kubectl --kubeconfig=kubeconfig.yaml exec -n jenkins jenkins-0 -c jenkins -- git --version
```

Note: tools installed this way are not persistent if the pod is recreated.

## 4) Install/confirm required plugins

In Jenkins UI (`Manage Jenkins` -> `Plugins`), confirm these are installed:
- Kubernetes
- Pipeline (workflow-aggregator)
- Git
- Docker Pipeline (docker-workflow)
- Blue Ocean

## 5) Configure Jenkins credentials

`Manage Jenkins` -> `Credentials` -> `System` -> `Global credentials`:

- `github-token` (Secret text): GitHub PAT (repo read + webhook if needed)
- **`kubeconfig-yas` (Secret file): kubeconfig used by CD pipelines (required for Option C below)**

### Option C — Deploy using a kubeconfig credential (not the Jenkins pod ServiceAccount)

Pipelines `developer_build` and `teardown` wrap every `helm` / `kubectl` step with `withCredentials` and set `KUBECONFIG` to the uploaded file. The identity inside that kubeconfig must be allowed to manage Helm releases in namespace `yas` (including `secrets` — Helm 3 stores release metadata there), and to read `nodes` + `ingress-nginx` services for the NodePort URL step.

**A) Jenkins UI**

1. `Manage Jenkins` -> `Credentials` -> add **Secret file**.
2. **ID** must be exactly: `kubeconfig-yas` (or change `KUBECONFIG_CREDENTIAL_ID` in the Jenkinsfiles to match).
3. Upload your kubeconfig file. Do not commit this file to Git.

**B) Obtain a kubeconfig (pick one)**

- **Use an existing admin/deploy kubeconfig** from Member 1 if policy allows (same file you use with `kubectl` locally).
- **Or** create a dedicated SA and token (recommended for class demos):

```bash
kubectl apply -f k8s/deploy/jenkins/rbac-cd-kubeconfig-sa.yaml

# Token (Kubernetes 1.24+); adjust --duration as needed
TOKEN=$(kubectl create token jenkins-cd -n jenkins --duration=8760h)

SERVER=$(kubectl config view --minify -o jsonpath='{.clusters[0].cluster.server}')
CA_DATA=$(kubectl config view --minify --raw -o jsonpath='{.clusters[0].cluster.certificate-authority-data}')

# Writes jenkins-cd.kubeconfig — upload this file as the Jenkins Secret file credential
cat > jenkins-cd.kubeconfig <<EOF
apiVersion: v1
kind: Config
clusters:
- cluster:
    certificate-authority-data: ${CA_DATA}
    server: ${SERVER}
  name: cd-cluster
contexts:
- context:
    cluster: cd-cluster
    user: jenkins-cd
  name: cd-context
current-context: cd-context
users:
- name: jenkins-cd
  user:
    token: ${TOKEN}
EOF
```

**C) If you change the credential ID**

Edit `KUBECONFIG_CREDENTIAL_ID` in `jenkins/Jenkinsfile.developer_build` and `jenkins/Jenkinsfile.teardown` to match the Jenkins credential ID you created.

## 6) Create `developer_build` pipeline job

1. New Item -> **Pipeline** -> name: `developer_build`
2. Pipeline definition: **Pipeline script from SCM**
3. SCM: Git
4. Repository URL: `https://github.com/HuynhLuan05/Project-1.git`
5. Branch Specifier: `*/main`
6. Script Path: `jenkins/Jenkinsfile.developer_build`

Run once with:
- `TARGET_SERVICE=delivery`
- `TARGET_BRANCH=feature/delivery`
- `NAMESPACE=yas`

Expected behavior:
- Jenkins resolves SHA from `TARGET_BRANCH`
- Target service deploys with SHA tag
- Other services deploy with `latest`
- Build description shows clickable NodePort URL

## 7) Create `teardown` pipeline job

1. New Item -> **Pipeline** -> name: `teardown`
2. Pipeline script from SCM -> same repo
3. Script Path: `jenkins/Jenkinsfile.teardown`

Run parameter:
- `NAMESPACE=yas`

Expected:
- `helm uninstall` for all app releases in `yas`
- final `helm list -n yas` mostly empty

## 8) Configure GitHub webhook (main branch)

In GitHub repository settings:

1. `Settings` -> `Webhooks` -> `Add webhook`
2. Payload URL:
   - `http://<jenkins-node-ip>:32080/github-webhook/`
3. Content type: `application/json`
4. Events:
   - **Just the push event**
5. Active: checked

Then in Jenkins job:
- Enable `GitHub hook trigger for GITScm polling`
- Keep branch filter to `main` (`*/main`) for CD job.

## 9) Troubleshooting

- If `secrets is forbidden` for `system:serviceaccount:jenkins:jenkins`:
  - use **Option C** (kubeconfig credential) with a user/SA that has deploy rights on `yas`, or bind a Role to the Jenkins pod SA (Option A RBAC on cluster).
- If `helm`/`kubectl` missing in agent:
  - use Jenkins agent image that includes both tools, or run on controller with tools installed.
- If NodePort URL not shown:
  - check `ingress-nginx-controller` service exists
  - fallback is first NodePort service in `yas`.
- If SHA image not found:
  - ensure CI build-push process publishes `ghcr.io/nashtech-garage/yas-<service>:<short-sha>`

## 10) Files committed for CD

- `jenkins/Jenkinsfile.developer_build`
- `jenkins/Jenkinsfile.teardown`
- `k8s/charts/delivery/Chart.yaml`
- `k8s/charts/delivery/values.yaml`
- `k8s/charts/delivery/.helmignore`
- `k8s/deploy/jenkins/values.yaml`
- `k8s/deploy/jenkins/install-jenkins.sh`
- `k8s/deploy/jenkins/rbac-cd-kubeconfig-sa.yaml` (optional SA for Option C)
