package nextstep.security.config.annotation;

import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.config.Customizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class HttpSecurityConfiguration {

    private static final String BEAN_NAME_PREFIX = "nextstep.security.config.annotation.HttpSecurityConfiguration.";

    private static final String HTTPSECURITY_BEAN_NAME = BEAN_NAME_PREFIX + "httpSecurity";

    @Autowired
    private ApplicationContext context;

    @Bean(HTTPSECURITY_BEAN_NAME)
    @Scope("prototype")
    HttpSecurity httpSecurity(AuthenticationManager authenticationManager) {
        return new HttpSecurity(authenticationManager, createSharedObjects())
                .securityContext(Customizer.withDefaults());
    }

    private Map<Class<?>, Object> createSharedObjects() {
        Map<Class<?>, Object> sharedObjects = new HashMap<>();
        sharedObjects.put(ApplicationContext.class, this.context);
        return sharedObjects;
    }
}
