package net.deechael.framework.content;

import com.google.gson.JsonElement;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.InputStream;

public interface Content {

    byte[] getBytes();

    static Content bytes(byte[] bytes) {
        return new BytesContent(bytes);
    }

    static Content bytes(Byte[] bytes) {
        return new BytesContent(bytes);
    }

    static Content file(File file) {
        return new FileContent(file);
    }

    static Content json(JsonElement element) {
        return new JsonContent(element);
    }

    static Content jsoup(Document document) {
        return new JSoupContent(document);
    }

    static Content w3c(org.w3c.dom.Document document) {
        return new W3CContent(document);
    }

    static Content text(String text) {
        return new StringContent(text);
    }

    static Content redirect(String url) {
        return new RedirectContent(url);
    }

    static Content inputStream(InputStream inputStream) {
        return new InputStreamContent(inputStream);
    }

}
