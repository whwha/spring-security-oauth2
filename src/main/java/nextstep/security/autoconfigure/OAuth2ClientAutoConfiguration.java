package nextstep.security.autoconfigure;

import nextstep.oauth2.registration.ClientRegistration;
import nextstep.security.config.annotation.EnableWebSecurity;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration(before = SecurityAutoConfiguration.class)
@ConditionalOnClass({EnableWebSecurity.class, ClientRegistration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({OAuth2ClientRegistrationRepositoryConfiguration.class})
public class OAuth2ClientAutoConfiguration {

}
