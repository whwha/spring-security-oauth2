package nextstep.security.access;

import jakarta.servlet.http.HttpServletRequest;

public class NegatedRequestMatcher implements RequestMatcher {
    private final RequestMatcher requestMatcher;

    public NegatedRequestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return !this.requestMatcher.matches(request);
    }
}
