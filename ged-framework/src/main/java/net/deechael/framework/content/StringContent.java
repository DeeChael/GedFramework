package net.deechael.framework.content;

import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

public class StringContent implements Content {

    @Getter
    @Setter
    private String text;

    public StringContent(String text) {
        this.text = text;
    }

    @Override
    public byte[] getBytes() {
        return this.text.getBytes(StandardCharsets.UTF_8);
    }

}
