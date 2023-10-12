package info.colinhan.sisyphus.context;

import info.colinhan.sisyphus.model.ReferenceType;
import info.colinhan.sisyphus.model.VariableType;

public interface VariableValidationContext {

    /**
     * Check if a user exists.
     * @param username The username / email to check.
     * @return True if the user exists, false otherwise.
     */
    boolean hasUser(String username);

    VariableType getVariableType(ReferenceType type, String name);
}
