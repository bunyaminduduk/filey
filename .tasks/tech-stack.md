# Tech Stack

## Frontend

| Technology      | Purpose                                      |
|-----------------|----------------------------------------------|
| React           | UI framework                                 |
| TypeScript      | Type safety                                  |
| Vite            | Build tool, dev server (SPA, no SSR)         |
| TanStack Query  | Data fetching, caching, mutations            |
| TanStack Router | File-based routing, type-safe                |
| Tailwind CSS    | Styling                                      |
| shadcn/ui       | Component library (accessible, customizable) |
| Storybook       | Component visualization and documentation    |

### Why Vite SPA instead of Next.js?

- Filey is behind authentication, so SEO doesn't matter
- All content is user-specific and dynamic, nothing to pre-render
- Simpler architecture: no Node.js server needed in production
- Java backend serves static files directly
- No hydration issues, no "use client" directives

### Why TanStack?

- TanStack Query is the standard for data fetching in React SPAs
- TanStack Router provides file-based routing (like Next.js) with full type safety
- Both integrate well together and are actively maintained

### Why shadcn/ui?

- Not a dependency: you own the component code
- Built on Radix primitives for accessibility
- Fully customizable with Tailwind
- No library lock-in

## Backend

| Technology   | Purpose                                                 |
|--------------|---------------------------------------------------------|
| Java 25      | Runtime (performance improvements with virtual threads) |
| Spring Boot  | Web framework                                           |
| SQLite       | Database (lightweight, no separate server)              |
| tus protocol | Chunked/resumable file uploads                          |

### Why Java 25?

- Battle-tested for file I/O and long-running processes
- Virtual threads (Project Loom) for efficient concurrent uploads/downloads
- Strong typing and mature ecosystem
- No "jankiness" of JavaScript for handling critical file operations

### Why SQLite?

- Single file database, no separate server
- Zero configuration
- More than enough for home users with a handful of users
- Easy backups (just copy the file)

### Why tus protocol?

- Open standard for resumable uploads
- If connection drops at 9.99GB of a 10GB upload, resume from where you left off
- Has Java server implementations and JavaScript clients

## Monorepo & Build

| Technology | Purpose                     |
|------------|-----------------------------|
| Nx         | Monorepo task orchestration |
| Gradle     | Java build tool             |

### Why Nx?

- Task orchestration across Java and JavaScript
- Caching for faster builds
- Used by 70% of Fortune 500 companies
- Single command to run both frontend and backend

### Why Gradle?

- More flexible than Maven (Groovy/Kotlin DSL)
- Faster incremental builds
- Better integration with Nx

## Testing

| Technology | Purpose |
|------------|---------|
| Vitest | Frontend unit tests |
| JUnit 5 | Backend unit tests |
| Playwright | End-to-end tests |

### Test Locations

| Type          | Tool       | Location                                     |
|---------------|------------|----------------------------------------------|
| Frontend unit | Vitest     | `apps/web/tests/` (mirrors `src/`)           |
| Backend unit  | JUnit 5    | `apps/server/src/test/java/` (Java standard) |
| UI components | Vitest     | `packages/ui/tests/`                         |
| E2E           | Playwright | `apps/e2e/tests/`                            |
