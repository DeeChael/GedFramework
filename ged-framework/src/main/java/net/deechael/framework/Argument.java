package net.deechael.framework;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Should annotate the parameters in the page handler methods except for Request, Responder
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Argument {

    /**
     * e.g. "name" in xx.com/path?name=xxx
     *
     * @return the name of argument
     */
    @NotNull
    String value();

    /**
     * Used for parsing the value of the argument to the type you want
     *
     * @return the required type of the argument
     */
    @NotNull
    ArgumentType type();

}
