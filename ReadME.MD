### How to run
1. Run `./gradlew build && java -jar build/libs/order-management-system.jar` to build JAR files.
2. Run `docker build --build-arg JAR_FILE=build/libs/\*.jar -t order-management-system .` to build `Docker` image.
3. Run `docker run -p 8080:8080 order-management-system` to run server.

### API usage

All the GraphQL API is exposed in `/graphql`. 
Some endpoints require `JWT` token and a particular role for authorization.

1. Create a user with the following example graphQL body:
```graphql
mutation {
    createUser(input: {name: "admin", password: "admin", email: "kwakubiney@gmail.com", role: ADMIN})
}
```   

2. Login using the following example graphQL body:
```graphql
query {
    loginUser(input: {password: "admin", email: "kwakubiney@gmail.com"})
}
```   

3. After retrieving the token, refer to [the schema](https://github.com/kwakubiney/order-management-system/tree/main/src/main/resources/graphql) to determine how to make certain queries and mutations.

4. For authenticated requests, send request with header: `Authorization: Bearer {TOKEN}`

5. Visit [this link](http://localhost:8080/graphiql) for a beautiful interface to make queries and mutations.

### Additional API usage guidelines

For clarity when reading [the schema](https://github.com/kwakubiney/order-management-system/tree/main/src/main/resources/graphql):
1. Endpoints annotated with ` #[ADMIN_AUTH_REQUIRED]` can only be accessed by logged in users with `ADMIN` role. 
2. Endpoints annotated with ` #[NO_AUTH_REQUIRED]` can only be accessed without logging in.
3.  Endpoints annotated with ` #[ANY_AUTH_REQUIRED]` can only be accessed by logged in users with either `ADMIN` or `NORMAL` role.
4. with header: `Authorization: Bearer TOKEN`.