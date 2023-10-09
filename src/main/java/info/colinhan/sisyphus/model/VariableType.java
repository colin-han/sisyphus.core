package info.colinhan.sisyphus.model;

import info.colinhan.sisyphus.context.VariableValidationContext;

public interface VariableType {
    String validate(VariableValidationContext context, Object value);

    String getName();
}
