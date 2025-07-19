package nextstep.oauth2;

import nextstep.oauth2.github.GithubProfileUser;
import nextstep.oauth2.google.GoogleProfileUser;
import nextstep.oauth2.keycloak.KeycloakProfileUser;

import java.util.Map;

public interface OAuth2ProfileUser {

    static OAuth2ProfileUser of(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return new GoogleProfileUser(attributes);
        }
        if ("github".equals(registrationId)) {
            return new GithubProfileUser(attributes);
        }
        if ("keycloak".equals(registrationId)) {
            return new KeycloakProfileUser(attributes);
        }
        throw new IllegalArgumentException("Unknown registration id: " + registrationId);
    }

    String getName();
    String getImageUrl();
    String getEmail();
}
