package net.deechael.framework;

import net.deechael.framework.response.ResponseListener;
import net.deechael.framework.ssl.SSLProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * To describe a class is a website handler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Website {

    String favicon() default "";

    /**
     * The website will start on this port
     *
     * @return the port
     */
    int port() default 80;

    /**
     * TODO, used for annotation processor
     *
     * @return listeners
     */
    Class<? extends ResponseListener>[] listeners() default {};

    /**
     * Use https protocol
     * sslKeyFile and sslPassword cannot be null
     *
     * @return if ssl
     */
    boolean ssl() default false;

    /**
     * Provide SSLContext
     *
     * @return ssl provider
     */
    Class<? extends SSLProvider> sslProvider() default SSLProvider.class;

}
