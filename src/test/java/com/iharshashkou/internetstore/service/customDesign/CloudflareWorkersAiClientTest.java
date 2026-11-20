package com.iharshashkou.internetstore.service.customDesign;

import com.iharshashkou.internetstore.config.CloudflareWorkersAiProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CloudflareWorkersAiClientTest {

    @Test
    void generatesImageSuccessfully() {

        CloudflareWorkersAiProperties properties =
                new CloudflareWorkersAiProperties(
                        "test-account",
                        "test-token",
                        "@cf/black-forest-labs/flux-2-klein-9b",
                        "https://api.cloudflare.test/client/v4/accounts"
                );

        RestClient.Builder builder = RestClient.builder();

        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CloudflareWorkersAiClient client =
                new CloudflareWorkersAiClient(properties, builder);

        server.expect(requestTo(
                        "https://api.cloudflare.test/client/v4/accounts/test-account/ai/run/@cf/black-forest-labs/flux-2-klein-9b"
                ))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(
                        "Authorization",
                        "Bearer test-token"
                ))
                .andExpect(content().contentTypeCompatibleWith(
                        MediaType.MULTIPART_FORM_DATA
                ))
                .andRespond(withSuccess(
                        """
                        {
                            "result": {
                                "image": "AQIDBAU="
                            }
                        }
                        """,
                        MediaType.APPLICATION_JSON
                ));

        byte[] result = client.generateImage("test prompt");

        assertArrayEquals(
                new byte[]{1, 2, 3, 4, 5},
                result
        );

        server.verify();
    }

    @Test
    void throwsExceptionWhenCloudflareDoesNotReturnImage() {

        CloudflareWorkersAiProperties properties =
                new CloudflareWorkersAiProperties(
                        "test-account",
                        "test-token",
                        "@cf/black-forest-labs/flux-2-klein-9b",
                        "https://api.cloudflare.test/client/v4/accounts"
                );

        RestClient.Builder builder = RestClient.builder();

        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CloudflareWorkersAiClient client =
                new CloudflareWorkersAiClient(properties, builder);

        server.expect(requestTo(
                        "https://api.cloudflare.test/client/v4/accounts/test-account/ai/run/@cf/black-forest-labs/flux-2-klein-9b"
                ))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(
                        """
                        {
                            "result": {}
                        }
                        """,
                        MediaType.APPLICATION_JSON
                ));

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> client.generateImage("test prompt")
        );

        server.verify();
    }

    @Test
    void throwsExceptionWhenImageBase64IsInvalid() {

        CloudflareWorkersAiProperties properties =
                new CloudflareWorkersAiProperties(
                        "test-account",
                        "test-token",
                        "@cf/black-forest-labs/flux-2-klein-9b",
                        "https://api.cloudflare.test/client/v4/accounts"
                );

        RestClient.Builder builder = RestClient.builder();

        MockRestServiceServer server =
                MockRestServiceServer.bindTo(builder).build();

        CloudflareWorkersAiClient client =
                new CloudflareWorkersAiClient(properties, builder);

        server.expect(requestTo(
                        "https://api.cloudflare.test/client/v4/accounts/test-account/ai/run/@cf/black-forest-labs/flux-2-klein-9b"
                ))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(
                        """
                        {
                            "result": {
                                "image": "not-valid-base64!!!"
                            }
                        }
                        """,
                        MediaType.APPLICATION_JSON
                ));

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> client.generateImage("test prompt")
        );

        server.verify();
    }
}
