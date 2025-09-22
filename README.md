# Trade Store
Trade Store is a Spring Boot application that ingests trades via RabbitMQ, enforces versioning and maturity validations, and stores them in both SQL and NoSQL databases. It includes automated expiry handling, comprehensive TDD tests, and a CI/CD pipeline with vulnerability scanning

# Assumptions for design and development
1) Each trade is uniquely identified by trade id + version and version number strictly increase for subsequent revisions of a trade
1) Trades will be organized and stored by trade id + version  
1) Streaming Platform - Trades will be ingested via RabbitMQ
2) Data Storage - In Memory SQL (H2) and NoSQl (Hazelcast) databases used


# Trade Store Sequence Diagram

![Trade Store Sequence Diagram](./tradestoresequence.png)

# Trade Store Class Diagram

![Trade Store Sequence Diagram](./tradestoreclassdiagram.png)
