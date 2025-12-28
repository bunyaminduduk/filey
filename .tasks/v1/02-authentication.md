# Authentication

## What it does

Secure login system using HTTP-only cookies. Java acts as a gatekeeper, validating sessions before serving any content.

## Authentication Flow

### Login

```
┌──────────┐   POST /api/auth/login     ┌──────────┐
│  React   │  ─────────────────────▶    │   Java   │
│  (login  │   { username, password }   │  Spring  │
│   form)  │  ◀─────────────────────    │          │
└──────────┘   Set-Cookie: session=abc  └──────────┘
               (HTTP-only, Secure)
```

1. User submits username + password
2. Server validates credentials (password hashed with bcrypt)
3. On success: Create session, set HTTP-only cookie, return user info
4. On failure: Return 401 with error message

### Protected Routes

```
Browser requests /dashboard
         │
         ▼
┌─────────────────────────────────┐
│  Java checks session cookie     │
│                                 │
│  Valid?                         │
│  ├─ Yes → Serve index.html (SPA)│
│  └─ No  → Redirect to /login    │
└─────────────────────────────────┘
```

- Java filter intercepts all requests to protected routes
- Checks for valid session cookie
- Invalid/missing session → redirect to `/login`
- Valid session → proceed to serve content

### Logout

```
POST /api/auth/logout  ──▶  Java clears cookie
                            Invalidates session in DB
                            Redirects to /login
```

## Session Management

### Storage

Sessions stored in SQLite:

```sql
CREATE TABLE sessions (
  id TEXT PRIMARY KEY,           -- UUID
  user_id TEXT NOT NULL,         -- Reference to users table
  created_at TIMESTAMP NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  last_accessed TIMESTAMP NOT NULL,
  user_agent TEXT,               -- For session list in settings
  ip_address TEXT                -- For session list in settings
);
```

### Configuration

- Session expiry: 7 days (configurable)
- Session refresh: Extend expiry on each request
- Max sessions per user: unlimited (user can view and revoke in settings)

### Cookie Flags

| Flag     | Value             | Purpose                                     |
|----------|-------------------|---------------------------------------------|
| HttpOnly | true              | Prevents JavaScript access (XSS protection) |
| Secure   | true (production) | Only sent over HTTPS                        |
| SameSite | Strict            | CSRF protection                             |
| Path     | /                 | Available for all routes                    |
| Max-Age  | 7 days            | Session duration                            |

## API Endpoints

| Method | Path               | Description           |
|--------|--------------------|-----------------------|
| POST   | `/api/auth/login`  | Authenticate user     |
| POST   | `/api/auth/logout` | End session           |
| GET    | `/api/auth/me`     | Get current user info |

### POST /api/auth/login

Request:
```json
{
  "username": "admin",
  "password": "securepassword"
}
```

Response (success, 200):
```json
{
  "id": "uuid",
  "username": "admin",
  "role": "admin"
}
```

Response (failure, 401):
```json
{
  "error": "Invalid username or password"
}
```

### GET /api/auth/me

Response (authenticated, 200):
```json
{
  "id": "uuid",
  "username": "admin",
  "role": "admin",
  "storageUsed": 1073741824,
  "storageQuota": -1
}
```

Response (not authenticated, 401):
```json
{
  "error": "Not authenticated"
}
```

## UI Components

### Login Page

- Username input
- Password input (with visibility toggle)
- "Remember me" checkbox (extends session to 30 days)
- "Login" button
- Error message display
- Clean, centered design

### Layout Header (when logged in)

- User avatar/icon
- Username display
- Dropdown menu:
  - Settings
  - Logout

## Security Considerations

### Password Hashing

- Use bcrypt with cost factor 12
- Never store or log plain passwords
- Rate limit login attempts (5 per minute per IP)

### Session Security

- Generate cryptographically secure session IDs (UUID v4)
- Invalidate all sessions on password change
- Log suspicious activity (multiple failed attempts)

### Protection Against Attacks

| Attack            | Protection               |
|-------------------|--------------------------|
| XSS               | HTTP-only cookies        |
| CSRF              | SameSite=Strict cookie   |
| Session fixation  | New session ID on login  |
| Brute force       | Rate limiting            |
| Session hijacking | Secure flag, IP tracking |
