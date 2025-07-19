package nextstep.security.web.csrf;

public class CsrfToken {
    private String headerName;
    private String parameterName;
    private String token;

    public CsrfToken(String headerName, String parameterName, String token) {
        this.headerName = headerName;
        this.parameterName = parameterName;
        this.token = token;
    }

    public String getHeaderName() {
        return headerName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getToken() {
        return token;
    }
}
