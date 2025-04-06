# Plaid Integration for Fraud Detection System

This module integrates Plaid's financial data APIs with the fraud detection system, enabling real-time fraud analysis on banking transactions.

## Features

- Connect to bank accounts using Plaid Link
- Retrieve account information
- Fetch transactions for fraud analysis
- Convert Plaid data to the core fraud detection system's format

## Setup

### Prerequisites

- Plaid developer account (sign up at [plaid.com/developers](https://plaid.com/developers/))
- Client ID and Secret from the Plaid Dashboard

### Configuration

Set the following environment variables or update `application.properties`:

```
PLAID_CLIENT_ID=your_client_id
PLAID_SECRET=your_secret
```

Additional configuration options in `application.properties`:

```properties
# Plaid API configuration
plaid.client-id=${PLAID_CLIENT_ID:your_client_id_here}
plaid.secret=${PLAID_SECRET:your_secret_here}
plaid.environment=sandbox       # Options: sandbox, development, production
plaid.client-name=Fraud Detection System
plaid.country-codes=US          # Comma-separated list for multiple countries
plaid.language=en
```

## Usage

### Creating a Link Token

A Link token is required to initialize Plaid Link, which is used to securely connect to bank accounts:

```
POST /api/plaid/create-link-token?userId=user123
```

### Exchanging Tokens

After completing the Link flow, exchange the public token for an access token:

```
POST /api/plaid/exchange-token?publicToken=public-token-from-plaid
```

### Retrieving Account Information

Get account information for connected accounts:

```
GET /api/plaid/accounts?accessToken=access-token
```

### Retrieving Transactions

Get transactions for a specific date range:

```
GET /api/plaid/transactions?accessToken=access-token&startDate=2023-01-01&endDate=2023-01-31
```

### Fraud Analysis on Plaid Transactions

Analyze transactions for fraud detection:

```
GET /api/plaid/fraud-analysis/transactions?accessToken=access-token&startDate=2023-01-01&endDate=2023-01-31
```

## Security Considerations

- Never expose your Plaid Secret on the client side
- Store access tokens securely
- Use environment variables for sensitive credentials
- Consider encrypting access tokens in your database

## Integration with Core Fraud Detection

This module adapts Plaid's transaction data to the core fraud detection system's model for seamless analysis.

The `PlaidTransactionAdapter` handles the mapping of Plaid-specific data to the fraud detection Transaction model, 
allowing existing rules and detection systems to work with minimal changes.

## References

- [Plaid API Documentation](https://plaid.com/docs/api/)
- [Plaid Java Client](https://github.com/plaid/plaid-java)