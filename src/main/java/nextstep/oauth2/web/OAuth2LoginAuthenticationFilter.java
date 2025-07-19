package nextstep.oauth2.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.oauth2.OAuth2AuthorizedClient;
import nextstep.oauth2.authentication.OAuth2AuthenticationToken;
import nextstep.oauth2.authentication.OAuth2AuthorizationException;
import nextstep.oauth2.authentication.OAuth2LoginAuthenticationToken;
import nextstep.oauth2.authentication.OAuth2LoginAuthenticationProvider;
import nextstep.oauth2.endpoint.OAuth2AuthorizationExchange;
import nextstep.oauth2.endpoint.OAuth2AuthorizationRequest;
import nextstep.oauth2.endpoint.OAuth2AuthorizationResponse;
import nextstep.oauth2.endpoint.OAuth2AuthorizationResponseUtils;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.oauth2.userinfo.OAuth2User;
import nextstep.oauth2.userinfo.OAuth2UserService;
import nextstep.security.authentication.*;
import nextstep.security.context.SecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

public class OAuth2LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String DEFAULT_LOGIN_REQUEST_BASE_URI = "/login/oauth2/code/";

    private final ClientRegistrationRepository clientRegistrationRepository;
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;
    private final AuthorizationRequestRepository authorizationRequestRepository = new AuthorizationRequestRepository();

    private final Converter<OAuth2LoginAuthenticationToken, OAuth2AuthenticationToken> authenticationResultConverter = this::createAuthenticationResult;

    public OAuth2LoginAuthenticationFilter(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository authorizedClientRepository, AuthenticationManager authenticationManager) {
        super(DEFAULT_LOGIN_REQUEST_BASE_URI, authenticationManager);
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.authorizedClientRepository = authorizedClientRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // request 에서 parameter 가져오기
        MultiValueMap<String, String> params = OAuth2AuthorizationResponseUtils.toMultiMap(request.getParameterMap());
        if (!OAuth2AuthorizationResponseUtils.isAuthorizationResponse(params)) {
            throw new OAuth2AuthorizationException();
        }

        // session 에서 authorizationRequest 가져오기
        OAuth2AuthorizationRequest authorizationRequest = authorizationRequestRepository.loadAuthorizationRequest(request);
        if (authorizationRequest == null) {
            throw new OAuth2AuthorizationException();
        }

        // registrationId 를 가져오고 clientRegistration 을 가져오기
        String registrationId = extractRegistrationId(request);
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        if (clientRegistration == null) {
            throw new OAuth2AuthorizationException();
        }

        // code 를 포함한 authorization response 를 객체로 가져오기
        OAuth2AuthorizationResponse authorizationResponse = OAuth2AuthorizationResponseUtils.convert(params, clientRegistration.getRedirectUri());

        // access token 을 가져오기 위한 request 객체 만들기
        OAuth2LoginAuthenticationToken authenticationRequest =
                new OAuth2LoginAuthenticationToken(
                        clientRegistration,
                        new OAuth2AuthorizationExchange(authorizationRequest, authorizationResponse));

        // OAuth2LoginAuthenticationToken 만들기
        OAuth2LoginAuthenticationToken authenticationResult = (OAuth2LoginAuthenticationToken) getAuthenticationManager().authenticate(authenticationRequest);

        // provider 인증 후 authenticated 된 OAuth2AuthenticationToken 객체 가져오기
        OAuth2AuthenticationToken oAuth2AuthenticationToken = authenticationResultConverter.convert(authenticationResult);
        Assert.notNull(oAuth2AuthenticationToken, "Authentication result must not be null");

        // authorizedClientRepository 에 저장할 OAuth2AuthorizedClient 를 만들고 저장
        OAuth2AuthorizedClient authorizedClient = new OAuth2AuthorizedClient(
                authenticationResult.getClientRegistration(),
                oAuth2AuthenticationToken.getName(),
                authenticationResult.getAccessToken());

        authorizedClientRepository.saveAuthorizedClient(authorizedClient, oAuth2AuthenticationToken, request, response);
        return oAuth2AuthenticationToken;
    }

    private String extractRegistrationId(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.startsWith(DEFAULT_LOGIN_REQUEST_BASE_URI)) {
            return uri.substring(DEFAULT_LOGIN_REQUEST_BASE_URI.length());
        }
        return null;
    }

    private OAuth2AuthenticationToken createAuthenticationResult(OAuth2LoginAuthenticationToken authenticationResult) {
        var oAuth2User = (OAuth2User) authenticationResult.getPrincipal();
        return new OAuth2AuthenticationToken(oAuth2User, authenticationResult.getAuthorities());
    }
}
