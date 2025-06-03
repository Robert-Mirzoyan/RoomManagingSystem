# Datasource vs DriverManager in JDBC

## DriverManager
- A basic way to connect to a database using hardcoded credentials and connection strings.
- Complicates the application performance as the connections are created/closed in java classes.
- Does not support connection pooling.
- Suitable for simple demos or small apps.

## Datasource
- Improves application performance as connections are not created/closed within a class, they are managed by the application server and can be fetched while at runtime.
- Provides a facility creating a pool of connections
- Allows external configuration of credentials, URLs, etc.
- Helpful for enterprise applications
