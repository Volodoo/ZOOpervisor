# ZOOpervisor

Internal system for zoo management.

## How to run the project

### Prerequisites
- JDK 21
- Docker
- Maven

### Run from terminal

```bash
docker compose up
```

Wait until you see a line with message containing "ready for connections":
```bash
... /usr/sbin/mysqld: ready for connections. ...
```

Then open new terminal/tab and run the following command to start the app:

```bash
mvn clean javafx:run
```

After exiting the app, you may or may not choose to delete the Docker container with
```bash
docker compose down
```

### Run from IntelliJ IDEA

We assume you already have the project opened in IntelliJ IDEA.

Open `docker-compose.yml` file and click on the first green play button on the left side of the file.

Then at the bottom of the screen you should click on the `mysql` container to see the terminal with the output of the docker container.
Wait until you see a line with message containing "ready for connections".

Then open `IDELauncher` class and run it.

To run tests, you first have to open `docker-compose.test.yml` file and click on the first green play button on the left side of the file.

## Login credentials

The app has a simple login form. 
By default, in `init.sql` there is an insert to create initial users.
Each user has email and password, which is used to log in to the application.
Password is always given users email with 1 added at the end, for example:
> User: Marek Tóth has \
> Email: marek.toth@zoo.sk \
> Password: marek.toth@zoo.sk1


> There is a helper program in `src/main/java/sk/upjs/paz/security/InitAdminGenerator.java` that you can run to create an admin user with specified credentials.
