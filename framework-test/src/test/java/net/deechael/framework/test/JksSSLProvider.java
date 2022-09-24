package net.deechael.framework.test;

import net.deechael.framework.ssl.SSLProvider;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * You should extend provider for a website,
 * every single ssl context require a class extending SSLProvider
 */
public class JksSSLProvider extends SSLProvider {

    @Override
    public SSLContext generate() {
        char[] passArray = "gedframework".toCharArray();
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream inputStream = new FileInputStream("local.jks");
            ks.load(inputStream, passArray);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, passArray);
            sslContext.init(kmf.getKeyManagers(), null, null);
            inputStream.close();
            return sslContext;
        } catch (NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException | KeyStoreException |
                 IOException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

}
