## Description
Managing errors between the API and the website. The errors should be easily checked at the frontend and also easily being sent from the API.

Additionally, auto-generate TypeScript API clients from OpenAPI spec so the frontend stays in sync with the backend.

## Implementation Notes
The body should look something like this when an error is thrown:

```json
{
  "code": "FILE_NOT_FOUND",
  "message": "The file is not found",
  "status": 404
}
```

It is important to keep in mind that Tanstack Query is being used in the frontend in combination with Tanstack Router.

## Acceptance Criteria
- [x] The frontend can pick up easily if an error is thrown and use the message/code to notify the user.
- [x] The API can easily throw an error with custom code and message
- [x] OpenAPI spec is auto-generated from Spring Boot controllers
- [x] TypeScript API clients with TanStack Query hooks are auto-generated using Orval