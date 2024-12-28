package nextstep.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class OAuth2LoginRedirectFilter extends GenericFilterBean {

    public static final String DEFAULT_AUTHORIZATION_REQUEST_BASE_URI = "/oauth2/authorization/";

    private final OAuth2ClientProperties oAuth2ClientProperties;

    public OAuth2LoginRedirectFilter(OAuth2ClientProperties oAuth2ClientProperties) {
        this.oAuth2ClientProperties = oAuth2ClientProperties;
    }

    private String extractRegistrationId(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.startsWith(DEFAULT_AUTHORIZATION_REQUEST_BASE_URI)) {
            return uri.substring(DEFAULT_AUTHORIZATION_REQUEST_BASE_URI.length());
        }
        return null;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String registrationId = extractRegistrationId(request);
        if (registrationId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        OAuth2ClientProperties.Provider provider = oAuth2ClientProperties.getProvider().get(registrationId);
        OAuth2ClientProperties.Registration registration = oAuth2ClientProperties.getRegistration().get(registrationId);

        String paramsQuery = UriComponentsBuilder.newInstance()
                .queryParam("client_id", registration.getClientId())
                .queryParam("response_type", "code")
                .queryParam("scope", registration.getScope())
                .queryParam("redirect_uri", registration.getRedirectUri())
                .build()
                .toUri()
                .getQuery();
        response.sendRedirect(provider.getAuthorizationUri() + "?" + paramsQuery);
    }
}
