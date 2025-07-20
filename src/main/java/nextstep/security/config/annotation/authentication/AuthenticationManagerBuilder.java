package nextstep.security.config.annotation.authentication;

import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.authentication.DaoAuthenticationProvider;
import nextstep.security.authentication.ProviderManager;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class AuthenticationManagerBuilder {

    private List<AuthenticationProvider> authenticationProviders = new ArrayList<>();

    public AuthenticationManagerBuilder(ApplicationContext context) {
        List<UserDetailsService> userDetailsServices = new ArrayList<>(context.getBeansOfType(UserDetailsService.class).values());
        if (userDetailsServices.isEmpty()) {
            return;
        }
        if (userDetailsServices.size() > 1) {
            return;
        }

        UserDetailsService userDetailsService = userDetailsServices.get(0);
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider(provider);
    }

    public AuthenticationManagerBuilder authenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProviders.add(authenticationProvider);
        return this;
    }

    public AuthenticationManager build() {
        ProviderManager providerManager = new ProviderManager(this.authenticationProviders);
        return providerManager;
    }
}
