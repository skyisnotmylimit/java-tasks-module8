package com.epam.jmp.service.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:jmp_test;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
        })
class RestApiIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void managesUsersAndSubscriptionsThroughRestResources() {
        ResponseEntity<String> swaggerUi = restTemplate.getForEntity("/swagger-ui/index.html", String.class);
        assertThat(swaggerUi.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<JsonNode> createdUser = restTemplate.postForEntity(
                "/api/v1/users",
                jsonRequest(Map.of(
                        "id", 999L,
                        "name", "Ada",
                        "surname", "Lovelace",
                        "birthday", "1815-12-10")),
                JsonNode.class);

        assertThat(createdUser.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createdUser.getHeaders().getLocation()).isNotNull();
        assertThat(createdUser.getBody().path("_links").path("self").path("href").asText())
                .isNotBlank();
        long userId = createdUser.getBody().path("id").asLong();

        ResponseEntity<JsonNode> updatedUser = restTemplate.exchange(
                "/api/v1/users/{id}",
                HttpMethod.PUT,
                jsonRequest(Map.of(
                        "id", userId,
                        "name", "Augusta",
                        "surname", "Lovelace",
                        "birthday", "1815-12-10")),
                JsonNode.class,
                userId);
        assertThat(updatedUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedUser.getBody().path("name").asText()).isEqualTo("Augusta");

        ResponseEntity<JsonNode> retrievedUser = restTemplate.getForEntity(
                "/api/v1/users/{id}", JsonNode.class, userId);
        assertThat(retrievedUser.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(retrievedUser.getBody().path("_links").path("users").path("href").asText())
                .isNotBlank();
        ResponseEntity<JsonNode> users = restTemplate.getForEntity("/api/v1/users", JsonNode.class);
        assertThat(users.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(users.getBody().path("_embedded").toString()).contains("userResponseDtoList");

        ResponseEntity<JsonNode> mismatchedUpdate = restTemplate.exchange(
                "/api/v1/users/{id}",
                HttpMethod.PUT,
                jsonRequest(Map.of(
                        "id", userId + 1,
                        "name", "Ada",
                        "surname", "Lovelace",
                        "birthday", "1815-12-10")),
                JsonNode.class,
                userId);
        assertThat(mismatchedUpdate.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ResponseEntity<JsonNode> createdSubscription = restTemplate.postForEntity(
                "/api/v1/subscriptions",
                jsonRequest(Map.of("userId", userId)),
                JsonNode.class);
        assertThat(createdSubscription.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        long subscriptionId = createdSubscription.getBody().path("id").asLong();
        assertThat(createdSubscription.getBody().path("userId").asLong()).isEqualTo(userId);

        ResponseEntity<JsonNode> updatedSubscription = restTemplate.exchange(
                "/api/v1/subscriptions/{id}",
                HttpMethod.PUT,
                jsonRequest(Map.of("id", subscriptionId, "userId", userId)),
                JsonNode.class,
                subscriptionId);
        assertThat(updatedSubscription.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<JsonNode> retrievedSubscription = restTemplate.getForEntity(
                "/api/v1/subscriptions/{id}", JsonNode.class, subscriptionId);
        assertThat(retrievedSubscription.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(retrievedSubscription.getBody().path("userId").asLong()).isEqualTo(userId);

        ResponseEntity<JsonNode> subscriptions = restTemplate.getForEntity(
                "/api/v1/subscriptions", JsonNode.class);
        assertThat(subscriptions.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(subscriptions.getBody().path("_embedded").toString())
                .contains("subscriptionResponseDtoList");

        restTemplate.delete("/api/v1/subscriptions/{id}", subscriptionId);
        ResponseEntity<JsonNode> deletedSubscription = restTemplate.getForEntity(
                "/api/v1/subscriptions/{id}", JsonNode.class, subscriptionId);
        assertThat(deletedSubscription.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        restTemplate.delete("/api/v1/users/{id}", userId);
        ResponseEntity<JsonNode> deletedUser = restTemplate.getForEntity(
                "/api/v1/users/{id}", JsonNode.class, userId);
        assertThat(deletedUser.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private HttpEntity<Map<String, Object>> jsonRequest(Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
