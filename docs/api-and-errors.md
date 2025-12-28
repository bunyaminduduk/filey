# API Client & Error Handling

## Overview

The project uses:
- **Backend**: SpringDoc OpenAPI to generate API specification
- **Frontend**: Orval to generate TypeScript clients with TanStack Query hooks
- **Shared Package**: `@filey/api` contains all generated API code

## Backend: Throwing Errors

### Using ApiException

```java
import com.filey.exception.ApiException;
import com.filey.exception.ErrorCode;

// Throw with default message
throw new ApiException(ErrorCode.FILE_NOT_FOUND);

// Throw with custom message
throw new ApiException(ErrorCode.VALIDATION_ERROR, "Email is required");
```

Add new error codes in `com.filey.exception.ErrorCode`. The TypeScript `ErrorCode` type is auto-generated from this enum when you run `pnpm api:generate`.

### Documenting Endpoints

Use OpenAPI annotations for documentation:

```java
@RestController
@RequestMapping("/api/files")
@Tag(name = "Files", description = "File operations")
public class FilesController {

    @GetMapping("/{id}")
    @Operation(summary = "Get file", description = "Retrieves file metadata by ID")
    public FileResponse getFile(@PathVariable String id) {
        // ...
    }
}
```

### Request Validation

Use Jakarta validation annotations with `@Valid`:

```java
// DTO
public record CreateUserRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @Size(min = 8, message = "Password must be at least 8 characters")
    String password
) {}

// Controller
@PostMapping
public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
    // Validation errors are automatically caught and returned as VALIDATION_ERROR
}
```

### Response DTOs

Place response/request DTOs in `com.filey.dto`:

```java
package com.filey.dto;

@Schema(description = "File metadata response")
public record FileResponse(
    @Schema(description = "Unique file identifier")
    String id,

    @Schema(description = "Original filename")
    String name,

    @Schema(description = "File size in bytes")
    long size
) {}
```

## Frontend: Using the API

### Generating the Client

```bash
# Start the backend first
cd apps/server && ./gradlew bootRun

# Generate TypeScript clients (in another terminal)
cd apps/web && pnpm api:generate
```

This generates files in `packages/api/src/`:
- `generated/{tag}/{tag}.ts` - Hooks and functions per controller tag
- `generated/schemas/` - TypeScript types
- `client.ts` - Custom fetch client with error handling

### Using Hooks

```typescript
import { useHealth, useListFiles, useCreateFile } from "@filey/api";

function MyComponent() {
  // Query hook (GET requests)
  const { data, error, isLoading } = useListFiles();

  // Mutation hook (POST, PUT, DELETE)
  const { mutate: createFile, isPending } = useCreateFile();

  const handleCreate = () => {
    createFile({ name: "example.txt" }, {
      onSuccess: (data) => console.log("Created:", data),
      onError: (error) => console.error("Failed:", error),
    });
  };
}
```

### Handling Errors

```typescript
import { isApiError, ErrorCodes } from "@filey/api";

function MyComponent() {
  const { data, error } = useGetFile("123");

  if (error) {
    if (isApiError(error)) {
      // Typed error with code, message, status
      console.log(error.code);    // "FILE_NOT_FOUND"
      console.log(error.message); // "File not found"
      console.log(error.status);  // 404

      // Check specific error using ErrorCodes constant
      if (error.is(ErrorCodes.FILE_NOT_FOUND)) {
        return <p>File does not exist</p>;
      }
    }
    return <p>Something went wrong</p>;
  }

  return <div>{data?.name}</div>;
}
```

### Raw Function Calls

For non-hook usage (e.g., in loaders or actions):

```typescript
import { health, getFile } from "@filey/api";

// Direct API call
const data = await health();
const file = await getFile("123");
```

## Adding New Endpoints

When you add a new controller/endpoint:

1. Add OpenAPI annotations to the Java controller
2. Run `pnpm api:generate` (with backend running)
3. Update `packages/api/src/index.ts` to export the new endpoints:

```typescript
// Add new export for the new controller
export * from "./generated/files/files";
```

## Adding New Error Codes

When you need a new error code:

1. Add it to `com.filey.exception.ErrorCode` in Java:

```java
// In ErrorCode.java
MY_NEW_ERROR(HttpStatus.BAD_REQUEST, "Something went wrong");
```

2. Run `pnpm api:generate` (with backend running)
3. The new error code is automatically available in TypeScript:

```typescript
import { ErrorCodes } from "@filey/api";

error.is(ErrorCodes.MY_NEW_ERROR);
```

## Swagger UI

When the backend is running, Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON spec:

```
http://localhost:8080/v3/api-docs
```