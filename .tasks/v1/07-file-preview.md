# File Preview

## What it does

Preview files directly in the browser without downloading them.

## Supported Formats

### V1 Support

| Type   | Formats                                  | Preview Method            |
|--------|------------------------------------------|---------------------------|
| Images | jpg, jpeg, png, gif, webp, svg, bmp, ico | Native `<img>` tag        |
| Videos | mp4, webm, mov, avi, mkv                 | Native `<video>` tag      |
| Audio  | mp3, wav, ogg, m4a, flac, aac            | Native `<audio>` tag      |
| Text   | txt, md, json, xml, yaml, csv, log       | Syntax-highlighted viewer |
| Code   | js, ts, jsx, tsx, py, java, go, rs, etc. | Syntax-highlighted viewer |
| PDF    | pdf                                      | PDF.js or native browser  |

### Future (V2+)

- Office documents (docx, xlsx, pptx)
- Archives (zip, tar, gz) - show contents
- 3D models
- eBooks (epub)

## Thumbnail Generation

### When Generated

- On file upload completion
- Background job for existing files (on first access)

### Image Thumbnails

- Generated immediately (sync)
- Size: 200x200 pixels
- Format: JPEG (quality 80)
- Crop mode: Cover (fill square, crop excess)
- Library: Java ImageIO or Thumbnailator

### Video Thumbnails

- Generated in background (async)
- Extract frame at 1 second (or 10% if shorter)
- Same size/format as images
- Tool: FFmpeg
- Fallback: Generic video icon until ready

### Storage

```
/data/.thumbnails/
  {file_id}.jpg
```

### Cache Headers

Thumbnails served with:
```
Cache-Control: public, max-age=31536000
ETag: {file_hash}
```

## Preview Modal

### Layout

```
┌─────────────────────────────────────────────────────────────┐
│ [←] [→]                document.pdf                    [×]  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│                                                             │
│                    ┌─────────────────┐                      │
│                    │                 │                      │
│                    │   File Preview  │                      │
│                    │                 │                      │
│                    └─────────────────┘                      │
│                                                             │
│                                                             │
├─────────────────────────────────────────────────────────────┤
│ Name: document.pdf  Size: 2.4 MB  Modified: Jan 15, 2025   │
│                                          [Download] [Share] │
└─────────────────────────────────────────────────────────────┘
```

### Navigation

- Left/Right arrows to browse files in same folder
- Keyboard: Arrow keys, Escape to close
- Swipe on mobile
- Only previews files (skips folders)

### Controls by Type

**Images:**
- Zoom in/out
- Fit to screen / Actual size
- Rotate

**Videos:**
- Play/Pause
- Seek bar
- Volume control
- Fullscreen
- Playback speed

**Audio:**
- Play/Pause
- Seek bar
- Volume control
- Waveform visualization (optional)

**Text/Code:**
- Syntax highlighting
- Line numbers
- Word wrap toggle
- Copy button

**PDF:**
- Page navigation
- Zoom
- Fit width / Fit page
- Thumbnail sidebar

## API Endpoints

| Method | Path                        | Description         |
|--------|-----------------------------|---------------------|
| GET    | `/api/files/{id}/preview`   | Get preview content |
| GET    | `/api/files/{id}/thumbnail` | Get thumbnail       |

### GET /api/files/{id}/preview

Returns appropriate content based on file type:

**Images:** Raw image bytes with correct Content-Type

**Videos/Audio:**
- Supports Range headers for streaming
- Returns partial content (206) for seeks

**Text/Code:**
```json
{
  "content": "file contents here",
  "language": "javascript",
  "lineCount": 150,
  "truncated": false
}
```

**PDF:** Raw PDF bytes

### GET /api/files/{id}/thumbnail

- Returns thumbnail JPEG
- 404 if not yet generated
- 404 for unsupported types

## Large File Handling

### Text Files

- Limit preview to first 100KB
- Show "File truncated" warning
- Offer to download full file

### Videos

- Stream on demand (no pre-loading)
- Range request support for seeking
- Adaptive quality if transcoding added later

### Images

- Serve original for preview
- Consider generating web-optimized version for very large images

## Mobile Preview

### Touch Gestures

- Pinch to zoom (images)
- Swipe to navigate
- Tap to show/hide controls

### Fullscreen

- Auto-fullscreen for videos
- Gesture to enter/exit fullscreen

## Error States

| Scenario           | Display                                                 |
|--------------------|---------------------------------------------------------|
| Unsupported format | Icon + "Preview not available" + Download button        |
| File too large     | Warning + "File too large to preview" + Download button |
| Generation failed  | Error message + Retry button                            |
| Network error      | "Failed to load" + Retry button                         |

## Performance

### Lazy Loading

- Don't load preview until modal opens
- Show skeleton/spinner while loading

### Preloading

- Preload next/previous file in folder
- Preload thumbnails for visible files

### Memory Management

- Dispose video/audio elements when closing
- Clear large image references
- Limit simultaneous previews
