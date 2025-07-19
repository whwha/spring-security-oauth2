package nextstep.security.web.csrf;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class AccessDeniedHandler {
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(HttpStatus.FORBIDDEN.value());
    }
}
