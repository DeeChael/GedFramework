package net.deechael.framework;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To limit the hosts
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Host {

    /**
     * Example:
     * If your value is "deechael.net", to browse the path, the user must access the website through "deechael.net"
     *
     * @return allowed host
     */
    @NotNull
    String value();

    /**
     * Match host with regex
     *
     * @return if regex
     */
    boolean regex() default false;

    /**
     * Match port, -1 means that port won't be detected
     *
     * @return port integer
     */
    int port() default -1;

}
