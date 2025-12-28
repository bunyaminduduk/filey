# Filey

A self-hosted file server with a focus on clean UI/UX.

## What is Filey?

Filey is a modern, self-hosted file server that makes it easy to access and manage your files from anywhere. Whether you're uploading photos from your phone, sharing documents with family, or managing files across multiple devices, Filey provides a clean, intuitive interface that anyone can use.

## Why Filey?

Existing self-hosted file solutions often suffer from:

- **Complex setup** requiring extensive documentation
- **Poor UI/UX** designed by developers for developers
- **Bloated features** that make simple tasks complicated
- **Unclear security** leaving users unsure about their data

Filey takes a different approach: simplicity first, with powerful features when you need them.

## Features

### Core (Free Forever)
- Upload, download, view, and delete files
- Folder navigation and management
- User accounts with simple authentication
- File previews (images, videos, documents)
- Resumable uploads (never lose progress on large files)
- Mobile-friendly responsive design
- Simple Docker deployment

### Premium (License Required)
- SSO integration
- Team management
- Advanced sharing (external links, expiring, password-protected)
- Audit logging
- SMB and other protocol support

## Quick Start

```bash
docker run -d \
  -p 8080:8080 \
  -v filey-data:/data \
  -v /path/to/your/files:/storage \
  filey/filey
```

Visit `http://localhost:8080` to complete the setup wizard.

## Documentation

See the [docs](./docs) folder for detailed documentation.

## Development

Filey uses an Nx monorepo with:

- **Frontend**: React, TypeScript, Vite, TanStack Query/Router, Tailwind, shadcn/ui
- **Backend**: Java 25, Spring Boot, SQLite
- **Testing**: Vitest, JUnit 5, Playwright

See [.tasks](./.tasks) for implementation planning and feature specifications.

## License

Open source. See [LICENSE](./LICENSE) for details.
