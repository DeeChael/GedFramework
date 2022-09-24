package net.deechael.framework;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A page handler method must be static, and annotated by this class or UnknownPath
 * The first argument of the method must be net.deechael.framework.Request and the second must be net.deechael.framework.Responder
 * You can add more arguments, but they must be annotated by Argument because they used for accepting the arguments
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Path {

    /**
     * This is an array doesn't mean you should separate the path
     * you can redirect multiple path to one page
     * e.g. {"/list", "/map"}
     * user can access the page with "xxx.com/list" or "xxx.com/map"
     *
     * @return paths
     */
    @NotNull
    String value();

    /**
     * If ture, e.g. the value of path is {"/list"}
     * you can access the page with "xxx.com/list", "xxx.com/LiST" or whatever whose lowercase is equals to "list"
     *
     * @return ignored
     */
    boolean ignoreCaps() default false;

    /**
     * Match path with regex
     *
     * @return is regex
     */
    boolean regex() default false;

}
