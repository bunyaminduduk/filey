# Permissions (ACL)

## What it does

Control who can access what files and folders using Access Control Lists.

## V1 Scope

V1 focuses on basic permission model:
- Each user has their own root folder
- Admin can access all users' files
- Sharing between users is V2+ feature

## Permission Model

```sql
CREATE TABLE permissions (
  id TEXT PRIMARY KEY,
  file_entry_id TEXT NOT NULL,
  user_id TEXT,                    -- NULL = applies to all users
  permission TEXT NOT NULL,        -- 'read', 'write', 'admin'
  inherited BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL,

  FOREIGN KEY (file_entry_id) REFERENCES file_entries(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_permissions_file ON permissions(file_entry_id);
CREATE INDEX idx_permissions_user ON permissions(user_id);
```

## Permission Levels

| Level | Can View | Can Upload | Can Edit/Delete | Can Manage Permissions |
|-------|----------|------------|-----------------|------------------------|
| none  | No       | No         | No              | No                     |
| read  | Yes      | No         | No              | No                     |
| write | Yes      | Yes        | Yes             | No                     |
| admin | Yes      | Yes        | Yes             | Yes                    |

## Inheritance

### How It Works

1. By default, folders inherit permissions from their parent
2. Can override permissions at any level
3. `inherited` flag tracks if permission was explicitly set or inherited

### Resolution Order

1. Check explicit permission on file/folder
2. If none, check parent folder
3. Continue up to root
4. If no permission found, deny access

### Override Example

```
/Documents (user: admin)
  /Shared (user: admin, all users: read)  ← Override
    /file.txt (inherits: all users can read)
```

## User Root Folders

### Structure

```
/storage/
  {user_id}/           ← User's root folder
    Documents/
    Photos/
    .uploads/          ← Temp upload folder
```

### Permissions

- User has `admin` permission on their root folder
- All children inherit this permission
- Admin users have `admin` on all folders

## API Endpoints

| Method | Path                                   | Description                     |
|--------|----------------------------------------|---------------------------------|
| GET    | `/api/files/{id}/permissions`          | Get permissions for file/folder |
| PUT    | `/api/files/{id}/permissions`          | Set permissions                 |
| DELETE | `/api/files/{id}/permissions/{permId}` | Remove specific permission      |

### GET /api/files/{id}/permissions

Response:
```json
{
  "fileId": "uuid",
  "path": "/Documents/Shared",
  "owner": {
    "id": "uuid",
    "username": "admin"
  },
  "permissions": [
    {
      "id": "uuid",
      "user": null,
      "permission": "read",
      "inherited": false
    },
    {
      "id": "uuid",
      "user": {
        "id": "uuid",
        "username": "mom"
      },
      "permission": "write",
      "inherited": false
    }
  ],
  "effectivePermission": "admin"
}
```

### PUT /api/files/{id}/permissions

Request:
```json
{
  "permissions": [
    {
      "userId": null,
      "permission": "read"
    },
    {
      "userId": "uuid",
      "permission": "write"
    }
  ]
}
```

## Permission Checks

### In Java

```java
public class PermissionService {

    public boolean hasPermission(UUID userId, UUID fileId, Permission required) {
        // Check if user is admin
        User user = userRepository.findById(userId);
        if (user.getRole() == Role.ADMIN) {
            return true;
        }

        // Check explicit permissions
        Permission effective = getEffectivePermission(userId, fileId);
        return effective.includes(required);
    }

    public Permission getEffectivePermission(UUID userId, UUID fileId) {
        FileEntry file = fileRepository.findById(fileId);

        // Walk up the tree
        while (file != null) {
            Permission perm = permissionRepository.findByFileAndUser(file.getId(), userId);
            if (perm != null) {
                return perm;
            }
            file = file.getParent();
        }

        return Permission.NONE;
    }
}
```

### Caching

- Cache effective permissions per user+file
- Invalidate on permission change
- Invalidate all children on parent change

## UI Components

### Permissions Dialog (V2)

For V1, permissions are implicit based on ownership. In V2:

```
┌─────────────────────────────────────────────┐
│ Permissions: Documents/Shared          [×]  │
├─────────────────────────────────────────────┤
│ Owner: admin                                │
├─────────────────────────────────────────────┤
│ Who has access:                             │
│                                             │
│ 👤 Everyone              [Read      ▼]  [×]│
│ 👤 mom                   [Write     ▼]  [×]│
│                                             │
│ [+ Add user]                                │
├─────────────────────────────────────────────┤
│                              [Cancel] [Save]│
└─────────────────────────────────────────────┘
```

## Security Considerations

### Path Traversal Prevention

- Always resolve paths to canonical form
- Validate file is within user's allowed scope
- Reject `..` and other traversal attempts

### Time-of-Check to Time-of-Use (TOCTOU)

- Re-check permissions on actual file operations
- Don't trust cached permissions for writes/deletes

### Admin Access Logging

- Log when admin accesses other users' files
- Important for audit trail

## V2+ Enhancements

- Share with specific users
- Share with groups
- Share via link (external sharing)
- Temporary permissions (expire after time)
- Per-file permissions UI
- Permission inheritance toggle
