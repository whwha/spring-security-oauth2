package nextstep.security.authentication;

import nextstep.security.access.MvcRequestMatcher;
import nextstep.security.context.SecurityContextRepository;
import nextstep.security.context.SecurityContext;
import nextstep.security.context.SecurityContextHolder;
import nextstep.security.userdetails.UserDetailsService;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final MvcRequestMatcher DEFAULT_MVC_REQUEST_MATCHER = new MvcRequestMatcher(HttpMethod.POST, "/login");
    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
    public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

    public UsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_MVC_REQUEST_MATCHER, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String username = obtainUsername(request);
        username = (username != null) ? username.trim() : "";
        String password = obtainPassword(request);
        password = (password != null) ? password : "";

        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private String obtainUsername(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_USERNAME_KEY);
    }

    private String obtainPassword(HttpServletRequest request) {
        return request.getParameter(SPRING_SECURITY_FORM_PASSWORD_KEY);
    }
}
