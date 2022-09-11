package net.deechael.framework.content;

import com.google.common.io.Files;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileContent implements Content {

    @Getter
    @Setter
    @NonNull
    @NotNull
    private File file;

    public FileContent(@NotNull File file) {
        this.file = file;
    }

    public FileContent(@NonNull String fileName) {
        this(new File(fileName));
    }

    @Override
    public byte[] getBytes() {
        if (!file.exists()) {
            return "Failed to find the file, please contact the admin of the website!".getBytes(StandardCharsets.UTF_8);
        } else {
            try {
                return Files.toByteArray(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
