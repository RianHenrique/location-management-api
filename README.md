# Location Management API

## Overview

This is a Spring Boot application that provides a RESTful API for managing locations. It supports basic CRUD operations, pagination, and sorting. The API is documented using Swagger. Additionally, the application includes a logging system to help track operations and troubleshoot issues.

## Features

- **Create a Location**: Adds a new location.
- **Retrieve a Location by ID**: Fetches details of a location using its ID.
- **Retrieve All Locations**: Lists all locations with pagination and sorting by creation date.
- **Update a Location**: Modifies details of an existing location by ID.
- **Delete a Location**: Removes a location by ID.

## Getting Started

### Prerequisites

- JDK 17 or higher
- Maven
- PostgreSQL (or another compatible database)

### Configuration

Before running the application, configure your database settings in `src/main/resources/application.properties`:

```properties
spring.application.name=location-management-api
spring.datasource.url=jdbc:postgresql://localhost:5432/location_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
server.error.include-message=always
server.error.include-binding-errors=always
```
## Running the Application

1. **Build the Project**: Use Maven to build the project.

    ```bash
    mvn clean install
    ```

2. **Run the Application**: Start the application using the Maven Spring Boot plugin.

    ```bash
    mvn spring-boot:run
    ```

   The application will start on `http://localhost:8080`.

## Testing

1. **Run Unit Tests**: Execute the unit tests to verify that the application logic works as expected.

    ```bash
    mvn test
    ```

2. **Run Integration Tests**: Integration tests ensure that the application components work together correctly.

    ```bash
    mvn verify
    ```

## API Documentation

The API is documented using Swagger. To view the API documentation, ensure that the project is running and visit the following URL in your web browser:
```Swagger UI
http://localhost:8080/swagger-ui.html
```
## Logging

The application includes a logging system to help track its operations and troubleshoot issues. The logs are configured to record information and error messages.

Logs are written to a file named `app.log` located in the root directory of the project. The logging configuration is set to capture:
- **INFO** level logs for general information about the application's operation.
- **ERROR** level logs for any issues or exceptions that occur.

You can adjust the logging configuration in the `application.properties` file to suit your needs.

## Example Requests

### Create Location

**Endpoint**: `POST /api/locations`

**Request Body**:
```json
{
  "name": "Example Location",
  "neighborhood": "Downtown",
  "city": "Metropolis",
  "state": "NY"
}
```

**Response Body**:
```json
{
  "id": 1,
  "name": "Example Location",
  "neighborhood": "Downtown",
  "city": "Metropolis",
  "state": "NY",
  "createdAt": "2024-08-15T12:34:56",
  "updatedAt": "2024-08-15T12:34:56"
}
```

### Get Location by ID

**Endpoint**: `GET /api/locations/{id}`

**Response Body**:
```json
{
  "id": 1,
  "name": "Example Location",
  "neighborhood": "Downtown",
  "city": "Metropolis",
  "state": "NY",
  "createdAt": "2024-08-15T12:34:56",
  "updatedAt": "2024-08-15T12:34:56"
}
```

### Get All Locations

**Endpoint:** `GET /api/locations`

**Query Parameters:**
```
- page (integer).
- size (integer).
- sort (array[string]): Default value: createdAt,asc.
```
**Response:**

- **Successful Response (200 OK):**

  ```json
  {
    "totalElements": 0,
    "totalPages": 0,
    "size": 0,
    "content": [
      {
        "id": 0,
        "name": "string",
        "neighborhood": "string",
        "city": "string",
        "state": "string",
        "createdAt": "2024-08-15T16:32:55.272Z",
        "updatedAt": "2024-08-15T16:32:55.272Z"
      }
    ],
    "number": 0,
    "sort": [
      {
        "direction": "string",
        "nullHandling": "string",
        "ascending": true,
        "property": "string",
        "ignoreCase": true
      }
    ],
    "first": true,
    "last": true,
    "numberOfElements": 0,
    "pageable": {
      "offset": 0,
      "sort": [
        {
          "direction": "string",
          "nullHandling": "string",
          "ascending": true,
          "property": "string",
          "ignoreCase": true
        }
      ],
      "unpaged": true,
      "pageNumber": 0,
      "pageSize": 0,
      "paged": true
    },
    "empty": true
  }
  ```

- **Error Response ( 400 Bad Request):**

  ```json
  {
    "timestamp": "2024-08-15T16:32:55.272Z",
    "status": 400,
    "error": "Bad Request",
    "message": "The request parameters are invalid.",
    "path": "/api/locations"
  }
  ```

- **Description:** This endpoint retrieves a paginated list of locations, ordered by creation date. You can specify the `page` and `size` query parameters to control pagination.




### Update Location

**Endpoint**: `PUT /api/locations/{id}`

**Request Body**:
```json
{
  "name": "Updated Location",
  "neighborhood": "Uptown",
  "city": "Metropolis",
  "state": "NY"
}
```

**Response Body**:
```json
{
  "id": 1,
  "name": "Updated Location",
  "neighborhood": "Uptown",
  "city": "Metropolis",
  "state": "NY",
  "createdAt": "2024-08-15T12:34:56",
  "updatedAt": "2024-08-16T12:34:56"
}
```

### Delete Location

**Endpoint**: `DELETE /api/locations/{id}`

**Request Body**:
```String
Location with ID {id} deleted successfully!
```




