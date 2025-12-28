package com.filey.config;

import com.filey.exception.ErrorCode;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Filey API")
                        .description("Self-hosted file server API")
                        .version("0.0.1"))
                .components(new Components()
                        .addSchemas("ErrorCode", createErrorCodeSchema())
                        .addSchemas("ErrorResponse", createErrorResponseSchema())
                        .addResponses("BadRequest", createErrorResponse("Bad request"))
                        .addResponses("Unauthorized", createErrorResponse("Authentication required"))
                        .addResponses("Forbidden", createErrorResponse("Access denied"))
                        .addResponses("NotFound", createErrorResponse("Resource not found"))
                        .addResponses("InternalError", createErrorResponse("Internal server error")));
    }

    @SuppressWarnings("unchecked")
    private Schema<String> createErrorCodeSchema() {
        var enumValues = Arrays.stream(ErrorCode.values())
                .map(Enum::name)
                .toList();

        return new StringSchema()
                .description("Error code identifier")
                ._enum(enumValues);
    }

    @SuppressWarnings("rawtypes")
    private Schema createErrorResponseSchema() {
        return new Schema<>()
                .type("object")
                .description("Error response")
                .addProperty("code", new Schema<>().$ref("#/components/schemas/ErrorCode"))
                .addProperty("message", new StringSchema().description("Human-readable error message"))
                .addProperty("status", new Schema<Integer>().type("integer").description("HTTP status code"));
    }

    private ApiResponse createErrorResponse(String description) {
        return new ApiResponse()
                .description(description)
                .content(new Content()
                        .addMediaType("application/json", new MediaType()
                                .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))));
    }
}