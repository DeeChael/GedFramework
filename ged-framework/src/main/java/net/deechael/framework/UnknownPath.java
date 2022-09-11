package net.deechael.framework;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If the static method is annotated by this annotation, it is not allowed to add argument in the method parameters and the Host, RequestMethod annotation will be ignored
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UnknownPath {
}
