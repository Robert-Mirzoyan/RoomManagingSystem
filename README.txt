The database is initialized automatically using the `init.sql` script located in the `./db` folder. The script creates all required tables with appropriate relationships.

How to Run

1. Clone the project and go to the folder.
2. Copy the env file and change values if needed
3. Start the container: docker-compose --env-file .env up -d
4. Connect with Beaver with Host: localhost, Port: 5432, User: rob, Password: 12345678, DB name: mydb
5. To stop: docker-compose down