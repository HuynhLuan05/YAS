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

## 3) Install/confirm required plugins

In Jenkins UI (`Manage Jenkins` -> `Plugins`), confirm these are installed:
- Kubernetes
- Pipeline (workflow-aggregator)
- Git
- Docker Pipeline (docker-workflow)
- Blue Ocean

## 4) Configure Jenkins credentials

`Manage Jenkins` -> `Credentials` -> `System` -> `Global credentials`:

- `github-token` (Secret text): GitHub PAT (repo read + webhook if needed)
- `kubeconfig-yas` (Secret file): kubeconfig for deployment cluster

If you deploy from inside cluster using service account, `kubeconfig-yas` can be optional.

## 5) Create `developer_build` pipeline job

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

## 6) Create `teardown` pipeline job

1. New Item -> **Pipeline** -> name: `teardown`
2. Pipeline script from SCM -> same repo
3. Script Path: `jenkins/Jenkinsfile.teardown`

Run parameter:
- `NAMESPACE=yas`

Expected:
- `helm uninstall` for all app releases in `yas`
- final `helm list -n yas` mostly empty

## 7) Configure GitHub webhook (main branch)

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

## 8) Troubleshooting

- If `helm`/`kubectl` missing in agent:
  - use Jenkins agent image that includes both tools, or run on controller with tools installed.
- If NodePort URL not shown:
  - check `ingress-nginx-controller` service exists
  - fallback is first NodePort service in `yas`.
- If SHA image not found:
  - ensure CI build-push process publishes `ghcr.io/nashtech-garage/yas-<service>:<short-sha>`

## 9) Files committed for CD

- `jenkins/Jenkinsfile.developer_build`
- `jenkins/Jenkinsfile.teardown`
- `k8s/charts/delivery/Chart.yaml`
- `k8s/charts/delivery/values.yaml`
- `k8s/charts/delivery/.helmignore`
- `k8s/deploy/jenkins/values.yaml`
- `k8s/deploy/jenkins/install-jenkins.sh`
