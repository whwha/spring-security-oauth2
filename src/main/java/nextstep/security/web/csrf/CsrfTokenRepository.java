package nextstep.security.web.csrf;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.UUID;

public class CsrfTokenRepository {
    private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

    private static final String DEFAULT_CSRF_HEADER_NAME = "X-CSRF-TOKEN";

    private static final String DEFAULT_CSRF_TOKEN_ATTR_NAME = CsrfTokenRepository.class.getName()
            .concat(".CSRF_TOKEN");

    private String parameterName = DEFAULT_CSRF_PARAMETER_NAME;

    private String headerName = DEFAULT_CSRF_HEADER_NAME;

    private String sessionAttributeName = DEFAULT_CSRF_TOKEN_ATTR_NAME;

    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        if (token == null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute(this.sessionAttributeName);
            }
        } else {
            HttpSession session = request.getSession();
            session.setAttribute(this.sessionAttributeName, token);
        }
    }

    public CsrfToken loadToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (CsrfToken) session.getAttribute(this.sessionAttributeName);
    }

    public CsrfToken generateToken(HttpServletRequest request) {
        return new CsrfToken(this.headerName, this.parameterName, createNewToken());
    }

    private String createNewToken() {
        return UUID.randomUUID().toString();
    }
}

