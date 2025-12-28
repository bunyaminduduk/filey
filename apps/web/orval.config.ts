import { defineConfig } from "orval";

export default defineConfig({
  api: {
    input: "http://localhost:8080/v3/api-docs",
    output: {
      target: "../../packages/api/src/generated/endpoints.ts",
      schemas: "../../packages/api/src/generated/schemas",
      client: "react-query",
      mode: "tags-split",
      override: {
        mutator: {
          path: "../../packages/api/src/client.ts",
          name: "api",
        },
        query: {
          useQuery: true,
          useMutation: true,
        },
      },
    },
  },
});
