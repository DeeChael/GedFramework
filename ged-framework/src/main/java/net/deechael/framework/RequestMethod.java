package net.deechael.framework;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To limit the request methods
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestMethod {

    /**
     * To limit the allowed request method like GET, POST
     *
     * @return methods
     */
    @NotNull
    HttpMethod[] value();

}
