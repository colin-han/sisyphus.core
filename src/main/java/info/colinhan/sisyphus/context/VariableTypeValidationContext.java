package info.colinhan.sisyphus.context;

public interface VariableTypeValidationContext {

    /**
     * Check if a user exists.
     * @param username The username / email to check.
     * @return True if the user exists, false otherwise.
     */
    boolean hasUser(String username);
}
