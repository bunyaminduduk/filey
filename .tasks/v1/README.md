# V1 Features

Version 1 focuses on the core file management experience. The goal is a solid foundation that's simple, reliable, and pleasant to use.

## Features

| #  | Feature                                        | Description                                |
|----|------------------------------------------------|--------------------------------------------|
| 01 | [Setup Wizard](./01-setup-wizard.md)           | First-time configuration via web interface |
| 02 | [Authentication](./02-authentication.md)       | Secure login with HTTP-only cookies        |
| 03 | [User Management](./03-user-management.md)     | Create, edit, delete user accounts         |
| 04 | [File Browser](./04-file-browser.md)           | Navigate folders and view files            |
| 05 | [File Upload](./05-file-upload.md)             | Chunked/resumable uploads with tus         |
| 06 | [File Operations](./06-file-operations.md)     | Create, rename, move, copy, delete         |
| 07 | [File Preview](./07-file-preview.md)           | Preview files without downloading          |
| 08 | [Permissions](./08-permissions.md)             | ACL-based access control                   |
| 09 | [Responsive Design](./09-responsive-design.md) | Works on desktop and mobile                |
| 10 | [Docker Deployment](./10-docker-deployment.md) | Simple container deployment                |

## What V1 Does NOT Include

These are planned for V2+:

- SSO integration
- Team management
- External sharing (shareable links)
- Search, tags, filters
- SMB and other protocol support
- React Native mobile app
- i18n / multi-language support

## Success Criteria

V1 is complete when:

1. A user can run `docker run filey/filey` and complete setup
2. Multiple users can log in with their own accounts
3. Users can upload, download, browse, and organize files
4. Files can be previewed (images, videos, documents)
5. Works well on both desktop and mobile
6. E2E tests pass for all core flows
