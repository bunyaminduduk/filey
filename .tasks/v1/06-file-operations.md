# File Operations

## What it does

Create, rename, move, copy, and delete files and folders.

## API Endpoints

| Method | Path                      | Description                   |
|--------|---------------------------|-------------------------------|
| POST   | `/api/files`              | Create new folder             |
| PUT    | `/api/files/{id}`         | Rename file/folder            |
| POST   | `/api/files/{id}/move`    | Move to new location          |
| POST   | `/api/files/{id}/copy`    | Copy to new location          |
| DELETE | `/api/files/{id}`         | Delete file/folder (to trash) |
| POST   | `/api/files/batch`        | Batch operations              |
| GET    | `/api/trash`              | List trashed items            |
| POST   | `/api/trash/{id}/restore` | Restore from trash            |
| DELETE | `/api/trash/{id}`         | Permanently delete            |
| DELETE | `/api/trash`              | Empty trash                   |

### POST /api/files (Create Folder)

Request:
```json
{
  "name": "New Folder",
  "parentPath": "/Documents"
}
```

Response:
```json
{
  "id": "uuid",
  "name": "New Folder",
  "path": "/Documents/New Folder",
  "type": "folder",
  "createdAt": "2025-01-15T10:00:00Z"
}
```

### PUT /api/files/{id} (Rename)

Request:
```json
{
  "name": "New Name.pdf"
}
```

Response:
```json
{
  "id": "uuid",
  "name": "New Name.pdf",
  "path": "/Documents/New Name.pdf",
  "updatedAt": "2025-01-15T10:00:00Z"
}
```

### POST /api/files/{id}/move

Request:
```json
{
  "destinationPath": "/Archive"
}
```

Response:
```json
{
  "id": "uuid",
  "path": "/Archive/document.pdf",
  "previousPath": "/Documents/document.pdf"
}
```

### POST /api/files/{id}/copy

Request:
```json
{
  "destinationPath": "/Backup"
}
```

Response:
```json
{
  "id": "new-uuid",
  "path": "/Backup/document.pdf",
  "originalId": "uuid"
}
```

### DELETE /api/files/{id}

Query params:
- `permanent`: boolean (default: false) - Skip trash

Response:
```json
{
  "id": "uuid",
  "trashed": true,
  "trashedAt": "2025-01-15T10:00:00Z"
}
```

### POST /api/files/batch

Request:
```json
{
  "operation": "move",  // move, copy, delete
  "ids": ["uuid1", "uuid2", "uuid3"],
  "destinationPath": "/Archive"  // for move/copy
}
```

Response:
```json
{
  "success": ["uuid1", "uuid2"],
  "failed": [
    {
      "id": "uuid3",
      "error": "File not found"
    }
  ]
}
```

## Trash System

### Trash Behavior

- Delete moves files to trash (soft delete)
- Trashed files remain on disk but hidden from browser
- Database marks: `is_trashed = true`, `trashed_at = now()`
- Original path preserved for restore

### Trash Storage

Files stay in their original location on disk. Only the database entry is updated. This makes restore instant.

### Auto-Empty

- Background job runs daily
- Deletes items older than 30 days (configurable)
- Logs deleted items for audit

### Trash UI

Accessible from sidebar or menu:

```
┌─────────────────────────────────────────────┐
│ Trash                          [Empty Trash]│
├─────────────────────────────────────────────┤
│ 📄 old-document.pdf                         │
│    Deleted 5 days ago • /Documents          │
│    [Restore] [Delete Forever]               │
├─────────────────────────────────────────────┤
│ 📁 Old Project                              │
│    Deleted 2 weeks ago • /Projects          │
│    [Restore] [Delete Forever]               │
└─────────────────────────────────────────────┘
```

## UI Components

### New Folder Dialog

- Input field for folder name
- Create / Cancel buttons
- Validation: no special characters, max length
- Auto-focus input on open

### Rename Dialog

- Input field pre-filled with current name
- Name part selected (not extension)
- Rename / Cancel buttons
- Validation: no special characters, unique in folder

### Move/Copy Dialog

- Folder tree browser
- Current location highlighted
- Cannot move folder into itself
- Create new folder option
- Move/Copy / Cancel buttons

### Delete Confirmation

- "Move to Trash" as default action
- "Delete Forever" as secondary option
- Item name and type shown
- Warning for folders (shows item count)

### Batch Operations

When multiple items selected:
- Toolbar shows: "X items selected"
- Actions: Move, Copy, Delete, Download (zip)
- Confirmation shows count

## Validation Rules

### Folder Names
- Max 255 characters
- No: `/`, `\`, `:`, `*`, `?`, `"`, `<`, `>`, `|`
- No leading/trailing spaces
- No `.` or `..`

### File Names
- Same as folder names
- Extension preserved on rename (warn if changing)

### Collisions

When moving/copying to location with same name:
- Dialog: "File already exists"
- Options: Replace, Keep Both, Skip
- "Keep Both" renames: `file.pdf` → `file (1).pdf`
- "Apply to all" checkbox for batch

## Disk Operations

### Move

1. Update database path
2. Move file on disk: `Files.move()`
3. Update parent_id reference

### Copy

1. Copy file on disk: `Files.copy()`
2. Create new database entry
3. Generate new UUID
4. Copy permissions

### Delete (to trash)

1. Update database: `is_trashed = true`
2. No disk operation (file stays)

### Delete (permanent)

1. Delete file from disk
2. Delete database entry
3. Delete thumbnail if exists
4. Update user's storage_used

### Create Folder

1. Create directory on disk: `Files.createDirectory()`
2. Create database entry
3. Inherit permissions from parent

## Error Handling

| Error | User Message |
|-------|--------------|
| File not found | "This file no longer exists" |
| Permission denied | "You don't have permission" |
| Name too long | "Name must be under 255 characters" |
| Invalid characters | "Name contains invalid characters" |
| Disk full | "Not enough storage space" |
| Name conflict | "An item with this name already exists" |
