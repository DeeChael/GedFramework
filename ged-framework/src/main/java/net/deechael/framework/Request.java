package net.deechael.framework;

import io.netty.handler.codec.http.cookie.Cookie;
import lombok.Getter;

import java.util.List;
import java.util.Map;

public class Request {

    @Getter
    private final String[] paths;
    @Getter
    private final Map<String, String> arguments;
    @Getter
    private final HttpMethod method;
    @Getter
    private final String fullUrl;
    @Getter
    private final String host;
    @Getter
    private final Map<String, String> headers;
    @Getter
    private final List<Cookie> cookies;

    public Request(String[] paths, Map<String, String> arguments, HttpMethod method, String fullUrl, String host, Map<String, String> headers, List<Cookie> cookies) {
        this.paths = paths;
        this.arguments = arguments;
        this.method = method;
        this.fullUrl = fullUrl;
        this.host = host;
        this.headers = headers;
        this.cookies = cookies;
    }

}
