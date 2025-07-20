package nextstep.security.config.annotation;

import jakarta.servlet.*;
import nextstep.security.authentication.AuthenticationManager;
import nextstep.security.authentication.AuthenticationProvider;
import nextstep.security.config.Customizer;
import nextstep.security.config.DefaultSecurityFilterChain;
import nextstep.security.config.annotation.authentication.AuthenticationManagerBuilder;
import nextstep.security.config.annotation.configurers.*;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.*;

public class HttpSecurity {
    private final LinkedHashMap<Class<? extends SecurityConfigurer>, SecurityConfigurer> configurers = new LinkedHashMap<>();

    private List<OrderedFilter> filters = new ArrayList<>();

    private FilterOrderRegistration filterOrders = new FilterOrderRegistration();

    private final Map<Class<?>, Object> sharedObjects = new HashMap<>();

    public HttpSecurity(AuthenticationManagerBuilder authenticationManagerBuilder, Map<Class<?>, Object> sharedObjects) {
        setSharedObject(AuthenticationManagerBuilder.class, authenticationManagerBuilder);
        for (Map.Entry<Class<?>, Object> entry : sharedObjects.entrySet()) {
            setSharedObject((Class<Object>) entry.getKey(), entry.getValue());
        }
    }

    public <C> C getSharedObject(Class<C> sharedType) {
        return (C) this.sharedObjects.get(sharedType);
    }

    public <C> void setSharedObject(Class<C> sharedType, C object) {
        this.sharedObjects.put(sharedType, object);
    }

    private void init() {
        for (SecurityConfigurer configurer : this.configurers.values()) {
            configurer.init(this);
        }
    }

    private void beforeConfigure() {
        AuthenticationManager manager = getAuthenticationRegistry().build();
        setSharedObject(AuthenticationManager.class, manager);
    }

    private void configure() {
        for (SecurityConfigurer configurer : this.configurers.values()) {
            configurer.configure(this);
        }
    }

    public DefaultSecurityFilterChain build() {
        init();
        beforeConfigure();
        configure();
        return performBuild();
    }

    private DefaultSecurityFilterChain performBuild() {
        this.filters.sort(OrderComparator.INSTANCE);

        List<Filter> sortedFilters = new ArrayList<>(this.filters.size());
        for (Filter filter : this.filters) {
            sortedFilters.add(((OrderedFilter) filter).filter);
        }
        return new DefaultSecurityFilterChain(sortedFilters);
    }

    public HttpSecurity addFilter(Filter filter) {
        Integer order = this.filterOrders.getOrder(filter.getClass());
        if (order == null) {
            throw new IllegalArgumentException();
        }
        filters.add(new OrderedFilter(filter, order));
        return this;
    }

    public HttpSecurity authenticationProvider(AuthenticationProvider authenticationProvider) {
        getAuthenticationRegistry().authenticationProvider(authenticationProvider);
        return this;
    }

    private AuthenticationManagerBuilder getAuthenticationRegistry() {
        return getSharedObject(AuthenticationManagerBuilder.class);
    }

    public HttpSecurity csrf(Customizer<CsrfConfigurer> csrfCustomizer) {
        csrfCustomizer.customize(getOrApply(new CsrfConfigurer()));
        return HttpSecurity.this;
    }

    public HttpSecurity httpBasic(Customizer<HttpBasicConfigurer> httpBasicCustomizer) {
        httpBasicCustomizer.customize(getOrApply(new HttpBasicConfigurer()));
        return HttpSecurity.this;
    }

    public HttpSecurity formLogin(Customizer<FormLoginConfigurer> formLoginCustomizer) {
        formLoginCustomizer.customize(getOrApply(new FormLoginConfigurer()));
        return HttpSecurity.this;
    }

    public HttpSecurity oauth2Login(Customizer<OAuth2LoginConfigurer> oauth2LoginCustomizer) {
        oauth2LoginCustomizer.customize(getOrApply(new OAuth2LoginConfigurer()));
        return HttpSecurity.this;
    }

    public HttpSecurity authorizeHttpRequests(Customizer<AuthorizeHttpRequestsConfigurer> authorizeHttpRequestsCustomizer) {
        authorizeHttpRequestsCustomizer.customize(getOrApply(new AuthorizeHttpRequestsConfigurer()));
        return HttpSecurity.this;
    }

    public HttpSecurity securityContext(Customizer<SecurityContextConfigurer> securityContextCustomizer) {
        securityContextCustomizer.customize(getOrApply(new SecurityContextConfigurer()));
        return HttpSecurity.this;
    }

    private <C extends SecurityConfigurer> C getOrApply(C configurer) {
        Class<? extends SecurityConfigurer> clazz = configurer.getClass();
        C existingConfig = (C) this.configurers.get(clazz);
        if (existingConfig != null) {
            return existingConfig;
        }
        this.configurers.put(clazz, configurer);
        return configurer;
    }

    private static final class OrderedFilter implements Ordered, Filter {

        private final Filter filter;

        private final int order;

        private OrderedFilter(Filter filter, int order) {
            this.filter = filter;
            this.order = order;
        }

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
                throws IOException, ServletException {
            this.filter.doFilter(servletRequest, servletResponse, filterChain);
        }

        @Override
        public int getOrder() {
            return this.order;
        }

        @Override
        public String toString() {
            return "OrderedFilter{" + "filter=" + this.filter + ", order=" + this.order + '}';
        }
    }
}
