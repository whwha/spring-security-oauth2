package nextstep.oauth2;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class GoogleRequestClient {

    private static final String YOUR_CLIENT_SECRET = "YOUR_CLIENT_SECRET";

//    private static final String GOOGLE_TOKEN_URL = "https://accounts.google.com/o/oauth2/token";
//    public static final String GOOGLE_PROFILE_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    private static final String GOOGLE_TOKEN_URL = "http://localhost:8089/o/oauth2/token";
    private static final String GOOGLE_PROFILE_URL = "http://localhost:8089/oauth2/v1/userinfo";
    private final RestTemplate restTemplate = new RestTemplate();

    public String requestAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        Map<String, String> body = new HashMap<>();
        body.put("client_id", "706228114313-dc8oo7lvn4jdb0nagqnfmdo92gro2snd.apps.googleusercontent.com");
        body.put("client_secret", YOUR_CLIENT_SECRET);
        body.put("code", code);
        body.put("redirect_uri", GoogleLoginRedirectFilter.REDIRECT_URI);
        body.put("grant_type", "authorization_code");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(GOOGLE_TOKEN_URL, HttpMethod.POST, request, Map.class);
        return (String) response.getBody().get("access_token");
    }

    public Map<String, String> requestUserProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(GOOGLE_PROFILE_URL, HttpMethod.GET, request, Map.class);
        return response.getBody();
    }
}
