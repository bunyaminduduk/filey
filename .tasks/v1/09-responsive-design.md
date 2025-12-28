# Responsive Design

## What it does

Ensures Filey works well on desktop, tablet, and mobile devices.

## Breakpoints

| Name | Width | Layout |
|------|-------|--------|
| mobile | < 640px | Single column, bottom nav |
| tablet | 640-1024px | Collapsible sidebar |
| desktop | > 1024px | Fixed sidebar |

Using Tailwind's default breakpoints:
- `sm`: 640px
- `md`: 768px
- `lg`: 1024px
- `xl`: 1280px

## Layout Variations

### Desktop (> 1024px)

```
┌─────────────────────────────────────────────────────────────┐
│ [Logo]      [Search]                           [User ▼]     │
├──────────────┬──────────────────────────────────────────────┤
│              │ Home > Documents                             │
│  Files       │ ─────────────────────────────────────────    │
│  Shared      │                                              │
│  Trash       │  ┌─────┐  ┌─────┐  ┌─────┐  ┌─────┐        │
│              │  │     │  │     │  │     │  │     │        │
│  ──────      │  │ 📁  │  │ 📄  │  │ 🖼  │  │ 🎬  │        │
│  Storage     │  │     │  │     │  │     │  │     │        │
│  [███░░] 60% │  └─────┘  └─────┘  └─────┘  └─────┘        │
│              │   Work    doc.pdf  photo.jpg video.mp4      │
│  Settings    │                                              │
└──────────────┴──────────────────────────────────────────────┘
```

- Fixed sidebar (250px)
- Full file grid
- Toolbar with all actions

### Tablet (640-1024px)

```
┌─────────────────────────────────────────────────────────────┐
│ [≡] [Logo]  [Search]                           [User ▼]     │
├─────────────────────────────────────────────────────────────┤
│ Home > Documents                                            │
│ ─────────────────────────────────────────────────────────   │
│                                                             │
│  ┌─────┐  ┌─────┐  ┌─────┐  ┌─────┐  ┌─────┐              │
│  │     │  │     │  │     │  │     │  │     │              │
│  │ 📁  │  │ 📄  │  │ 🖼  │  │ 🎬  │  │ 📁  │              │
│  │     │  │     │  │     │  │     │  │     │              │
│  └─────┘  └─────┘  └─────┘  └─────┘  └─────┘              │
│   Work    doc.pdf  photo.jpg video.mp4  Archive            │
└─────────────────────────────────────────────────────────────┘
```

- Hamburger menu for sidebar
- Sidebar overlays content when open
- Slightly reduced file grid

### Mobile (< 640px)

```
┌─────────────────────────────┐
│ [≡] Documents        [⋮]   │
├─────────────────────────────┤
│ ┌───────┐  ┌───────┐       │
│ │       │  │       │       │
│ │  📁   │  │  📄   │       │
│ │       │  │       │       │
│ └───────┘  └───────┘       │
│   Work      doc.pdf        │
│                            │
│ ┌───────┐  ┌───────┐       │
│ │       │  │       │       │
│ │  🖼   │  │  🎬   │       │
│ │       │  │       │       │
│ └───────┘  └───────┘       │
│  photo.jpg  video.mp4      │
├─────────────────────────────┤
│ [📁 Files] [📤 Upload] [⚙] │
└─────────────────────────────┘
```

- 2 columns for files
- Bottom navigation bar
- Top bar simplified
- Actions in bottom sheet menu

## Component Adaptations

### Sidebar

**Desktop:** Always visible, fixed position
**Tablet:** Hidden by default, slides in from left
**Mobile:** Hidden, accessed via hamburger menu

### File Grid

**Desktop:** 4-5 items per row
**Tablet:** 3-4 items per row
**Mobile:** 2 items per row

### Context Menu

**Desktop:** Right-click menu
**Tablet:** Long-press menu
**Mobile:** Bottom sheet

### Upload Panel

**Desktop:** Bottom-right corner, floating
**Mobile:** Full-width bottom sheet

### Preview Modal

**Desktop:** Centered modal with margins
**Mobile:** Full-screen

### Dialogs

**Desktop:** Centered modal
**Mobile:** Full-screen or bottom sheet

## Touch Interactions

### Gestures

| Gesture          | Action                |
|------------------|-----------------------|
| Tap              | Select/Open           |
| Long press       | Context menu          |
| Swipe left/right | Navigate (in preview) |
| Pull down        | Refresh file list     |
| Pinch            | Zoom (in preview)     |

### Touch Targets

Minimum touch target size: 44x44 pixels

- Buttons: min-height 44px
- List items: min-height 48px
- Icons with actions: 44x44px tap area

## Desktop Interactions

### Keyboard Shortcuts

| Shortcut     | Action                        |
|--------------|-------------------------------|
| Arrow keys   | Navigate files                |
| Enter        | Open file/folder              |
| Backspace    | Go up one folder              |
| Space        | Preview file                  |
| Ctrl/Cmd + A | Select all                    |
| Ctrl/Cmd + C | Copy                          |
| Ctrl/Cmd + X | Cut                           |
| Ctrl/Cmd + V | Paste                         |
| Delete       | Move to trash                 |
| Ctrl/Cmd + U | Upload                        |
| Ctrl/Cmd + N | New folder                    |
| Escape       | Close modal / Clear selection |
| /            | Focus search                  |

### Drag and Drop

- Drag files to folders
- Drag to upload
- Drag to reorder (if applicable)
- Visual feedback during drag

### Hover States

- File cards show checkbox on hover
- Actions appear on hover
- Tooltips for truncated names

## Performance Considerations

### Images

- Use responsive images (`srcset`)
- Lazy load off-screen thumbnails
- Placeholder while loading

### Layout Shifts

- Reserve space for images
- Skeleton loaders match content size
- Avoid layout shift on load

### Touch vs Mouse

- Detect input type
- Show hover states only for mouse
- Increase tap targets on touch devices

## Accessibility

### Focus Management

- Visible focus indicators
- Logical tab order
- Focus trap in modals

### Screen Readers

- ARIA labels for icons
- Live regions for updates
- Semantic HTML

### Reduced Motion

- Respect `prefers-reduced-motion`
- Disable animations if preferred
- Provide instant transitions

### Color Contrast

- WCAG AA minimum (4.5:1 for text)
- Don't rely on color alone
- High contrast mode support
