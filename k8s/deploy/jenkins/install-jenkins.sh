#!/bin/bash
set -euo pipefail

NAMESPACE="${NAMESPACE:-jenkins}"
RELEASE_NAME="${RELEASE_NAME:-jenkins}"
VALUES_FILE="${VALUES_FILE:-./jenkins/values.yaml}"

helm repo add jenkins https://charts.jenkins.io
helm repo update

kubectl create namespace "${NAMESPACE}" --dry-run=client -o yaml | kubectl apply -f -

helm upgrade --install "${RELEASE_NAME}" jenkins/jenkins \
  --namespace "${NAMESPACE}" \
  --create-namespace \
  -f "${VALUES_FILE}"

echo "Jenkins has been deployed."
echo "Service info:"
kubectl get svc -n "${NAMESPACE}" "${RELEASE_NAME}" -o wide
