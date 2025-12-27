# Stripe Payment Integration System

A microservices-based payment processing system that integrates with Stripe for handling online payments. The system is built using Spring Boot and follows a distributed architecture pattern.

## Architecture Overview

This system consists of three main microservices:

1. **Eureka Service Registry** (Port: 8761) - Service discovery and registration
2. **Payment Processing Service** (Port: 8082) - Core payment transaction management
3. **Stripe Provider Service** (Port: 8083) - Stripe integration and webhook handling

## Features

- **Microservices Architecture**: Distributed system with service discovery
- **Stripe Integration**: Complete Stripe payment gateway integration
- **Transaction Management**: Full lifecycle payment transaction handling
- **Webhook Support**: Real-time payment status updates via Stripe webhooks
- **Database Persistence**: MySQL database for transaction storage
- **Multi-Environment Support**: Configurable for local, dev, and production environments
- **Circuit Breaker**: Resilience4j for fault tolerance
- **Distributed Tracing**: Micrometer tracing for monitoring
- **Async Processing**: Support for asynchronous payment operations

## Technology Stack

- **Java 17**
- **Spring Boot 3.4.2**
- **Spring Cloud 2024.0.1**
- **MySQL 8.0.33**
- **Stripe Java SDK 29.5.0**
- **Maven**
- **Lombok**
- **Gson**
- **ModelMapper**
- **Resilience4j**
- **Micrometer Tracing**

## Database Schema

The system uses a MySQL database with the following main tables:

- `Payment_Method` - Payment method types (APM, etc.)
- `Payment_Type` - Transaction types (SALE, etc.)
- `Provider` - Payment providers (STRIPE, etc.)
- `Transaction_Status` - Transaction statuses (CREATED, INITIATED, PENDING, SUCCESS, FAILED)
- `Transaction` - Main transaction records
- `Transaction_Log` - Transaction status change logs

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Stripe account with API keys
- Git

## Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd stripe-payment-integration
```

### 2. Database Setup

Execute the database scripts in order:

```bash
# Create database and tables
mysql -u root -p < db/ddl-script.sql

# Insert initial data
mysql -u root -p < db/dml-script.sql
```

### 3. Configuration

Configure the following properties in each service's `application-{profile}.properties`:

**Payment Processing Service:**
- Database connection details
- Stripe API keys (if needed)
- Service URLs

**Stripe Provider Service:**
- Stripe API keys (publishable and secret)
- Webhook endpoint configuration
- Service URLs

**Eureka Service Registry:**
- Service registry configuration

### 4. Build Services

Build each service individually:

```bash
# Build Eureka Service Registry
cd eureka-service-registry
mvn clean install

# Build Payment Processing Service
cd ../payment-processing-service
mvn clean install

# Build Stripe Provider Service
cd ../stripe-provider-service
mvn clean install
```

## Usage

### Starting the Services

Start the services in the following order:

1. **Eureka Service Registry**
```bash
cd eureka-service-registry
mvn spring-boot:run
```

2. **Payment Processing Service**
```bash
cd payment-processing-service
mvn spring-boot:run
```

3. **Stripe Provider Service**
```bash
cd stripe-provider-service
mvn spring-boot:run
```

### API Endpoints

#### Payment Processing Service (Port: 8082)

- `POST /v1/payments` - Create a new transaction
- `POST /v1/payments/{txnReference}/initiate` - Initiate a payment transaction
- `POST /v1/payments/notifications` - Process payment notifications

#### Stripe Provider Service (Port: 8083)

- `POST /payments` - Create a Stripe payment
- `GET /payments/{id}` - Retrieve payment details
- `POST /payments/{id}/expire` - Expire a payment
- `POST /v1/stripe/webhook` - Handle Stripe webhooks

#### Eureka Service Registry (Port: 8761)

- Dashboard available at: `http://localhost:8761`

### Environment Profiles

The system supports multiple environments:

- **local** (default): Local development
- **dev**: Development environment
- **prod**: Production environment

Activate profiles using:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## API Examples

### Create a Payment Transaction
```bash
curl -X POST http://localhost:8082/v1/payments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 123,
    "amount": 100.00,
    "currency": "USD",
    "paymentMethodId": 1,
    "providerId": 1,
    "paymentTypeId": 1
  }'
```

### Initiate a Payment
```bash
curl -X POST http://localhost:8082/v1/payments/{txnReference}/initiate \
  -H "Content-Type: application/json" \
  -d '{
    "returnUrl": "https://your-site.com/return",
    "cancelUrl": "https://your-site.com/cancel"
  }'
```

### Create Stripe Payment
```bash
curl -X POST http://localhost:8083/payments \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 10000,
    "currency": "usd",
    "paymentMethod": "pm_card_visa"
  }'
```

## Monitoring and Logging

- **Actuator Endpoints**: Health checks and metrics available at `/actuator`
- **Distributed Tracing**: Request tracing across microservices
- **Structured Logging**: Comprehensive logging with different levels
- **Circuit Breaker Monitoring**: Resilience4j circuit breaker status

## Security Considerations

- Stripe webhook signature verification
- API key management (use environment variables)
- Database connection security
- HTTPS configuration for production

## Development

### Running Tests
```bash
mvn test
```

### Code Style
- Uses Lombok for boilerplate code reduction
- Follows Spring Boot conventions
- Implements proper error handling and logging

### Adding New Payment Providers

1. Create a new provider service module
2. Implement the PaymentService interface
3. Add provider configuration
4. Update database with new provider
5. Register with Eureka




