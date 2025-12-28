# File Upload

## What it does

Upload files from device to Filey with chunked/resumable support using the tus protocol.

## Why tus Protocol?

- Open standard for resumable uploads
- If connection drops at 9.99GB of a 10GB upload, resume from where it left off
- Has Java server implementations and JavaScript clients
- No need to build chunking logic from scratch

## API Endpoints

Following tus protocol:

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/upload` | Create upload session |
| PATCH | `/api/upload/{id}` | Upload chunk |
| HEAD | `/api/upload/{id}` | Get upload progress |
| DELETE | `/api/upload/{id}` | Cancel upload |

### POST /api/upload

Creates a new upload session.

Headers:
```
Upload-Length: 10485760
Upload-Metadata: filename base64encodedname,filetype base64encodedtype
Tus-Resumable: 1.0.0
```

Response:
```
HTTP/1.1 201 Created
Location: /api/upload/abc123
Tus-Resumable: 1.0.0
```

### PATCH /api/upload/{id}

Uploads a chunk of the file.

Headers:
```
Content-Type: application/offset+octet-stream
Upload-Offset: 0
Tus-Resumable: 1.0.0
```

Body: Binary chunk data

Response:
```
HTTP/1.1 204 No Content
Upload-Offset: 1048576
Tus-Resumable: 1.0.0
```

### HEAD /api/upload/{id}

Get current upload progress (for resuming).

Response:
```
HTTP/1.1 200 OK
Upload-Offset: 5242880
Upload-Length: 10485760
Tus-Resumable: 1.0.0
```

## Upload Flow

### Standard Flow

1. User selects files (file picker or drag-drop)
2. For each file:
   - Create upload session (POST)
   - Upload in 5MB chunks (PATCH)
   - If connection drops, get progress (HEAD) and resume
3. Once complete, file moved to target path
4. Thumbnail generated (sync for images, async for videos)
5. Database entry created

### "Upload First, Organize Later" Flow

1. User clicks quick upload button (or uses keyboard shortcut)
2. Selects files
3. Files upload to `/storage/{user_id}/.uploads/` (temp folder)
4. User sees success notification with "Move files" action
5. User can continue browsing while upload happens in background
6. Later, user moves files to desired location (or leaves in uploads)

## Upload Storage

### During Upload

Incomplete uploads stored in:
```
/data/.uploads-temp/{upload_id}
```

### After Completion

Moved to target path:
```
/storage/{user_id}/Documents/file.pdf
```

Or to uploads folder if no path specified:
```
/storage/{user_id}/.uploads/file.pdf
```

### Cleanup

- Incomplete uploads older than 24 hours are deleted
- Background job runs hourly

## UI Components

### Upload Button

- Always visible in toolbar
- Click opens file picker
- Multiple file selection enabled
- Icon + "Upload" text on desktop
- Icon only on mobile

### Drag and Drop Zone

- Entire file browser area is a drop zone
- Visual feedback on drag over:
  - Border highlight
  - "Drop files here" overlay
- Drop outside folders → upload to current folder
- Drop on folder → upload to that folder

### Upload Progress Panel

- Slides up from bottom of screen
- Collapsible (click to minimize/expand)
- Shows current upload queue

```
┌─────────────────────────────────────────────┐
│ Uploading 3 files                    [─] [×]│
├─────────────────────────────────────────────┤
│ 📄 large-video.mp4                          │
│ ████████████░░░░░░░░ 60% • 2.4 GB/4 GB     │
│ [⏸ Pause]                                   │
├─────────────────────────────────────────────┤
│ 📄 document.pdf                             │
│ Waiting...                                  │
├─────────────────────────────────────────────┤
│ 📄 photo.jpg                                │
│ ✓ Complete                                  │
└─────────────────────────────────────────────┘
```

### Per-File Controls

- Progress bar with percentage
- File size (current / total)
- Upload speed
- Estimated time remaining
- Pause/Resume button
- Cancel button (X)
- Retry button (on failure)

### Queue Management

- Files upload sequentially (to prevent overwhelming)
- Can reorder queue (drag and drop)
- Can remove pending files
- "Cancel all" option

### Notifications

- Toast notification on completion: "3 files uploaded"
- Error notification with retry option
- Desktop notifications (if permitted)

## Error Handling

### Connection Lost

1. Detect network failure
2. Show "Connection lost, will retry..." message
3. Retry with exponential backoff (1s, 2s, 4s, 8s, max 30s)
4. Resume from last successful offset

### File Too Large

- Check file size before starting
- Show error: "File exceeds maximum size of X GB"
- Allow user to skip and continue with other files

### Storage Full

- Server returns 507 Insufficient Storage
- Show error: "Storage quota exceeded"
- Link to storage settings

### Server Error

- Retry 3 times
- Show error with "Retry" button
- Allow user to skip file

## Validation

### Before Upload

- Check file size against quota
- Check filename for invalid characters
- Check for duplicate filenames (offer to rename)

### Filename Sanitization

- Remove/replace invalid filesystem characters
- Truncate to max 255 characters
- Handle collisions: `file.pdf` → `file (1).pdf`

## Mobile Considerations

### Camera Integration

- "Take Photo" option in upload menu
- Direct camera access
- Upload immediately or save to uploads folder

### Background Upload

- Continue upload when app is minimized (PWA)
- Resume on app reopen

### Low Bandwidth Mode

- Smaller chunk size (1MB instead of 5MB)
- More frequent progress saves
- Option to queue for WiFi only
