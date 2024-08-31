# Token Auth Starter

This is a basic starter template for token based authentication setup using **Spring Boot** for backend and **Next JS** for frontend. The authentication is based on the **Token based Authentication** setup where **JWT** token is used with support of **Refresh Token** (_token expiry can be configured in the **application.properties** file_). The backend is configured with **REST** configuration to enable authentication through api calls. Frontend uses **NextAuth JS** for managing the authentication and is configured with **Refresh Token Rotation** mechanism.

---

#### Backend

Uses two databases, **MySQL** and **H2**
**MySQL DB** is used for storing the persistent data and **H2 DB** is an in memory DB which is used for storing the assigned jwt token for each user
**Note:** _H2 DB doesn't require any other configuration, it will automatically start along with the backend server and is **volatile**._
Current user configuration doesn't consider role based usecase

- update the **application.properties** file

```
spring.application.name=backend  # replace with your preferred application name

# MySQL DB setup
spring.datasource.persistent.url=jdbc:mysql://localhost:3306/auth
spring.datasource.persistent.username=anandup  # replace with your mysql username
spring.datasource.persistent.password=password # replace with your mysql password
spring.datasource.persistent.driver-class-name=com.mysql.cj.jdbc.Driver

# H2 DB setup
spring.datasource.inmemory.url=jdbc:h2:mem:tokens
spring.datasource.inmemory.driverClassName=org.h2.Driver
spring.datasource.inmemory.username=sa  # Default username
spring.datasource.inmemory.password=    # leave blank

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

#spring.jpa.open-in-view=false

spring.jpa.status=update  # DDL generation setting
inmemory.max.entry=100    # maximum user active user limit

jwt.expiry=86400000             # JWT token expiration in ms (1 day)
refreshToken.expiry=2628000000  # Refresh Token Expiration in ms (1 month)
```

- Starting the server

```
./mvnw spring-boot:run
```

| Endpoint                     | Purpose                                 |
| ---------------------------- | --------------------------------------- |
| /api/auth/register           | For creating a new user                 |
| /api/auth/login              | Sign in                                 |
| /api/auth/refreshToken       | For generating a new JWT token          |
| /api/auth/authorizationCheck | Checking if user is authenticated       |
| /api/auth/logout             | Sign out                                |
| /api/user/test               | Test Endpoint (requires authentication) |

---

#### Frontend

- add a **.env** file to the home directory (frontend)

```
NEXTAUTH_URL=http://localhost:3000  # Replace with your frontend server endpoint

NEXTAUTH_SECRET=Some-Secret-value   # Replace with your secret value

NEXT_PUBLIC_BACKEND_ENDPOINT=http://localhost:8080  # Replace with your backend server endpoint

TOKEN_REFRESH_RATE=86390000   # Token refresh rate in ms (23.5 hrs)
```

**Note:** For creating a secure secret value, use this command on the terminal

```
openssl rand -base64 32
```

- Install the packages

```
pnpm install
```

- Starting the server

```
pnpm dev
```

| Endpoint | Purpose         |
| -------- | --------------- |
| /        | Login Page      |
| /signup  | Sign up Page    |
| /user    | Protected Route |

---
