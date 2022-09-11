package net.deechael.framework.content;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class JsonContent implements Content {

    private final static Gson GSON = new Gson();

    @Getter
    @Setter
    @NonNull
    @NotNull
    private JsonElement element;

    public JsonContent(@NonNull JsonElement element) {
        this.element = element;
    }

    @Override
    public byte[] getBytes() {
        return GSON.toJson(this.element).getBytes(StandardCharsets.UTF_8);
    }

}
