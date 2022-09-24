package net.deechael.framework.ssl;

import javax.net.ssl.SSLContext;

public abstract class SSLProvider {

    protected SSLProvider() {

    }

    public abstract SSLContext generate();

}
