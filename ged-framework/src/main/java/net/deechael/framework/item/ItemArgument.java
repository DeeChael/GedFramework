package net.deechael.framework.item;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All the parameters of an ItemConstructor annotated constructor should be annotated with this annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ItemArgument {

    /**
     * The type of the argument
     * @return string, double, int, boolean or header(string)
     */
    ItemArgumentType type();

    /**
     * When the type is header, this will be the header key,
     * or else will input the value in arguments (if exists)
     *
     * @return key
     */
    String name();

    /**
     * If true, the name is missed, this constructor will be skipped
     *
     * @return if it is required
     */
    boolean require() default false;

}
