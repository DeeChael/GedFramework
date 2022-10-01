package net.deechael.framework.item;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * There must be at least one constructor annotated with this annotation in the Item annotated class
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface ItemConstructor {

    /**
     * The higher value of priority is, the higher chance the constructor will be invoked
     * @return priority
     */
    int priority() default 0;

}
