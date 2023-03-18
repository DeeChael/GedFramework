package net.deechael.framework.content;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamContent implements Content {

    private byte[] bytes;

    public InputStreamContent(InputStream inputStream) {
        this.setInputStream(inputStream);
    }

    public void setInputStream(InputStream inputStream) {
        try {
            this.bytes = new byte[inputStream.available()];
            inputStream.read(this.bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

}
