package net.deechael.framework;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Header {

    /**
     * The key of the header
     * Ignoring caps
     *
     * @return key name
     */
    @NotNull
    String value();

    /**
     * If the header is required
     *
     * @return the requirement
     */
    @NotNull
    DataRequirement requirement() default DataRequirement.OPTIONAL;

}
