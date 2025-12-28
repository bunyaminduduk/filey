# File Browser

## What it does

Navigate folders and view files in a clean, intuitive interface.

## File/Folder Model

```sql
CREATE TABLE file_entries (
  id TEXT PRIMARY KEY,              -- UUID
  name TEXT NOT NULL,
  path TEXT NOT NULL,               -- Virtual path (what user sees)
  real_path TEXT NOT NULL,          -- Actual disk path
  type TEXT NOT NULL,               -- 'file' or 'folder'
  mime_type TEXT,                   -- For files only
  size INTEGER,                     -- Bytes, for files only
  owner_id TEXT NOT NULL,           -- Reference to users
  parent_id TEXT,                   -- Reference to parent folder, null for root
  thumbnail_path TEXT,              -- Path to thumbnail, if generated
  is_trashed BOOLEAN DEFAULT FALSE,
  trashed_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,

  FOREIGN KEY (owner_id) REFERENCES users(id),
  FOREIGN KEY (parent_id) REFERENCES file_entries(id)
);

CREATE INDEX idx_file_entries_parent ON file_entries(parent_id);
CREATE INDEX idx_file_entries_owner ON file_entries(owner_id);
CREATE INDEX idx_file_entries_path ON file_entries(path);
```

## API Endpoints

| Method | Path                        | Description                |
|--------|-----------------------------|----------------------------|
| GET    | `/api/files`                | List files/folders at path |
| GET    | `/api/files/{id}`           | Get file/folder metadata   |
| GET    | `/api/files/{id}/download`  | Download file content      |
| GET    | `/api/files/{id}/thumbnail` | Get thumbnail              |

### GET /api/files

Query params:
- `path`: Virtual path (default: `/`)
- `sort`: `name`, `date`, `size` (default: `name`)
- `order`: `asc`, `desc` (default: `asc`)

Response:
```json
{
  "path": "/Documents",
  "entries": [
    {
      "id": "uuid",
      "name": "Work",
      "type": "folder",
      "itemCount": 15,
      "updatedAt": "2025-01-15T10:30:00Z"
    },
    {
      "id": "uuid",
      "name": "report.pdf",
      "type": "file",
      "mimeType": "application/pdf",
      "size": 1048576,
      "thumbnailUrl": "/api/files/uuid/thumbnail",
      "updatedAt": "2025-01-14T09:00:00Z"
    }
  ],
  "breadcrumbs": [
    { "name": "Home", "path": "/" },
    { "name": "Documents", "path": "/Documents" }
  ]
}
```

### GET /api/files/{id}/download

- Returns file content with appropriate Content-Type
- Sets Content-Disposition header for download
- Supports Range headers for partial content (video streaming)

### GET /api/files/{id}/thumbnail

- Returns thumbnail image (JPEG)
- Returns 404 if no thumbnail available
- Returns generic icon based on mime type if no thumbnail

## UI Components

### Main Layout

```
┌─────────────────────────────────────────────────────┐
│ [Logo]  [Breadcrumb: Home > Documents]    [User ▼] │
├─────────────────────────────────────────────────────┤
│ [Upload] [New Folder] [Grid/List] [Sort ▼]         │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ┌─────┐  ┌─────┐  ┌─────┐  ┌─────┐               │
│  │     │  │     │  │     │  │     │               │
│  │ 📁  │  │ 📁  │  │ 📄  │  │ 🖼  │               │
│  │     │  │     │  │     │  │     │               │
│  └─────┘  └─────┘  └─────┘  └─────┘               │
│   Work    Personal report.pdf photo.jpg            │
│                                                     │
└─────────────────────────────────────────────────────┘
```

### Breadcrumb Navigation

- Shows full path with clickable segments
- Home icon for root
- Truncates middle segments on mobile
- Current folder is not clickable

### Grid View

- 4-5 items per row on desktop
- 2-3 items per row on mobile
- Thumbnail or icon for each item
- Name below thumbnail
- Hover shows full name if truncated
- Selection checkbox on hover

### List View

- Full width rows
- Columns: Icon, Name, Size, Modified Date
- Sortable columns
- Selection checkbox per row

### Context Menu (right-click or long-press)

- Download (files only)
- Open (preview)
- Rename
- Move to...
- Copy to...
- Delete
- Properties

### Multi-select

- Click + Shift: Select range
- Click + Ctrl/Cmd: Toggle selection
- Drag to select multiple
- Toolbar changes when items selected:
  - "X selected" indicator
  - Download (zips multiple)
  - Move to...
  - Delete
  - Clear selection

### Sorting

Dropdown with options:
- Name (A-Z)
- Name (Z-A)
- Date (Newest)
- Date (Oldest)
- Size (Largest)
- Size (Smallest)

Folders always shown first regardless of sort.

### Empty State

- Icon showing empty folder
- "This folder is empty"
- "Drop files here or click Upload"

### Loading State

- Skeleton loader matching grid/list layout
- Maintains layout stability

## Keyboard Shortcuts

| Shortcut     | Action                 |
|--------------|------------------------|
| Arrow keys   | Navigate between items |
| Enter        | Open file/folder       |
| Backspace    | Go to parent folder    |
| Ctrl/Cmd + A | Select all             |
| Escape       | Clear selection        |
| Delete       | Delete selected        |
| Ctrl/Cmd + C | Copy                   |
| Ctrl/Cmd + X | Cut                    |
| Ctrl/Cmd + V | Paste                  |

## Performance Considerations

### Large Folders

- Virtualized list for folders with 100+ items
- Load items in pages (50 at a time)
- Intersection observer for lazy loading

### Thumbnails

- Lazy load thumbnails as they scroll into view
- Low-res placeholder while loading
- Cache thumbnails in browser
