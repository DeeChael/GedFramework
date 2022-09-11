package net.deechael.framework.content;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class StringContent implements Content {

    @Getter
    @Setter
    @NonNull
    @NotNull
    private String text;

    public StringContent(@NonNull String text) {
        this.text = text;
    }

    @Override
    public byte[] getBytes() {
        return this.text.getBytes(StandardCharsets.UTF_8);
    }

}
