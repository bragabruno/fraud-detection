#!/bin/bash
set -e

ENVIRONMENT=$1

if [ -z "$ENVIRONMENT" ]; then
    echo "Usage: $0 <environment>"
    exit 1
fi

echo "Applying secrets for ${ENVIRONMENT} environment..."

# Load environment-specific variables
source jenkins/environments/${ENVIRONMENT}.env

# Authenticate with Vault
vault login -method=kubernetes \
    role=${VAULT_ROLE} \
    jwt=$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)

# Create Kubernetes secret for database credentials
vault kv get -format=json secret/${ENVIRONMENT}/database | \
    jq -r '.data.data | {
        "apiVersion": "v1",
        "kind": "Secret",
        "metadata": {
            "name": "db-credentials",
            "namespace": "'${APP_NAMESPACE}'"
        },
        "type": "Opaque",
        "data": {
            "username": (.username | @base64),
            "password": (.password | @base64)
        }
    }' | kubectl apply -f -

# Create Kubernetes secret for Redis credentials
vault kv get -format=json secret/${ENVIRONMENT}/redis | \
    jq -r '.data.data | {
        "apiVersion": "v1",
        "kind": "Secret",
        "metadata": {
            "name": "redis-credentials",
            "namespace": "'${APP_NAMESPACE}'"
        },
        "type": "Opaque",
        "data": {
            "password": (.password | @base64)
        }
    }' | kubectl apply -f -

# Create Kubernetes secret for API keys
vault kv get -format=json secret/${ENVIRONMENT}/api-keys | \
    jq -r '.data.data | {
        "apiVersion": "v1",
        "kind": "Secret",
        "metadata": {
            "name": "api-keys",
            "namespace": "'${APP_NAMESPACE}'"
        },
        "type": "Opaque",
        "data": {
            "plaid-key": (.plaid_key | @base64),
            "monitoring-key": (.monitoring_key | @base64)
        }
    }' | kubectl apply -f -

echo "Secrets applied successfully"