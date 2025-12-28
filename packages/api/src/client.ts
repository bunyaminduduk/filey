import type { ErrorCode } from "./generated/schemas/errorCode";

export type { ErrorCode } from "./generated/schemas/errorCode";
export { ErrorCode as ErrorCodes } from "./generated/schemas/errorCode";

export interface ApiErrorResponse {
    code: ErrorCode;
    message: string;
    status: number;
}

export class ApiError extends Error {
    public readonly code: ErrorCode;
    public readonly status: number;

    constructor(response: ApiErrorResponse) {
        super(response.message);
        this.name = "ApiError";
        this.code = response.code;
        this.status = response.status;
    }

    is(code: ErrorCode): boolean {
        return this.code === code;
    }
}

export function isApiError(error: unknown): error is ApiError {
    return error instanceof ApiError;
}

async function parseErrorResponse(response: Response): Promise<ApiError> {
    try {
        const data = (await response.json()) as ApiErrorResponse;
        return new ApiError(data);
    } catch {
        return new ApiError({
            code: "INTERNAL_ERROR",
            message: "An unexpected error occurred",
            status: response.status,
        });
    }
}

type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

interface ApiConfig<TData = unknown> {
    url: string;
    method: HttpMethod;
    params?: Record<string, string>;
    data?: TData;
    headers?: Record<string, string>;
    signal?: AbortSignal;
}

export async function api<TResponse, TData = unknown>(
    config: ApiConfig<TData>
): Promise<TResponse> {
    const { url, method, params, data, headers, signal } = config;

    const queryString = params ? "?" + new URLSearchParams(params).toString() : "";

    const response = await fetch(`${url}${queryString}`, {
        method,
        headers: {
            "Content-Type": "application/json",
            ...headers,
        },
        body: data ? JSON.stringify(data) : undefined,
        signal,
    });

    if (!response.ok) {
        throw await parseErrorResponse(response);
    }

    if (response.status === 204) {
        return undefined as TResponse;
    }

    return response.json() as Promise<TResponse>;
}
