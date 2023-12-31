### Demo
You can view a [demo](https://www.loom.com/share/99a3bd4719f34863a318b616e52ee10f) of some features here.

### How to run on your machine

1. Clone the project.

2. Run `docker build -t order-management-system .` to build `Docker` image.

3. Run `docker run -p 8080:8080 order-management-system` to run server.


### API usage

All the GraphQL API endpoints are exposed in `/graphql`. 
Some endpoints require `JWT` token and a particular role for authorization.

1. Create a user with the following example graphQL body:
```graphql
mutation {
    createUser(input:{
        name: "admin"
        email:"kwakubiney@gmail.com"
        password:"admin"
        role: ADMIN
    }){
        email
        role
        name
    }
}
```   

2. Login using the following example graphQL body:
```graphql
query {
    loginUser(input: {password: "admin", 
        email: "kwakubiney@gmail.com"})
}
```   

3. After retrieving the token, refer to [the schema](https://github.com/kwakubiney/order-management-system/tree/main/src/main/resources/graphql/schema.graphqls) to determine how to make certain queries and mutations.

4. For authenticated requests, send request with header: `Authorization: Bearer {TOKEN}`

5. Visit [this link](http://localhost:8080/graphiql) for a beautiful interface to make queries and mutations.

### Additional API usage guidelines

For clarity when reading [the schema](https://github.com/kwakubiney/order-management-system/tree/main/src/main/resources/graphql/schema.graphqls):
1. Endpoints annotated with `#[ADMIN_AUTH_REQUIRED]` can only be accessed by logged in users with `ADMIN` role. 

2. Endpoints annotated with `#[NO_AUTH_REQUIRED]` can be accessed without logging in.

3. Endpoints annotated with `#[ANY_AUTH_REQUIRED]` can only be accessed by logged in users with either `ADMIN` or `NORMAL` role.