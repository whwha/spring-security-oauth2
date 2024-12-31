package nextstep.oauth2.authentication;

import nextstep.oauth2.endpoint.OAuth2AuthorizationExchange;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.security.authentication.Authentication;

import java.util.Set;

public class OAuth2AuthorizationCodeAuthenticationToken implements Authentication {

    private final ClientRegistration clientRegistration;
    private final OAuth2AuthorizationExchange authorizationExchange;
    private final OAuth2AccessToken accessToken;

    public OAuth2AuthorizationCodeAuthenticationToken(ClientRegistration clientRegistration, OAuth2AuthorizationExchange authorizationExchange) {
        this.clientRegistration = clientRegistration;
        this.authorizationExchange = authorizationExchange;
        this.accessToken = null;
    }

    public OAuth2AuthorizationCodeAuthenticationToken(ClientRegistration clientRegistration, OAuth2AuthorizationExchange authorizationExchange, OAuth2AccessToken accessToken) {
        this.clientRegistration = clientRegistration;
        this.authorizationExchange = authorizationExchange;
        this.accessToken = accessToken;
    }


    @Override
    public Set<String> getAuthorities() {
        return Set.of();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    public ClientRegistration getClientRegistration() {
        return clientRegistration;
    }

    public OAuth2AuthorizationExchange getAuthorizationExchange() {
        return authorizationExchange;
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }
}
