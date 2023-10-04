package info.colinhan.sisyphus.model;

import info.colinhan.sisyphus.context.VariableTypeValidationContext;

public interface VariableType {

    String validate(VariableTypeValidationContext context, Object value);
}
