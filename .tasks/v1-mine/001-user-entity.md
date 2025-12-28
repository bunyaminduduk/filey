## Description
To manage files, we first need to create the users model. Create the user data model, repository and service layer.

## Data Model
```sqlite
CREATE TABLE users
(
    id         TEXT PRIMARY KEY,
    username   TEXT NOT NULL UNIQUE,
    password   TEXT NOT NULL,
    role       TEXT NOT NULL CHECK (role IN ('admin', 'user')),
    created_at TEXT NOT NULL DEFAULT (datetime('now')),
    updated_at TEXT NOT NULL DEFAULT (datetime('now'))
);
```

## Implementation Notes
- JPA entity with SQLite
- UserRepository: findByUsername, existsByRole
- UserService: isFirstRun(), createUser()
- PasswordService: hashPassword()
- Password hashing with bcrypt (cost 12)


## Acceptance Criteria
- [ ] User entity persists to SQLite
- [ ] UserService.isFirstRun() returns true when no admin exists
- [ ] UserService.createUser() hashes password and saves user
- [ ] Duplicate username throws exception