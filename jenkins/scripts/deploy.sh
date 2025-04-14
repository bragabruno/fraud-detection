#!/bin/bash
set -e

ENVIRONMENT=$1
VERSION=$2

if [ -z "$ENVIRONMENT" ] || [ -z "$VERSION" ]; then
    echo "Usage: $0 <environment> <version>"
    exit 1
fi

# Load environment variables
source jenkins/environments/${ENVIRONMENT}.env

echo "Deploying version ${VERSION} to ${ENVIRONMENT}..."

# Configure kubectl with the appropriate context
export KUBECONFIG=${KUBE_CONFIG}
kubectl config use-context ${ENVIRONMENT}

# Apply secrets from HashiCorp Vault
./jenkins/scripts/apply-secrets.sh ${ENVIRONMENT}

# Update kubernetes configurations with the current version
for SERVICE in api-gateway transaction monitoring notification rule-engine analytics; do
    echo "Deploying ${SERVICE} service..."
    
    # Replace version in deployment files
    sed -i "s|IMAGE_TAG|${VERSION}|g" jenkins/kubernetes/${ENVIRONMENT}/${SERVICE}.yaml
    
    # Apply Kubernetes configurations
    kubectl apply -f jenkins/kubernetes/${ENVIRONMENT}/${SERVICE}.yaml
    
    # Wait for deployment to complete
    kubectl rollout status deployment/${SERVICE} -n ${APP_NAMESPACE} --timeout=300s
done

# Run environment-specific post-deployment steps
if [ -f "jenkins/environments/${ENVIRONMENT}.post-deploy" ]; then
    source "jenkins/environments/${ENVIRONMENT}.post-deploy"
fi

echo "Deployment to ${ENVIRONMENT} completed successfully"