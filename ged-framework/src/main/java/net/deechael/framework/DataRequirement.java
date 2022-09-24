package net.deechael.framework;

public enum DataRequirement {

    /**
     * User can see the page without setting the value
     */
    OPTIONAL,
    /**
     * User cannot see any page
     */
    REQUIRED_CLOSE,
    /**
     * User will redirect to unknown path annotated method
     */
    REQUIRED_GO_TO_UNKNOWN_PATH

}
