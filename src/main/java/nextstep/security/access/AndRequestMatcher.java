package nextstep.security.access;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

public class AndRequestMatcher implements RequestMatcher {
    private final List<RequestMatcher> requestMatchers;

    public AndRequestMatcher(List<RequestMatcher> requestMatchers) {
        Assert.notEmpty(requestMatchers, "requestMatchers must contain a value");
        Assert.noNullElements(requestMatchers, "requestMatchers cannot contain null values");
        this.requestMatchers = requestMatchers;
    }

    public AndRequestMatcher(RequestMatcher... requestMatchers) {
        this(Arrays.asList(requestMatchers));
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        for (RequestMatcher matcher : this.requestMatchers) {
            if (!matcher.matches(request)) {
                return false;
            }
        }
        return true;
    }
}
