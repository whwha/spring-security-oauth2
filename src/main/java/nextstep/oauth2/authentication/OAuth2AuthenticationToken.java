package nextstep.oauth2.authentication;

import nextstep.oauth2.userinfo.OAuth2User;
import nextstep.security.authentication.Authentication;
import org.springframework.util.Assert;

import java.util.Set;

public class OAuth2AuthenticationToken implements Authentication {

    private final OAuth2User principal;
    private final Set<String> authorities;
    private final boolean authenticated;

    public OAuth2AuthenticationToken(OAuth2User principal, Set<String> authorities, boolean authenticated) {
        Assert.notNull(principal, "principal must not be null");
        this.principal = principal;
        this.authorities = authorities;
        this.authenticated = authenticated;
    }

    public OAuth2AuthenticationToken(OAuth2User principal, Set<String> authorities) {
        Assert.notNull(principal, "principal must not be null");
        this.principal = principal;
        this.authorities = authorities;
        this.authenticated = true;
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

    public String getName() {
        return principal.toString();
    }
}
