## Description
To manage files we first need to create and manage users.

## Implementation Notes
- A user has an id, username, password, created_at, updated_at and a role (admin or user).
- It's a JPA entity with SQLite.
- To create a user the password should be hashed with bcrypt (cost 12).
- There also should be a function to check if there is an admin or not.

## Acceptance Criteria
- [x] User entity persists to SQLite
- [x] UserService.hasAdmin() returns true when an admin exists
- [x] UserService.createUser() hashes password and saves user
- [x] Duplicate username throws USER_ALREADY_EXISTS exception