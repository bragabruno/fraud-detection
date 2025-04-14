#!/bin/bash
set -e

ENVIRONMENT=$1

if [ -z "$ENVIRONMENT" ]; then
    echo "Usage: $0 <environment>"
    exit 1
fi

# Load environment variables
source jenkins/environments/${ENVIRONMENT}.env

echo "Validating deployment in ${ENVIRONMENT} environment..."

# Configure kubectl
export KUBECONFIG=${KUBE_CONFIG}
kubectl config use-context ${ENVIRONMENT}

# Check all deployments status
SERVICES=("api-gateway" "transaction" "monitoring" "notification" "rule-engine" "analytics")
for SERVICE in "${SERVICES[@]}"; do
    echo "Checking ${SERVICE} deployment status..."
    
    # Verify deployment rollout
    if ! kubectl rollout status deployment/${SERVICE} -n ${APP_NAMESPACE} --timeout=60s; then
        echo "Error: ${SERVICE} deployment failed"
        exit 1
    fi
    
    # Check pod health
    PODS=$(kubectl get pods -n ${APP_NAMESPACE} -l app=${SERVICE} -o jsonpath='{.items[*].metadata.name}')
    for POD in ${PODS}; do
        echo "Checking pod ${POD}..."
        
        # Check if pod is running and ready
        STATUS=$(kubectl get pod ${POD} -n ${APP_NAMESPACE} -o jsonpath='{.status.phase}')
        if [ "${STATUS}" != "Running" ]; then
            echo "Error: Pod ${POD} is not running (status: ${STATUS})"
            exit 1
        fi
        
        # Check container readiness
        if ! kubectl get pod ${POD} -n ${APP_NAMESPACE} -o jsonpath='{.status.containerStatuses[0].ready}' | grep -q "true"; then
            echo "Error: Pod ${POD} is not ready"
            exit 1
        fi
    done
    
    # Verify service endpoints
    echo "Checking ${SERVICE} endpoints..."
    ENDPOINTS=$(kubectl get endpoints -n ${APP_NAMESPACE} ${SERVICE} -o jsonpath='{.subsets[*].addresses[*].ip}')
    if [ -z "${ENDPOINTS}" ]; then
        echo "Error: No endpoints found for service ${SERVICE}"
        exit 1
    fi
done

# Run health checks
API_URL=$(kubectl get ing -n ${APP_NAMESPACE} api-gateway -o jsonpath='{.spec.rules[0].host}')
echo "Running health checks against ${API_URL}..."

# Check API Gateway health
if ! curl -sf "https://${API_URL}/actuator/health" > /dev/null; then
    echo "Error: API Gateway health check failed"
    exit 1
fi

# Run smoke tests if they exist for the environment
if [ -f "jenkins/environments/${ENVIRONMENT}.smoke-test" ]; then
    source "jenkins/environments/${ENVIRONMENT}.smoke-test"
fi

echo "Deployment validation completed successfully"