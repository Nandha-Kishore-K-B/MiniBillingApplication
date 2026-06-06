# Mini SaaS Billing API

A robust, multi-tenant backend REST API built with Spring Boot. This application manages invoices for multiple businesses securely, ensuring strict data isolation, role-based access control (RBAC), and automated asynchronous PDF generation.

## 🚀 Features

* **True Multi-Tenancy:** Utilizes Hibernate Filters to intercept database queries and ensure users can only access data belonging to their specific tenant (company).
* **Role-Based Access Control (RBAC):** Mock JWT authentication supporting `ADMIN`, `MANAGER`, and `VIEWER` roles to secure endpoints via `@PreAuthorize`.
* **Asynchronous PDF Generation:** Uses Spring `@Async` and Thymeleaf templates to generate invoice PDFs in the background without blocking the main API thread.
* **Automated Database Migrations:** Flyway tracks and applies schema changes and seed data automatically on application startup.
* **Global Exception Handling:** Clean, professional JSON error responses masking internal database errors (e.g., converting unauthorized access attempts to 404 Not Found for enhanced security).

## 🛠️ Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.x
* **Security:** Spring Security & Custom JWT Filter
* **Database:** PostgreSQL (via Docker)
* **ORM:** Spring Data JPA & Hibernate
* **Migrations:** Flyway
* **Templating:** Thymeleaf (for PDF HTML rendering)

## 📦 Prerequisites

* **Java 21+** installed
* **Docker** installed and running
* **Maven** (included via wrapper)

## ⚙️ Getting Started

### 1. Start the Database
Spin up the local PostgreSQL container using Docker:
```bash
docker run --name mini-saas-db -e POSTGRES_USER=saas_user -e POSTGRES_PASSWORD=saas_pass -e POSTGRES_DB=mini_db -p 5432:5432 -d postgres:15-alpine
```
### 2. Run the Application
Start the Spring Boot application using the Maven wrapper. Flyway will automatically build the tables and insert the seed data.
```bash
# Mac/Linux
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```
### 🔐 Authentication (Testing via Postman)
This API uses a mock JWT system for local development. To authenticate, include an Authorization header in your HTTP requests formatted as Bearer {tenantId}_{role}_{username}.

**Available Test Accounts:**

**Zoho Corp Admin**: Bearer zoho-corp_admin_johndoe

**Acme Inc Admin:** Bearer acme-inc_admin_wilecoyote

**Acme Inc Viewer:** Bearer acme-inc_viewer_roadrunner

### API Endpoints
Base URL: http://localhost:8080/api/v1/invoices

| Method | Endpoint | Description | Required Role |
| :--- | :--- | :--- | :--- |
| **POST** | `/` | Create a new invoice and trigger PDF generation. | `ADMIN`, `MANAGER` |
| **GET** | `/{id}` | Fetch a specific invoice. | `ADMIN`, `MANAGER`, `VIEWER` |
| **PUT** | `/{id}/pay` | Update an invoice status from DRAFT to PAID. | `ADMIN`, `MANAGER` |
| **GET** | `/reports/revenue` | Aggregate monthly revenue for the current tenant (PAID invoices only). | `ADMIN`, `MANAGER` |
| **GET** | `/{id}/pdf` | Download the generated PDF file for a specific invoice. | `ADMIN`, `MANAGER`, `VIEWER` |

Example Payload: Create Invoice (POST /)
```bash
JSON
{
    "customerId": 1,
    "issueDate": "2026-06-05",
    "totalAmount": 105.00,
    "lineItems": [
    {
      "productId": 1,
      "quantity": 1,
      "subtotal": 45.00
    },
    {
      "productId": 2,
      "quantity": 2,
      "subtotal": 60.00
    }
  ]
}
```
### 🛡️ Architecture Highlights
**Tenant Isolation**: A @Aspect component intercepts all service calls, extracting the tenant ID from the SecurityContext and applying a Hibernate Session filter before any SQL is executed.

**The "N+1" Solution:** Repository queries utilize JOIN FETCH to eagerly load nested Line Items, preventing LazyInitializationException during background PDF processing.