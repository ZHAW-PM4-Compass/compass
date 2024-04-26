package ch.zhaw.pm4.compass.backend.controller;


import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;


public class TestUtils {

    /**
     * Initialize the MockMvc with a valid auth0 token
     * @param mockMvc MockMvc to initialize
     * @return token
     * @throws Exception
     */
    public static String initMockWithToken(MockMvc mockMvc) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = System.getenv("AUTH0_ISSUER_BASE_URL")+"oauth/token";

        JSONObject requestJson = new JSONObject();
        requestJson.put("grant_type", "client_credentials");
        requestJson.put("client_id", System.getenv("AUTH0_MGMT_CLIENT_ID"));
        requestJson.put("client_secret", System.getenv("AUTH0_MGMT_CLIENT_SECRET"));

        requestJson.put("audience", System.getenv("AUTH0_MGMT_AUDIENCE"));


        HttpEntity<String> request = new HttpEntity<String>(requestJson.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        String responseBody = response.getBody();
        JSONObject json = new JSONObject(responseBody);
        String accessToken = json.getString("access_token");
        return accessToken;
    }
}
