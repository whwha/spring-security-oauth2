package nextstep.security.config.annotation.configurers;

import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.BasicAuthenticationFilter;
import nextstep.security.config.annotation.HttpSecurity;
import nextstep.security.config.annotation.SecurityConfigurer;

public class HttpBasicConfigurer implements SecurityConfigurer {
    @Override
    public void init(HttpSecurity http) {
    }

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        BasicAuthenticationFilter basicAuthenticationFilter = new BasicAuthenticationFilter(authenticationManager);
        http.addFilter(basicAuthenticationFilter);
    }
}
