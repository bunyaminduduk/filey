# Filey

Self-hosted file server with clean UI/UX for non-technical users.

## Stack

- **Frontend**: React 19 + Vite + TanStack Router/Query + Tailwind v4
- **Backend**: Java 25 + Spring Boot 4 + SQLite
- **Monorepo**: Nx + pnpm

## Structure

```
apps/web/       → React frontend
apps/server/    → Java backend
packages/ui/    → Shared UI components (@filey/ui)
```

## Commands

```bash
pnpm dev        # Dev mode (Vite on :5173, Java on :8080)
pnpm build      # Build all
pnpm test       # Test all
pnpm format     # Format with Prettier
```

## How to Work

- Vite proxies `/api/*` to Java backend
- UI components go in `packages/ui/`, add to `packages/ui/src/index.ts`
- Run `pnpm dlx shadcn@latest add <component>` from `apps/web/`

## Docs

Detailed specs in `.tasks/`.