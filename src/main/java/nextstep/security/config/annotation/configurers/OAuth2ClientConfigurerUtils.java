package nextstep.security.config.annotation.configurers;

import nextstep.oauth2.registration.ClientRegistrationRepository;
import nextstep.oauth2.web.OAuth2AuthorizedClientRepository;
import nextstep.security.config.annotation.HttpSecurity;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Map;

public class OAuth2ClientConfigurerUtils {
    public static ClientRegistrationRepository getClientRegistrationRepository(HttpSecurity builder) {
        ClientRegistrationRepository clientRegistrationRepository = builder.getSharedObject(ClientRegistrationRepository.class);
        if (clientRegistrationRepository == null) {
            clientRegistrationRepository = getClientRegistrationRepositoryBean(builder);
            builder.setSharedObject(ClientRegistrationRepository.class, clientRegistrationRepository);
        }
        return clientRegistrationRepository;
    }

    private static ClientRegistrationRepository getClientRegistrationRepositoryBean(HttpSecurity builder) {
        return builder.getSharedObject(ApplicationContext.class).getBean(ClientRegistrationRepository.class);
    }

    public static OAuth2AuthorizedClientRepository getAuthorizedClientRepository(HttpSecurity builder) {
        OAuth2AuthorizedClientRepository authorizedClientRepository = builder
                .getSharedObject(OAuth2AuthorizedClientRepository.class);
        if (authorizedClientRepository == null) {
            authorizedClientRepository = getAuthorizedClientRepositoryBean(builder);
            if (authorizedClientRepository == null) {
                authorizedClientRepository = new OAuth2AuthorizedClientRepository();
            }
            builder.setSharedObject(OAuth2AuthorizedClientRepository.class, authorizedClientRepository);
        }
        return authorizedClientRepository;
    }

    private static OAuth2AuthorizedClientRepository getAuthorizedClientRepositoryBean(HttpSecurity builder) {
        Map<String, OAuth2AuthorizedClientRepository> authorizedClientRepositoryMap = BeanFactoryUtils
                .beansOfTypeIncludingAncestors(builder.getSharedObject(ApplicationContext.class),
                        OAuth2AuthorizedClientRepository.class);
        if (authorizedClientRepositoryMap.size() > 1) {
            throw new NoUniqueBeanDefinitionException(OAuth2AuthorizedClientRepository.class,
                    authorizedClientRepositoryMap.size(),
                    "Expected single matching bean of type '" + OAuth2AuthorizedClientRepository.class.getName()
                            + "' but found " + authorizedClientRepositoryMap.size() + ": "
                            + StringUtils.collectionToCommaDelimitedString(authorizedClientRepositoryMap.keySet()));
        }
        return (!authorizedClientRepositoryMap.isEmpty() ? authorizedClientRepositoryMap.values().iterator().next()
                : null);
    }
}
