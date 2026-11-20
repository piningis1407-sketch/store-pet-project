package com.iharshashkou.internetstore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloudflare.workers-ai")
public record CloudflareWorkersAiProperties(
        String accountId,
        String apiToken,
        String model,
        String baseUrl
) {
}