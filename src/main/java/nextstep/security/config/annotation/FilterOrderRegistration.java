package nextstep.security.config.annotation;

import jakarta.servlet.Filter;
import nextstep.security.authentication.BasicAuthenticationFilter;
import nextstep.security.authentication.UsernamePasswordAuthenticationFilter;
import nextstep.security.authorization.AuthorizationFilter;
import nextstep.security.context.SecurityContextHolderFilter;
import nextstep.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CorsFilter;

import java.util.HashMap;
import java.util.Map;

public class FilterOrderRegistration {
    private static final int INITIAL_ORDER = 100;

    private static final int ORDER_STEP = 100;

    private final Map<String, Integer> filterToOrder = new HashMap<>();

    FilterOrderRegistration() {
        Step order = new Step(INITIAL_ORDER, ORDER_STEP);
//        put(DisableEncodeUrlFilter.class, order.next());
//        put(ForceEagerSessionCreationFilter.class, order.next());
//        put(ChannelProcessingFilter.class, order.next());
        order.next(); // gh-8105
//        put(WebAsyncManagerIntegrationFilter.class, order.next());
        put(SecurityContextHolderFilter.class, order.next());
//        put(HeaderWriterFilter.class, order.next());
        put(CorsFilter.class, order.next());
        put(CsrfFilter.class, order.next());
//        put(LogoutFilter.class, order.next());
        this.filterToOrder.put(
                "nextstep.oauth2.web.OAuth2AuthorizationRequestRedirectFilter",
                order.next());
//        this.filterToOrder.put(
//                "org.springframework.security.saml2.provider.service.web.Saml2WebSsoAuthenticationRequestFilter",
//                order.next());
//        put(X509AuthenticationFilter.class, order.next());
//        put(AbstractPreAuthenticatedProcessingFilter.class, order.next());
//        this.filterToOrder.put("org.springframework.security.cas.web.CasAuthenticationFilter", order.next());
        this.filterToOrder.put("nextstep.oauth2.web.OAuth2LoginAuthenticationFilter",
                order.next());
//        this.filterToOrder.put(
//                "org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter",
//                order.next());
        put(UsernamePasswordAuthenticationFilter.class, order.next());
        order.next(); // gh-8105
//        put(DefaultLoginPageGeneratingFilter.class, order.next());
//        put(DefaultLogoutPageGeneratingFilter.class, order.next());
//        put(ConcurrentSessionFilter.class, order.next());
//        put(DigestAuthenticationFilter.class, order.next());
//        this.filterToOrder.put(
//                "org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter",
//                order.next());
        put(BasicAuthenticationFilter.class, order.next());
//        put(RequestCacheAwareFilter.class, order.next());
//        put(SecurityContextHolderAwareRequestFilter.class, order.next());
//        put(JaasApiIntegrationFilter.class, order.next());
//        put(RememberMeAuthenticationFilter.class, order.next());
//        put(AnonymousAuthenticationFilter.class, order.next());
//        this.filterToOrder.put("org.springframework.security.oauth2.client.web.OAuth2AuthorizationCodeGrantFilter",
//                order.next());
//        put(SessionManagementFilter.class, order.next());
//        put(ExceptionTranslationFilter.class, order.next());
//        put(FilterSecurityInterceptor.class, order.next());
        put(AuthorizationFilter.class, order.next());
//        put(SwitchUserFilter.class, order.next());
    }

    void put(Class<? extends Filter> filter, int position) {
        this.filterToOrder.putIfAbsent(filter.getName(), position);
    }

    Integer getOrder(Class<?> clazz) {
        while (clazz != null) {
            Integer result = this.filterToOrder.get(clazz.getName());
            if (result != null) {
                return result;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private static class Step {

        private int value;

        private final int stepSize;

        Step(int initialValue, int stepSize) {
            this.value = initialValue;
            this.stepSize = stepSize;
        }

        int next() {
            int value = this.value;
            this.value += this.stepSize;
            return value;
        }

    }
}
