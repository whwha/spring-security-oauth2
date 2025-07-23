package nextstep.security.authorization;

public class AuthorizationManagers {
    public static <T> AuthorizationManager<T> not(AuthorizationManager<T> manager) {
        return (authentication, object) -> {
            AuthorizationDecision decision = manager.check(authentication, object);
            if (decision == null) {
                return null;
            }
            return new NotAuthorizationDecision(decision);
        };
    }
    private static final class NotAuthorizationDecision extends AuthorizationDecision {

        private final AuthorizationDecision decision;

        private NotAuthorizationDecision(AuthorizationDecision decision) {
            super(!decision.isGranted());
            this.decision = decision;
        }

        @Override
        public String toString() {
            return "NotAuthorizationDecision [decision=" + this.decision + ']';
        }

    }
}
