package net.deechael.framework.content;

import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Document;

import java.nio.charset.StandardCharsets;

public class JSoupContent implements Content {

    @Getter
    @Setter
    private Document document;

    public JSoupContent(Document document) {
        this.document = document;
    }

    @Override
    public byte[] getBytes() {
        return document.toString().getBytes(StandardCharsets.UTF_8);
    }
}
