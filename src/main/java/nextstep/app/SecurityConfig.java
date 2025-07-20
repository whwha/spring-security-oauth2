package nextstep.app;

import nextstep.oauth2.OAuth2ClientProperties;
import nextstep.oauth2.registration.ClientRegistration;
import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.security.access.AnyRequestMatcher;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.hierarchicalroles.RoleHierarchy;
import nextstep.security.access.hierarchicalroles.RoleHierarchyImpl;
import nextstep.security.authorization.AuthorityAuthorizationManager;
import nextstep.security.authorization.PermitAllAuthorizationManager;
import nextstep.security.authorization.SecuredMethodInterceptor;
import nextstep.security.config.Customizer;
import nextstep.security.config.DelegatingFilterProxy;
import nextstep.security.config.FilterChainProxy;
import nextstep.security.config.SecurityFilterChain;
import nextstep.security.config.annotation.EnableWebSecurity;
import nextstep.security.config.annotation.HttpSecurity;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(OAuth2ClientProperties.class)
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2ClientProperties oAuth2ClientProperties;

    public SecurityConfig(OAuth2ClientProperties oAuth2ClientProperties) {
        this.oAuth2ClientProperties = oAuth2ClientProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain2(HttpSecurity http) {
        return http
                .csrf(c -> c.ignoringRequestMatchers("/login"))
                .authorizeHttpRequests(
                        authorizeHttpRequests -> authorizeHttpRequests
                                .addEntry(new MvcRequestMatcher(HttpMethod.GET, "/members"), new AuthorityAuthorizationManager(roleHierarchy(), "ADMIN"))
                                .addEntry(new MvcRequestMatcher(HttpMethod.GET, "/members/me"), new AuthorityAuthorizationManager(roleHierarchy(), "USER"))
                                .addEntry(AnyRequestMatcher.INSTANCE, new PermitAllAuthorizationManager()))
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .oauth2Login(Customizer.withDefaults())
                .build();
    }

    @Bean
    public SecuredMethodInterceptor securedMethodInterceptor() {
        return new SecuredMethodInterceptor();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.with()
                .role("ADMIN").implies("USER")
                .build();
    }

    @Bean
    public DelegatingFilterProxy delegatingFilterProxy(HttpSecurity httpSecurity) {
        return new DelegatingFilterProxy(filterChainProxy(List.of(securityFilterChain2(httpSecurity))));
    }

    @Bean
    public FilterChainProxy filterChainProxy(List<SecurityFilterChain> securityFilterChains) {
        return new FilterChainProxy(securityFilterChains);
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        Map<String, ClientRegistration> registrations = getClientRegistrations(oAuth2ClientProperties);
        return new ClientRegistrationRepository(registrations);
    }

    private static Map<String, ClientRegistration> getClientRegistrations(OAuth2ClientProperties properties) {
        Map<String, ClientRegistration> clientRegistrations = new HashMap<>();
        properties.getRegistration().forEach((key, value) -> clientRegistrations.put(key,
                getClientRegistration(key, value, properties.getProvider().get(key))));
        return clientRegistrations;
    }

    private static ClientRegistration getClientRegistration(String registrationId,
                                                            OAuth2ClientProperties.Registration registration, OAuth2ClientProperties.Provider provider) {
        return new ClientRegistration(registrationId, registration.getClientId(), registration.getClientSecret(), registration.getRedirectUri(), registration.getScope(), provider.getAuthorizationUri(), provider.getTokenUri(), provider.getUserInfoUri(), provider.getUserNameAttributeName());
    }
}

