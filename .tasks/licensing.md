# Licensing & Business Model

## Approach

- **Fully open source**: Single public repo, all code visible (core + premium features)
- **Single Docker image**: One image for everyone
- **License validation at runtime**: Premium features unlocked with valid license
- **Graceful fallback**: License expires → premium features disable, core keeps working
- **Subscription model**: License enables premium features for a period; updates always available to everyone

## Why This Model?

### Transparency

Users can see exactly what happens with their files. No hidden code, no mystery. This builds trust for a product that handles personal data.

### Simpler Maintenance

- One codebase, one Docker image
- No separate "community" vs "enterprise" builds
- No syncing between open/closed repos

### Based on Industry Trends

JetBrains recently unified IntelliJ Community and Ultimate into a single distribution. Their reasoning:
- Reduces testing overhead
- Simpler user experience
- "The boundary between open-source and proprietary components blurred over time"

### Practical Reality

If someone forks the code and removes license checks:
- They have to maintain their fork
- They don't get updates
- They weren't going to pay anyway

The effort to bypass licensing exceeds the cost of a license for most people.

## Core Features (Free Forever)

- File upload, download, view, delete
- Folder navigation and management
- User accounts with username/password
- Basic permissions
- File previews (images, videos)
- Responsive web UI
- Resumable uploads
- Thumbnail generation

## Premium Features (License Required)

- SSO integration
- Team management
- Advanced sharing (external links, expiring, password-protected)
- Audit logging
- SMB and other protocol support
- Priority support

## How Licensing Works

### Subscription Model

- Purchase a license for a period (e.g., 1 year)
- Premium features enabled during the license period
- Updates always available to everyone (free and premium)
- When license expires:
  - Core features continue working
  - Premium features disable gracefully
  - Renew to re-enable premium features

### Token-Based Activation

1. User runs Filey, enters license key in setup or settings
2. Filey validates token (can work offline after initial validation)
3. Token contains expiry date
4. Premium features enabled until expiry
5. Periodic re-validation (when online)

### Upgrade Path

Upgrading from free to premium is seamless:
- Same Docker image
- Same database
- Same files
- Just add a license key