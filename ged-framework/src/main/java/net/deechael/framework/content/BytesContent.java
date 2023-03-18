package net.deechael.framework.content;

import lombok.Setter;

public class BytesContent implements Content {

    @Setter
    private byte[] bytes;

    public BytesContent(byte[] bytes) {
        this.bytes = bytes;
    }

    public BytesContent(Byte[] bytes) {
        this.bytes = new byte[bytes.length];
        System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
