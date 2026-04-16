Here’s your guide rewritten clearly in English:

---

# YAS Cluster Access Guide — For Members 2, 3, 4

## 📌 Cluster Information

|                 |                         |
| --------------- | ----------------------- |
| **Minikube IP** | `192.168.58.2`          |
| **Namespaces**  | `yas`, `dev`, `staging` |

---

## 🔧 Step 1 — Install kubectl

### **Ubuntu / WSL2**

```bash
curl -LO "https://dl.k8s.io/release/$(curl -sL https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl && sudo mv kubectl /usr/local/bin/
```

### **Windows**

```powershell
winget install Kubernetes.kubectl
```

---

## 🔑 Step 2 — Get kubeconfig from Member 1

Contact Member 1 via private chat to receive the file `kubeconfig-yas.yaml`.

Then run:

```bash
mkdir -p ~/.kube
cp kubeconfig-yas.yaml ~/.kube/config

# Verify connection
kubectl get nodes
kubectl get pods -n yas
```

---

## 🌐 Step 3 — Add entries to /etc/hosts

### **Linux / WSL2**

```bash
sudo nano /etc/hosts
```

Add:

```
192.168.58.2  storefront.yas.local.com
192.168.58.2  backoffice.yas.local.com
192.168.58.2  api.yas.local.com
192.168.58.2  identity.yas.local.com
192.168.58.2  grafana.yas.local.com
192.168.58.2  kibana.yas.local.com
192.168.58.2  akhq.yas.local.com
192.168.58.2  pgadmin.yas.local.com
```

### **Windows**

Open:

```
C:\Windows\System32\drivers\etc\hosts
```

(using Notepad as Administrator)

Add:

```
192.168.58.2  storefront.yas.local.com
192.168.58.2  backoffice.yas.local.com
192.168.58.2  api.yas.local.com
192.168.58.2  identity.yas.local.com
```

---

## ✅ Step 4 — Verify Setup

```bash
# Check running pods
kubectl get pods -n yas

# Check available namespaces
kubectl get namespaces

# Check services and NodePorts
kubectl get svc -n yas | grep NodePort
```

### Expected Results:

* ~20 pods with status `1/1 Running`
* Namespaces: `yas`, `dev`, `staging`
* NodePorts: `30001`, `30002`, `30003`

---

## ⚠️ Notes

* The cluster **only works when Member 1’s machine is running**
* You must be on the **same LAN**, or use **Tailscale / Ngrok** for remote access
* **Do NOT commit** the kubeconfig file to GitHub

---

If you want, I can also turn this into a polished README.md for your repo.
