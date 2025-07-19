package nextstep.security.config.annotation;

public interface SecurityConfigurer {
    void init(HttpSecurity http);

    void configure(HttpSecurity http);
}
