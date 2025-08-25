package com.hackathon2_BE.pium.service;

import com.hackathon2_BE.pium.dto.FastApiResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpHeaders;

@Service
public class FastApiClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String FASTAPI_URL = "http://localhost:8001/predict"; // FastAPI 엔드포인트

    public FastApiResponse sendToFastApi(MultipartFile file) {
        try {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<FastApiResponse> response = restTemplate.exchange(
                    FASTAPI_URL,
                    HttpMethod.POST,
                    requestEntity,
                    FastApiResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 호출 실패", e);
        }
    }
}
