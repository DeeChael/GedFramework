package net.deechael.framework;

import io.netty.handler.codec.http.cookie.Cookie;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.deechael.framework.content.Content;
import net.deechael.framework.content.StringContent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Responder {

    @Getter
    private final Map<String, String> headers = new HashMap<>();

    @Getter
    private final List<Cookie> cookies = new ArrayList<>();

    @Getter
    @Setter
    @NonNull
    @NotNull
    private ContentType contentType = ContentType.TEXT_PLAIN;

    @Getter
    @Setter
    private Content content = new StringContent("No content");

    public Responder() {

    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void addCookie(Cookie cookie) {
        this.cookies.add(cookie);
    }

}
