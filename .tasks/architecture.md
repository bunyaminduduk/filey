# Architecture

## Project Structure

```
filey/
├── apps/
│   ├── web/                    # React frontend
│   │   ├── src/
│   │   │   ├── routes/         # TanStack Router file-based routes
│   │   │   ├── components/     # App-specific components
│   │   │   ├── hooks/          # Custom React hooks
│   │   │   ├── lib/            # Utilities, API client
│   │   │   └── main.tsx
│   │   ├── tests/              # Frontend unit tests (mirrors src/ structure)
│   │   │   ├── components/
│   │   │   ├── hooks/
│   │   │   └── lib/
│   │   ├── index.html
│   │   └── vite.config.ts
│   │
│   ├── server/                 # Java Spring backend
│   │   ├── src/main/java/
│   │   │   └── com/filey/
│   │   │       ├── config/     # Spring configuration
│   │   │       ├── controller/ # REST controllers
│   │   │       ├── service/    # Business logic
│   │   │       ├── repository/ # Data access
│   │   │       ├── model/      # Entities
│   │   │       ├── security/   # Auth, filters
│   │   │       └── extension/  # Premium extension points
│   │   ├── src/test/java/      # Backend unit tests (mirrors main/ structure)
│   │   │   └── com/filey/
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       └── ...
│   │   └── build.gradle
│   │
│   └── e2e/                    # End-to-end tests (Playwright)
│       ├── tests/
│       │   ├── auth.spec.ts
│       │   ├── file-browser.spec.ts
│       │   ├── file-upload.spec.ts
│       │   └── ...
│       └── playwright.config.ts
│
├── packages/
│   └── ui/                     # Shared UI components (for Storybook)
│       ├── src/components/
│       ├── tests/              # UI component tests
│       └── package.json
│
├── nx.json
├── package.json
└── docker/
    ├── Dockerfile
    └── docker-compose.yml
```

## Authentication Flow

```
1. User visits /dashboard
         │
         ▼
┌─────────────────────────────────┐
│  Java checks session cookie     │
│                                 │
│  Valid?                         │
│  ├─ Yes → Serve index.html (SPA)│
│  └─ No  → Redirect to /login    │
└─────────────────────────────────┘
         │
         ▼ (if valid)
┌─────────────────────────────────┐
│  React loads                    │
│  Fetches GET /api/me            │
│  Displays user info             │
└─────────────────────────────────┘
```

### Why this approach?

- **No "checking auth" spinner**: Java validates before serving any JavaScript
- **HTTP-only cookies**: Secure, can't be accessed by JavaScript (XSS-safe)
- **Simple mental model**: React handles UI only, Java handles all auth logic

### Login Flow

1. User submits username + password to `POST /api/auth/login`
2. Server validates credentials (password hashed with bcrypt)
3. On success: Create session, set HTTP-only cookie, return user info
4. On failure: Return 401 with error message

### Session Management

- Sessions stored in SQLite
- Session expiry: configurable (default 7 days)
- Cookie flags: `HttpOnly`, `Secure` (in production), `SameSite=Strict`

## File Storage

### On Disk

Files are stored at their original path structure:

```
/storage/
  {user_id}/
    Documents/
      report.pdf
    Photos/
      vacation/
        photo1.jpg
        photo2.jpg
  .thumbnails/
    {file_id}.jpg
```

### In Database

Only metadata is stored in SQLite:

- File name, size, mime type
- Owner, permissions
- Created/modified timestamps
- Thumbnail path (when generated)
- Virtual path (what user sees) vs real path (actual disk location)

## Development vs Production

### Development

```
┌─────────────────┐     proxy /api/*      ┌─────────────────┐
│  Vite Dev       │ ──────────────────▶   │  Java Spring    │
│  localhost:5173 │                       │  localhost:8080 │
│  (hot reload)   │ ◀──────────────────   │  (REST API)     │
└─────────────────┘     JSON responses    └─────────────────┘
```

- Vite dev server runs on port 5173 with hot module replacement
- API calls proxied to Java backend on port 8080
- Both servers run simultaneously

### Production

```
┌─────────────────────────────────────────┐
│  Java Spring (single server)            │
│                                         │
│  /api/*  → REST controllers             │
│  /*      → serves static files from     │
│            dist/ folder (Vite build)    │
└─────────────────────────────────────────┘
```

- Vite builds to static files (`dist/`)
- Java serves both API and static files
- Single server, single port, simple deployment
