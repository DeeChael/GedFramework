package net.deechael.framework.content;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jsoup.nodes.Document;

import java.nio.charset.StandardCharsets;

public class JSoupContent implements Content {

    @Getter
    @Setter
    @NonNull
    @NotNull
    private Document document;

    public JSoupContent(@NotNull Document document) {
        this.document = document;
    }

    @Override
    public byte[] getBytes() {
        return document.toString().getBytes(StandardCharsets.UTF_8);
    }
}
