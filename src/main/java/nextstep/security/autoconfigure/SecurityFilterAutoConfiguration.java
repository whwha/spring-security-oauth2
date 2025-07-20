package nextstep.security.autoconfigure;

import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.context.annotation.Bean;

public class SecurityFilterAutoConfiguration {

    private static final String DEFAULT_FILTER_NAME = "springSecurityFilterChain";

    @Bean
    public DelegatingFilterProxyRegistrationBean securityFilterChainRegistration() {
        DelegatingFilterProxyRegistrationBean registration = new DelegatingFilterProxyRegistrationBean(DEFAULT_FILTER_NAME);
        return registration;
    }
}
