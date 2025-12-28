# Setup Wizard

## What it does

First-time configuration via web interface when Filey starts with no existing configuration.

## User Flow

1. User runs `docker run filey/filey`
2. Visits `http://localhost:8080`
3. Filey detects no configuration, shows setup wizard
4. Steps:
   - Welcome screen (explains what Filey is)
   - Create admin account (username, password, confirm password)
   - Set storage location path (with validation)
   - Configure server name (optional, defaults to "Filey")
   - Confirm and finish
5. Configuration saved, redirected to login page

## Technical Details

### Detection

- Check for existence of config in SQLite database
- If no config exists, redirect all routes to `/setup`
- Once setup is complete, `/setup` route returns 404

### Validation

- Username: required, alphanumeric + underscore, 3-50 characters
- Password: required, minimum 8 characters
- Storage path: must exist and be writable
- Server name: optional, max 100 characters

### Storage

Configuration stored in SQLite `settings` table:

```sql
CREATE TABLE settings (
  key TEXT PRIMARY KEY,
  value TEXT NOT NULL
);
```

Example entries:
- `setup_complete`: "true"
- `server_name`: "My Filey Server"
- `storage_path`: "/storage"

### Security

- Setup wizard only accessible when `setup_complete` is not "true"
- After completion, setup endpoints return 403
- No way to re-run setup without database reset

## UI Components

### Welcome Screen
- Filey logo
- Brief description of what Filey does
- "Get Started" button

### Admin Account Step
- Username input
- Password input (with visibility toggle)
- Confirm password input
- Password strength indicator
- "Next" button

### Storage Path Step
- Path input field
- "Browse" button (if applicable)
- Validation feedback (exists, writable, free space)
- "Next" button

### Server Name Step
- Server name input
- Explanation that this appears in the browser title
- "Next" button

### Confirmation Step
- Summary of all settings
- "Complete Setup" button
- "Back" button to edit

## API Endpoints

| Method | Path                       | Description                      |
|--------|----------------------------|----------------------------------|
| GET    | `/api/setup/status`        | Check if setup is needed         |
| POST   | `/api/setup/complete`      | Complete setup with all settings |
| POST   | `/api/setup/validate-path` | Validate storage path            |

### POST /api/setup/complete

Request:
```json
{
  "username": "admin",
  "password": "securepassword",
  "storagePath": "/storage",
  "serverName": "My Filey Server"
}
```

Response (success):
```json
{
  "success": true,
  "redirectTo": "/login"
}
```

Response (error):
```json
{
  "success": false,
  "errors": {
    "username": "Username already exists",
    "storagePath": "Path is not writable"
  }
}
```
