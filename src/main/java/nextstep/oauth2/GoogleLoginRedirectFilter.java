package nextstep.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class GoogleLoginRedirectFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_REQUEST_URI = "/oauth2/authorization/google";
    public static final String GOOGLE_AUTHORIZATION_URI = "https://accounts.google.com/o/oauth2/auth?";
    public static final String REDIRECT_URI = "http://localhost:8080/login/oauth2/code/google";


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!request.getRequestURI().equals(AUTHORIZATION_REQUEST_URI)) {
            filterChain.doFilter(request, response);
            return;
        }

        String paramsQuery = UriComponentsBuilder.newInstance()
                .queryParam("client_id", "706228114313-dc8oo7lvn4jdb0nagqnfmdo92gro2snd.apps.googleusercontent.com")
                .queryParam("response_type", "code")
                .queryParam("scope", "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email")
                .queryParam("redirect_uri", REDIRECT_URI)
                .build()
                .toUri()
                .getQuery();
        response.sendRedirect(GOOGLE_AUTHORIZATION_URI + paramsQuery);
    }
}
