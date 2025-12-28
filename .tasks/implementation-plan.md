# Implementation Plan

## Phase 1: Project Foundation

1. Initialize Nx monorepo
2. Set up Java Spring Boot project with Gradle
3. Set up Vite React project with TypeScript
4. Configure Tailwind + shadcn/ui
5. Set up Storybook
6. Configure development proxy (Vite → Java)
7. Set up SQLite with initial schema
8. Configure Vitest, JUnit 5, Playwright

**Outcome**: Empty project with all tooling working. Can run `nx serve` and see both frontend and backend running.

## Phase 2: Authentication & Setup

1. Implement setup wizard (backend + frontend)
2. Implement user model and repository
3. Implement authentication endpoints
4. Implement session management with HTTP-only cookies
5. Implement Java auth filter (gatekeeper)
6. Create login page
7. Create basic layout with user menu

**Outcome**: Can run Filey, complete setup wizard, log in, and see a basic dashboard.

## Phase 3: File System Core

1. Implement file/folder model and repository
2. Implement file browser API endpoints
3. Implement file operations API (create folder, rename, delete)
4. Create file browser UI (grid/list view, navigation)
5. Implement file operations UI (context menu, modals)

**Outcome**: Can browse folders, create new folders, rename and delete files/folders.

## Phase 4: Upload System

1. Integrate tus server library
2. Implement upload endpoints
3. Create upload UI (button, drag-drop, progress)
4. Implement "upload first, organize later" temp folder flow
5. Test resumable uploads

**Outcome**: Can upload files with progress, pause/resume, and drag-drop support.

## Phase 5: Preview & Thumbnails

1. Implement thumbnail generation for images
2. Implement async thumbnail generation for videos (ffmpeg)
3. Create preview modal component
4. Implement preview for each supported file type

**Outcome**: Files show thumbnails in browser, can click to preview images/videos/documents.

## Phase 6: User Management

1. Implement user management API endpoints
2. Create admin user management page
3. Implement user settings page (change password)

**Outcome**: Admin can create/edit/delete users. Users can change their password.

## Phase 7: Polish & Testing

1. Implement responsive design adjustments
2. Add keyboard shortcuts
3. Write E2E tests with Playwright
4. Performance testing with large folders
5. Security review

**Outcome**: Works well on mobile, keyboard accessible, tested and secure.

## Phase 8: Docker & Deployment

1. Create Dockerfile
2. Create docker-compose.yml
3. Test deployment flow
4. Write deployment documentation

**Outcome**: Can deploy Filey with a single `docker run` command.

---

## Open Questions

These need to be decided during implementation:

- Exact thumbnail dimensions and format
- Session timeout duration (7 days default?)
- Trash auto-empty period (30 days default?)
- Max upload file size limit?
- Rate limiting approach
- Logging and monitoring strategy
- Backup strategy recommendations for users
- i18n / multi-language support (V2?)
