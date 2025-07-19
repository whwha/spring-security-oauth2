package nextstep.security.web.csrf;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.access.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CsrfFilter extends OncePerRequestFilter {
    public static final RequestMatcher DEFAULT_CSRF_MATCHER = new DefaultRequiresCsrfMatcher();

    private final RequestMatcher requireCsrfProtectionMatcher = DEFAULT_CSRF_MATCHER;
    private final AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandler();
    private CsrfTokenRepository tokenRepository = new CsrfTokenRepository();

    private final Set<MvcRequestMatcher> ignoringRequestMatchers;

    public CsrfFilter(Set<MvcRequestMatcher> ignoringRequestMatchers) {
        this.ignoringRequestMatchers = ignoringRequestMatchers;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (ignoringRequestMatchers.stream().anyMatch(matcher -> matcher.matches(request))) {
            filterChain.doFilter(request, response);
            return;
        }

        CsrfToken csrfToken = this.tokenRepository.loadToken(request);
        boolean missingToken = (csrfToken == null);
        if (missingToken) {
            csrfToken = this.tokenRepository.generateToken(request);
            this.tokenRepository.saveToken(csrfToken, request, response);
        }
        request.setAttribute(CsrfToken.class.getName(), csrfToken);
        if (!this.requireCsrfProtectionMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String actualToken = request.getHeader(csrfToken.getHeaderName());
        if (actualToken == null) {
            actualToken = request.getParameter(csrfToken.getParameterName());
        }
        if (!Objects.equals(csrfToken.getToken(), actualToken)) {
            this.accessDeniedHandler.handle(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static final class DefaultRequiresCsrfMatcher implements RequestMatcher {

        private final HashSet<String> allowedMethods = new HashSet<>(Arrays.asList("GET", "HEAD", "TRACE", "OPTIONS"));

        @Override
        public boolean matches(HttpServletRequest request) {
            return !this.allowedMethods.contains(request.getMethod());
        }

        @Override
        public String toString() {
            return "CsrfNotRequired " + this.allowedMethods;
        }

    }
}
