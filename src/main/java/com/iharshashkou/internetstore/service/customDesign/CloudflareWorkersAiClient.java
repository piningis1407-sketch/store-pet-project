package com.iharshashkou.internetstore.service.customDesign;

import com.iharshashkou.internetstore.config.CloudflareWorkersAiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import tools.jackson.databind.JsonNode;
import java.util.Base64;

@Component
public class CloudflareWorkersAiClient {

    private final CloudflareWorkersAiProperties properties;
    private final RestClient restClient;

    @Autowired
    public CloudflareWorkersAiClient(
            CloudflareWorkersAiProperties properties
    ) {
        this(properties, RestClient.builder());
    }

    CloudflareWorkersAiClient(
            CloudflareWorkersAiProperties properties,
            RestClient.Builder builder
    ) {
        this.properties = properties;

        this.restClient = builder
                .baseUrl(properties.baseUrl())
                .defaultHeader(
                        "Authorization",
                        "Bearer " + properties.apiToken()
                )
                .build();
    }

    public byte[] generateImage(String prompt) {

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("prompt", prompt);
        parts.add("width", "1024");
        parts.add("height", "768");

        String endpoint = "/"
                + properties.accountId()
                + "/ai/run/"
                + properties.model();

        JsonNode response = restClient.post()
                .uri(endpoint)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .body(parts)
                .retrieve()
                .body(JsonNode.class);

        if (response == null
                || !response.path("result").hasNonNull("image")) {
            throw new IllegalStateException(
                    "Cloudflare не вернул изображение"
            );
        }

        String base64Image = response
                .path("result")
                .path("image")
                .asText();

        return Base64.getDecoder().decode(base64Image);
    }
}
