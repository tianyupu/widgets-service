# widgets-service

A simple REST service written with Spring Boot.


## Getting started

To run the application, type `mvn spring-boot:run`. You can then use curl
or Postman (or some other client) to send requests.


## Available methods

* `GET /widgets`
* `GET /widget/{id}`
* `POST /widget`
* `PUT /widget/{id}`
* `DELETE /widget/{id}`

All endpoints output `application/json`. Where needed, endpoints also accept
`application/json`.


## Running tests

Unit and integration tests can be run with `mvn test`.