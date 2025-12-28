# User Management

## What it does

Admin can create, edit, and delete user accounts. Users can manage their own settings.

## User Model

```sql
CREATE TABLE users (
  id TEXT PRIMARY KEY,              -- UUID
  username TEXT UNIQUE NOT NULL,
  email TEXT UNIQUE,                -- Optional
  password_hash TEXT NOT NULL,
  role TEXT NOT NULL,               -- 'admin' or 'user'
  storage_quota INTEGER DEFAULT -1, -- Bytes, -1 = unlimited
  storage_used INTEGER DEFAULT 0,   -- Bytes
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);
```

### Roles

| Role | Permissions |
|------|-------------|
| admin | Full access: manage users, access all files, all settings |
| user | Access own files, change own password |

## API Endpoints

| Method | Path | Description | Access |
|--------|------|-------------|--------|
| GET | `/api/users` | List all users | Admin |
| POST | `/api/users` | Create new user | Admin |
| GET | `/api/users/{id}` | Get user details | Admin or self |
| PUT | `/api/users/{id}` | Update user | Admin or self (limited) |
| DELETE | `/api/users/{id}` | Delete user | Admin |
| PUT | `/api/users/{id}/password` | Change password | Admin or self |

### GET /api/users

Response:
```json
{
  "users": [
    {
      "id": "uuid",
      "username": "admin",
      "email": "admin@example.com",
      "role": "admin",
      "storageQuota": -1,
      "storageUsed": 1073741824,
      "createdAt": "2025-01-01T00:00:00Z"
    },
    {
      "id": "uuid",
      "username": "mom",
      "email": null,
      "role": "user",
      "storageQuota": 10737418240,
      "storageUsed": 524288000,
      "createdAt": "2025-01-02T00:00:00Z"
    }
  ]
}
```

### POST /api/users

Request:
```json
{
  "username": "newuser",
  "email": "user@example.com",
  "password": "initialpassword",
  "role": "user",
  "storageQuota": 10737418240
}
```

Response:
```json
{
  "id": "uuid",
  "username": "newuser",
  "email": "user@example.com",
  "role": "user",
  "storageQuota": 10737418240,
  "storageUsed": 0,
  "createdAt": "2025-01-15T00:00:00Z"
}
```

### PUT /api/users/{id}

Admin can update any field. Users can only update their own email.

Request (admin):
```json
{
  "email": "newemail@example.com",
  "role": "admin",
  "storageQuota": -1
}
```

Request (self):
```json
{
  "email": "newemail@example.com"
}
```

### PUT /api/users/{id}/password

Request:
```json
{
  "currentPassword": "oldpassword",   // Required for self
  "newPassword": "newpassword"
}
```

Admin can omit `currentPassword` when resetting another user's password.

### DELETE /api/users/{id}

Query params:
- `deleteFiles`: boolean (default: false) - Whether to delete user's files

Response:
```json
{
  "success": true,
  "filesDeleted": false
}
```

## UI Components

### Admin: User List Page

- Table with columns: Username, Email, Role, Storage Used, Actions
- "Add User" button
- Per-row actions: Edit, Delete
- Search/filter by username
- Storage usage progress bar per user

### Admin: Add/Edit User Modal

- Username input (disabled on edit)
- Email input
- Role dropdown (Admin, User)
- Storage quota input (with unit selector: MB, GB, Unlimited)
- Reset password checkbox (on edit)
- New password input (when reset checked or creating)
- Save/Cancel buttons

### Admin: Delete User Confirmation

- Warning message
- Checkbox: "Also delete all user's files"
- File count and size summary if deleting files
- Delete/Cancel buttons

### User: Settings Page

- Profile section:
  - Username (read-only)
  - Email (editable)
- Password section:
  - Current password
  - New password
  - Confirm new password
  - Change password button
- Storage section:
  - Usage bar (used / quota)
  - File count

## Validation Rules

### Username
- Required
- 3-50 characters
- Alphanumeric and underscore only
- Must be unique
- Cannot be changed after creation

### Email
- Optional
- Valid email format
- Must be unique if provided

### Password
- Required for new users
- Minimum 8 characters
- Admin can reset without knowing current password

### Storage Quota
- -1 for unlimited
- Minimum 0
- In bytes (UI converts from MB/GB)

## Business Rules

1. Cannot delete the last admin user
2. Cannot demote yourself from admin if you're the last admin
3. Deleting a user invalidates all their sessions
4. Storage quota is soft limit (warns but doesn't block critical operations)
5. When user is deleted with files, files are permanently removed (no trash)
