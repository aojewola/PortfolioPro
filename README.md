# PortfolioPro

**PortfolioPro** is a reactive stock portfolio management application built with Spring Boot 3.4.0 and Java 23. It leverages Spring WebFlux for reactive programming and uses an in-memory H2 database configured with reactive streaming for data persistence.

## Features

- **Reactive Programming:** Built using Spring WebFlux for non-blocking, reactive workflows.
- **In-Memory Database:** H2 reactive database for fast and efficient in-memory data storage.
- **Portfolio Management:**
  - Add, update, and delete stocks.
  - View the current portfolio value.
  - Fetch the latest stock prices to calculate potential investments.
- **Swagger API Documentation:** Integrated Swagger UI for testing and exploring the APIs.

---

## Requirements

- **Java Version:** Java 23 is required to build and run the application.
- **Build Tool:** Maven (included in the project).

---

## Getting Started

### Clone the Repository
```bash
git clone https://github.com/aojewola/PortfolioPro.git
cd PortfolioPro
```

### Build the Application
Ensure you have Java 23 installed. Run the following command:
```bash
./mvnw clean package
```

### Run the Application
Start the application with:
```bash
java -jar target/PortfolioPro-1.0.0.jar
```

The application will start on the default port **8080**.

---

## API Endpoints

### Base URL
`http://localhost:8080`

### Endpoints

1. **Manage Stocks:**
   - **List All Stocks:** `GET /stocks`
   - **Add a Stock:** `POST /stocks`
   - **Update a Stock:** `PUT /stocks/{id}`
   - **Delete a Stock:** `DELETE /stocks/{id}`

2. **Portfolio Calculations:**
   - **Calculate Total Portfolio Value:** `GET /total-value`
   - **Fetch Current Stock Price:** `GET /stock-price?ticker={ticker}`
   - **Fetch Company Info:** `GET /company-info?ticker={ticker}`

3. **Swagger API Docs:**
   - API Docs: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
   - Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Database Configuration

The application uses an H2 in-memory database configured for reactive programming.


### Schema and Sample Data
The application auto-loads schema and sample data from `schema.sql`, which is located in the `resources` folder.

---

## Development

### Prerequisites
- **Java 23**
- **Maven**

### Running Locally
Use the Maven wrapper to start the application locally:
```bash
./mvnw spring-boot:run
```

### Testing
Run all tests using:
```bash
./mvnw test
```

---

## Deployment

To deploy the application, package it as a JAR file and run it on any server that supports Java 23. You can use Docker for containerized deployment if needed.

---

## Troubleshooting

1. **Java Version Error:** Ensure Java 23 is installed and added to your `PATH`.
2. **Swagger UI Not Found:**
   - Check if the application is running on the correct port.
   - Ensure dependencies for `springdoc-openapi-webflux-ui` are included in `pom.xml`.

---

## Contributing

Contributions are welcome! Please follow these steps:
1. Fork the repository.
2. Create a feature branch (`git checkout -b feature-branch-name`).
3. Commit your changes (`git commit -m "Add some feature"`).
4. Push to the branch (`git push origin feature-branch-name`).
5. Open a pull request.

---

## License

This project is licensed under the MIT License. See the LICENSE file for details.

--- 

## Contact

For questions or support, please contact **[Asimi Ojewola]** at **[asimi.ojewola@gmail.com]**.
