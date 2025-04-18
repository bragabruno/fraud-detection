#!/bin/bash

# Exit on error
set -e

# Set default values if not provided
: ${POSTGRES_USER:="postgres"}
: ${POSTGRES_MULTIPLE_DATABASES:=""}

function create_user_and_database() {
    local database=$1
    if [ -z "$database" ]; then
        echo "Error: Database name cannot be empty"
        return 1
    fi

    echo "  Creating database '$database'"
    if psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" -lqt | cut -d \| -f 1 | grep -qw "$database"; then
        echo "  Database '$database' already exists, skipping..."
    else
        psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
            CREATE DATABASE $database;
            GRANT ALL PRIVILEGES ON DATABASE $database TO $POSTGRES_USER;
EOSQL
        echo "  Database '$database' created successfully"
    fi
}

# Check if POSTGRES_USER has required permissions
if ! psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" -c '\l' >/dev/null 2>&1; then
    echo "Error: Unable to connect to PostgreSQL with user '$POSTGRES_USER'"
    echo "Please ensure:"
    echo "1. PostgreSQL is running"
    echo "2. POSTGRES_USER has the correct permissions"
    echo "3. PostgreSQL connection settings are correct"
    exit 1
fi

if [ -z "$POSTGRES_MULTIPLE_DATABASES" ]; then
    echo "Warning: No databases specified in POSTGRES_MULTIPLE_DATABASES"
    echo "Usage: POSTGRES_MULTIPLE_DATABASES=\"db1,db2,db3\" $0"
    exit 0
else
    echo "Multiple database creation requested: $POSTGRES_MULTIPLE_DATABASES"
    for db in $(echo $POSTGRES_MULTIPLE_DATABASES | tr ',' ' '); do
        create_user_and_database "$db"
    done
    echo "Multiple databases creation completed"
fi
