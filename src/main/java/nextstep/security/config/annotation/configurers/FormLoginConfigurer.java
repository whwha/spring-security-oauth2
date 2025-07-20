package nextstep.security.config.annotation.configurers;

import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.UsernamePasswordAuthenticationFilter;
import nextstep.security.config.annotation.HttpSecurity;
import nextstep.security.config.annotation.SecurityConfigurer;


public class FormLoginConfigurer implements SecurityConfigurer {
    @Override
    public void init(HttpSecurity http) {

    }

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter(authenticationManager);
        http.addFilter(filter);
    }
}
