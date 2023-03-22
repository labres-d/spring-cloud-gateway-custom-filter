package com.labres.gatewayapi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class GatewayApiApplicationTest {

    private TestRestTemplate template = new TestRestTemplate();

    @Test
    public void fireRequests() {
        Arrays.asList("user1", "user2", "user3").parallelStream().forEach(s -> {
            HttpEntity entity = getHeaders(s);
            for(int i = 0; i < 50_000_000; i++) tryGet(entity);
        });
    }

    public void tryGet(HttpEntity entity) {
        ResponseEntity<Object> r = template.exchange("http://localhost:8080/v1/transactions", HttpMethod.GET, entity, Object.class, 1);
        log.info("Received: status->{}, payload->{}, remaining->{}", r.getStatusCode(), r.getBody(), r.getHeaders().get("X-RateLimit-Remaining"));
    }

    private HttpEntity getHeaders(String user){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", user);
        return new HttpEntity(headers);
    }
}