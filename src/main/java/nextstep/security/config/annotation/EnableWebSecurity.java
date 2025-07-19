package nextstep.security.config.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HttpSecurityConfiguration.class)
public @interface EnableWebSecurity {
}
