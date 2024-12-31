package nextstep.oauth2.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import nextstep.oauth2.OAuth2AuthorizedClient;
import nextstep.security.authentication.Authentication;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;


public class OAuth2AuthorizedClientRepository {

    private static final String DEFAULT_AUTHORIZED_CLIENTS_ATTR_NAME =
            OAuth2AuthorizedClientRepository.class.getName() + ".AUTHORIZED_CLIENTS";

    private final String sessionAttributeName = DEFAULT_AUTHORIZED_CLIENTS_ATTR_NAME;

    public OAuth2AuthorizedClient loadAuthorizedClient(String clientRegistrationId,
                                                       Authentication principal,
                                                       HttpServletRequest request) {
        Assert.hasText(clientRegistrationId, "clientRegistrationId must not be empty");
        Assert.notNull(request, "request must not be null");
        return this.getAuthorizedClients(request).get(clientRegistrationId);
    }

    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient,
                                     Authentication principal,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        Assert.notNull(authorizedClient, "authorizedClient must not be null");
        Assert.notNull(request, "request must not be null");
        Assert.notNull(response, "response must not be null");
        Map<String, OAuth2AuthorizedClient> authorizedClients = getAuthorizedClients(request);
        authorizedClients.put(authorizedClient.getClientRegistration().getClientId(), authorizedClient);
        request.getSession().setAttribute(sessionAttributeName, authorizedClients);
    }

    public void removeAuthorizedClient(String clientRegistrationId,
                                       Authentication principal,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        Assert.hasText(clientRegistrationId, "clientRegistrationId must not be empty");
        Assert.notNull(request, "request must not be null");
        Map<String, OAuth2AuthorizedClient> authorizedClients = getAuthorizedClients(request);
        if (!authorizedClients.isEmpty()) {
            if (authorizedClients.remove(clientRegistrationId) != null) {
                if (!authorizedClients.isEmpty()) {
                    request.getSession().setAttribute(sessionAttributeName, authorizedClients);
                }
                else {
                    request.getSession().removeAttribute(sessionAttributeName);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, OAuth2AuthorizedClient> getAuthorizedClients(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Map<String, OAuth2AuthorizedClient> authorizedClients = (session != null) ?
                (Map<String, OAuth2AuthorizedClient>) session.getAttribute(sessionAttributeName) : null;
        if (authorizedClients == null) {
            authorizedClients = new HashMap<>();
        }
        return authorizedClients;
    }


}
