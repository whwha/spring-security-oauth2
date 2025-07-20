package nextstep.security.config.annotation.configurers;

import nextstep.oauth2.authentication.OAuth2LoginAuthenticationProvider;
import nextstep.oauth2.userinfo.DefaultOAuth2UserService;
import nextstep.oauth2.userinfo.OAuth2UserService;
import nextstep.oauth2.web.OAuth2AuthorizationRequestRedirectFilter;
import nextstep.oauth2.web.OAuth2LoginAuthenticationFilter;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.config.annotation.HttpSecurity;
import nextstep.security.config.annotation.SecurityConfigurer;
import org.springframework.context.ApplicationContext;

public class OAuth2LoginConfigurer implements SecurityConfigurer {
    private OAuth2LoginAuthenticationFilter authFilter;

    @Override
    public void init(HttpSecurity http) {
        OAuth2LoginAuthenticationFilter authenticationFilter = new OAuth2LoginAuthenticationFilter(
                OAuth2ClientConfigurerUtils.getClientRegistrationRepository(http),
                OAuth2ClientConfigurerUtils.getAuthorizedClientRepository(http));
        this.authFilter = authenticationFilter;

        OAuth2UserService oauth2UserService = getOAuth2UserService(http);
        OAuth2LoginAuthenticationProvider oauth2LoginAuthenticationProvider = new OAuth2LoginAuthenticationProvider(oauth2UserService);
        http.authenticationProvider(oauth2LoginAuthenticationProvider);
    }

    @Override
    public void configure(HttpSecurity http) {
        OAuth2AuthorizationRequestRedirectFilter authorizationRequestFilter =
                new OAuth2AuthorizationRequestRedirectFilter(OAuth2ClientConfigurerUtils.getClientRegistrationRepository(http));
        http.addFilter(authorizationRequestFilter);

        OAuth2LoginAuthenticationFilter authenticationFilter = this.authFilter;
        authenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        http.addFilter(authenticationFilter);
    }

    private OAuth2UserService getOAuth2UserService(HttpSecurity http) {
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        OAuth2UserService bean = context.getBean(OAuth2UserService.class);
        return (bean != null) ? bean : new DefaultOAuth2UserService();
    }
}
