# StockSim Live - Real-time High-Frequency Trading Simulator

**StockSim Live** is a robust, microservices-based stock market simulator that mimics a high-frequency trading environment. It ingests real-time market data, bridges it via Kafka to WebSockets, and streams live price updates to a modern Next.js dashboard.

![Microservices Architecture](https://img.shields.io/badge/Architecture-Microservices-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![Kafka](https://img.shields.io/badge/Kafka-Event%20Streaming-black)
![Next.js](https://img.shields.io/badge/Next.js-14-white)

## 🏗️ Architecture

The system is built on a distributed microservices event-driven architecture:

1.  **Market Data Service**:
    *   Fetches real seed prices from external APIs (Finnhub).
    *   Simulates realistic HFT micro-movements (random walk).
    *   Publishes ticks to **Apache Kafka** (`market.prices`).
    *   Consumes its own stream to broadcast via **WebSockets (STOMP)**.
2.  **API Gateway**:
    *   Central entry point (Spring Cloud Gateway).
    *   Handles routing (`/api/**`, `/ws/**`) and CORS.
3.  **Discovery Server**:
    *   Service Registry (Eureka) for dynamic load balancing.
4.  **Frontend**:
    *   Next.js 14 App Router + Tailwind CSS.
    *   Connects to WebSocket to render live "Red/Green" price cards.
5.  **Infrastructure**:
    *   **Kafka + Zookeeper**: Event backbone using Confluent images.

## 🚀 Tech Stack

*   **Backend**: Java 21, Spring Boot 3, Spring Cloud (Gateway, Eureka), Spring Kafka.
*   **Messaging**: Apache Kafka, WebSocket (STOMP/SockJS).
*   **Frontend**: TypeScript, Next.js, React, Tailwind CSS, Recharts.
*   **DevOps**: Docker Compose, Maven.

## 🛠️ Getting Started

### Prerequisites

*   **Java 21 SDK**
*   **Node.js 20+**
*   **Docker Desktop** (running)
*   **Maven**

### 1. Start Infrastructure
Spin up Kafka and Zookeeper:
```bash
docker-compose up -d
```

### 2. Start Microservices
Run the following Spring Boot apps (order matters for stability, though Eureka handles eventual consistency):

1.  **Discovery Server** (`discovery-server`) -> http://localhost:8761
2.  **API Gateway** (`api-gateway`) -> http://localhost:8080
3.  **Market Data Service** (`market-data-service`) -> http://localhost:8081

*(You can run them via your IDE or `mvn spring-boot:run -pl <module-name>`)*

### 3. Start Frontend
Navigate to the frontend directory and launch the dashboard:
```bash
cd frontend
npm install
npm run dev
```

### 4. Live Dashboard
Open **http://localhost:3000**.
You should see:
*   "Live Feed" indicator (Green).
*   Real-time flashing price cards for AAPL, GOOGL, MSFT, etc.



## 🔐 Security Note
The project uses a default API token for Finnhub for demonstration purposes. In a production environment, this should be injected via the `FINNHUB_TOKEN` environment variable.

---
*Built for Portfolio Demonstration*
