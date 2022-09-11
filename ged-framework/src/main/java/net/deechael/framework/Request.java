package net.deechael.framework;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.netty.handler.codec.http.cookie.Cookie;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Request {

    @Getter
    @NonNull
    @NotNull
    private final String[] paths;
    @Getter
    @NonNull
    @NotNull
    private final Map<String, String> arguments;
    @Getter
    @NonNull
    @NotNull
    private final HttpMethod method;
    @Getter
    @NonNull
    @NotNull
    private final String userAddress;
    @Getter
    @NonNull
    @NotNull
    private final String fullUrl;
    @Getter
    @NonNull
    @NotNull
    private final String host;
    @Getter
    @NonNull
    @NotNull
    private final Map<String, String> headers;
    @Getter
    @NonNull
    @NotNull
    private final List<Cookie> cookies;
    @Getter
    private final byte[] body;

    public Request(@NotNull String[] paths, @NotNull Map<String, String> arguments, @NotNull HttpMethod method, @NotNull String userAddress, @NotNull String fullUrl, @NotNull String host, @NotNull Map<String, String> headers, @NotNull List<Cookie> cookies, byte[] body) {
        this.paths = paths;
        this.arguments = arguments;
        this.method = method;
        this.userAddress = userAddress;
        this.fullUrl = fullUrl;
        this.host = host;
        this.headers = headers;
        this.cookies = cookies;
        this.body = body;
    }

    @NotNull
    public JsonElement asJson() {
        return JsonParser.parseString(asText());
    }

    @NotNull
    public String asText() {
        return new String(this.getBody());
    }

}
