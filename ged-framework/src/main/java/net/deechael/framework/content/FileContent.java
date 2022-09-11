package net.deechael.framework.content;

import com.google.common.io.Files;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileContent implements Content {

    @Getter
    @Setter
    private File file;

    public FileContent(File file) {
        this.file = file;
    }

    public FileContent(String fileName) {
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
