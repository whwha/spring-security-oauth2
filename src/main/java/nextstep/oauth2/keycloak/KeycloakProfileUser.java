package nextstep.oauth2.keycloak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.oauth2.OAuth2ProfileUser;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakProfileUser implements OAuth2ProfileUser {

    @JsonProperty("sub")
    private String id;
    private String name;
    @JsonProperty("preferred_username")
    private String imageUrl;
    private String email;

    public KeycloakProfileUser(Map<String, Object> attributes) {
        this.name = attributes.get("name").toString();
        this.imageUrl = attributes.get("preferred_username").toString();
        this.email = attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getEmail() {
        return email;
    }
}
