package nextstep.oauth2.registration;

import java.io.Serializable;
import java.util.Set;

public class ClientRegistration {

    private final String registrationId;
    private final String clientId;
    private final String clientSecret;

//    private ClientAuthenticationMethod clientAuthenticationMethod;
//    private AuthorizationGrantType authorizationGrantType;

    private final String redirectUri;
    private Set<String> scopes;
    private ProviderDetails providerDetails;

    public ClientRegistration(String registrationId,
                              String clientId,
                              String clientSecret,
                              String redirectUri,
                              Set<String> scopes,
                              String authorizationUri,
                              String tokenUri,
                              String uri,
                              String userNameAttributeName) {
        this.registrationId = registrationId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.scopes = scopes;
        this.providerDetails = new ProviderDetails(authorizationUri, tokenUri, uri, userNameAttributeName);
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public ProviderDetails getProviderDetails() {
        return providerDetails;
    }


    public class ProviderDetails implements Serializable {

        private final String authorizationUri;
        private final String tokenUri;
        private final UserInfoEndpoint userInfoEndpoint;

        public ProviderDetails(String authorizationUri, String tokenUri, String uri, String userNameAttributeName) {
            this.authorizationUri = authorizationUri;
            this.tokenUri = tokenUri;
            this.userInfoEndpoint = new UserInfoEndpoint(uri, userNameAttributeName);
        }

        public String getAuthorizationUri() {
            return authorizationUri;
        }

        public String getTokenUri() {
            return tokenUri;
        }

        public UserInfoEndpoint getUserInfoEndpoint() {
            return userInfoEndpoint;
        }

        public class UserInfoEndpoint implements Serializable {
            private final String uri;
            private final String userNameAttributeName;

            public UserInfoEndpoint(String uri, String userNameAttributeName) {
                this.uri = uri;
                this.userNameAttributeName = userNameAttributeName;
            }

            public String getUri() {
                return uri;
            }

            public String getUserNameAttributeName() {
                return userNameAttributeName;
            }
        }
    }

}
