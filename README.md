# AI-Powered Restaurant Management System

### A modern, microservices-based platform for restaurant management, developed as a Bachelor's Thesis project.

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Angular](https://img.shields.io/badge/Angular-18-red.svg)](https://angular.io/)
[![Docker](https://img.shields.io/badge/Docker-blue.svg)](https://www.docker.com/)

This project is a complete, full-stack software solution that demonstrates the application of a microservices architecture to manage restaurant operations. The system integrates artificial intelligence agents to automate key tasks such as processing orders and reservations via voice interactions.

## System Architecture

The diagram below provides a high-level overview of the system's main components and their interactions.

```mermaid
graph TD;
    subgraph "Users"
        client[Web Client];
        admin[Administrator];
    end

    subgraph "Frontend Applications (Vercel)"
        app_client["Client App (Angular)"];
        app_admin["Admin Panel (Angular)"];
    end

    subgraph "Artificial Intelligence"
      n8n["n8n Cloud Platform"];
    end

    subgraph "Backend Microservices (Railway)"
        direction TB
        ms_auth["Auth Service"];
        ms_products["Products & Orders Service"];
        ms_reservation["Reservation Service"];
        ms_gallery["Gallery Service"];
        ms_review["Review Service"];
        ms_contact["Contact Service"];
        ms_logs["Logs Service (RabbitMQ)"];
    end
    
    client -- "Interacts with" --> app_client;
    admin -- "Interacts with" --> app_admin;

    app_client -- "API Call (REST)" --> ms_products;
    app_client --> ms_reservation;
    app_client --> ms_gallery;
    app_client --> ms_review;
    app_client --> ms_contact;
    
    app_admin --> ms_auth;
    app_admin --> ms_products;
    app_admin --> ms_reservation;
    app_admin --> ms_gallery;
    app_admin --> ms_review;
    app_admin --> ms_contact;
    app_admin --> ms_logs;
    
    app_admin -- "Chat Command" --> n8n;
    
    n8n --> ms_products;
    n8n --> ms_reservation;
    n8n --> ms_contact;

    ms_products -- "Publish Log" --> ms_logs;
    ms_reservation -- "Publish Log" --> ms_logs;
    ms_auth -- "Publish Log" --> ms_logs;
```

## Key Features

### Client Application (Public)
- Interactive menu viewing with filtering and search capabilities.
- Reservation creation through an intuitive form.
- Restaurant photo gallery viewer.
- Submission of reviews and feedback.
- **AI Integration:** Ability to request a phone call to place an order or make a reservation, handled by a conversational AI agent.

### Admin Panel
- Secure authentication based on JSON Web Tokens (JWT).
- Analytical dashboard with real-time statistics and charts.
- Full CRUD (Create, Read, Update, Delete) management for products, categories, and ingredients.
- Centralized management for orders and reservations.
- **AI Integration:** An administrative chatbot that can execute tasks (e.g., generate reports) based on natural language commands.

## Technology Stack

### Backend
- **Java 17** & **Spring Boot 3**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** & **Hibernate**
- **Microservices Architecture** (7 services)
- **Lombok**

### Frontend
- **Angular 18+** & **TypeScript**
- **RxJS** for reactive programming and state management.
- **Angular Material** for UI components.

### Databases & Messaging
- **MySQL** (using the "Database per Service" pattern)
- **RabbitMQ** (for asynchronous logging)

### AI & Automation
- **n8n.io** (workflow orchestration platform)
- **ElevenLabs API** (for conversational voice AI)
- **Google Gemini API** (for Natural Language Understanding)

### DevOps & Hosting
- **Docker** & **Docker Compose** (for containerization and local execution)
- **Railway** (for hosting backend microservices)
- **Vercel** (for hosting frontend applications)

## Project Structure

The repository is organized as a monorepo, with separate folders for each component of the system:
```
/
├── authentication-service/   # Authentication Microservice
├── products-service/         # Products & Orders Microservice
├── reservation-service/      # Reservation Microservice
├── ... (other services) ...
├── frontend-client-app/      # Angular application for clients
└── frontend-admin-app/       # Angular application for the admin panel
```

## Getting Started Locally

### Prerequisites
- Java JDK 17+
- Maven 3.8+
- Node.js 18+
- Docker & Docker Compose
- A local MySQL Server instance

### Installation Steps
1.  **Clone the repository:**
    ```bash
    git clone [YOUR_REPOSITORY_URL]
    cd [repository-name]
    ```
2.  **Backend Setup:**
    - Ensure your local MySQL server is running.
    - Create the necessary database schemas for each microservice.
    - Update the `application.properties` files in each microservice with your local database credentials.
3.  **Run the Backend:**
    - (Recommended) Use the provided `docker-compose.yml` file to start all microservices with a single command.
    - Alternatively, navigate to each microservice folder and run `mvn spring-boot:run`.
4.  **Run the Frontend:**
    - Navigate to one of the frontend application folders (e.g., `frontend-admin-app`).
    - Run `npm install` to install dependencies.
    - Run `ng serve` to start the development server. The application will be available at `http://localhost:4200/`.
    - Repeat the steps for the other frontend application.
