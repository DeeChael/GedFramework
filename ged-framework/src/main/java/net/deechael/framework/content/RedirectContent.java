package net.deechael.framework.content;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class RedirectContent implements Content {

    private final static String FORMAT = "" +
            "<head>\n" +
            "  <meta http-equiv=\"refresh\" content=\"0;url=%s\">\n" +
            "</head>";

    @Getter
    @Setter
    @NonNull
    @NotNull
    private String url;

    public RedirectContent(@NonNull String url) {
        this.url = url;
    }

    @Override
    public byte[] getBytes() {
        return String.format(FORMAT, this.url).getBytes(StandardCharsets.UTF_8);
    }

}
