# NeoBanking Backend Platform - Scalable Microservices Architecture
#### Event-Driven Digital Banking Backend with Spring Boot, gRPC, Kafka, PostgreSQL, MongoDB, and Docker


# Introduction

NeoBanking Backend Platform is a production-like digital banking system built on a scalable microservices architecture. It demonstrates how modern banks handle authentication, account management, transactions, notifications, and audits in a secure, event-driven, and distributed environment.

The project integrates Spring Boot, gRPC, Kafka, PostgreSQL, MongoDB, and Docker to simulate real-world transaction workflows, inter-service communication, and asynchronous event processing — laying the foundation for resilient, high-performance financial platforms.


# System Architecture

The following diagram illustrates the overall architecture of the NeoBank backend, showcasing how different services interact synchronously (gRPC/REST) and asynchronously (Kafka events).

![Neobank System Architecture](https://github.com/AyushVarshney1/NeoBanking-Backend-Platform/blob/7dd1c1792fad89b630b16aedfecc13b1377642a9/Neobank%20Architecture%20Animation.gif)


The architecture follows a **domain-driven microservices design**, where each service handles a specific banking domain.  
- **Auth Service** → Manages user authentication & authorization (role-based access).  
- **Account Service** → Handles account creation, account management, and KYC updates.  
- **Transaction Service** → Processes deposits, withdrawals, and fund transfers.  
- **Notification Service** → Sends real-time email alerts on important events.  
- **Audit Service** → Maintains immutable logs of all transactions and user actions for compliance.  

Databases are decoupled for reliability:  
- **PostgreSQL** → Used by most services for structured, transactional data.  
- **MongoDB** → Used by the Notification Service for flexible, document-based email records.  

All services and databases are **containerized with Docker** and communicate inside an internal Docker network.  
The **API Gateway** provides a unified entry point, request routing, and the foundation for future load balancing.  


# Tech Stack
- **Backend:** Java, Spring Boot, gRPC
- **Messaging:** Apache Kafka
- **Databases:** PostgreSQL, MongoDB
- **API Gateway:** Spring Cloud Gateway
- **Containerization:** Docker


