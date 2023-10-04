package info.colinhan.sisyphus.model;

import info.colinhan.sisyphus.context.VariableTypeValidationContext;

import java.util.HashMap;
import java.util.Map;

public final class VariableTypes {
    private VariableTypes() {
    }

    private static final Map<String, VariableType> simpleTypes = new HashMap<>();

    public static final VariableType STRING = new VariableType() {
        @Override
        public String validate(VariableTypeValidationContext context, Object value) {
            if (value == null) {
                return "Expected a string, but got null!";
            }
            if (value instanceof String) {
                return null;
            } else {
                return "Expected a string, but got a %s(%s) !".formatted(value.getClass().getSimpleName(), value);
            }
        }
    };

    public static final VariableType NUMBER = new VariableType() {
        @Override
        public String validate(VariableTypeValidationContext context, Object value) {
            if (value == null) {
                return "Expected a number, but got null!";
            }
            if (value instanceof Number) {
                return null;
            } else {
                return "Expected a number, but got a %s(%s) !".formatted(value.getClass().getSimpleName(), value);
            }
        }
    };

    public static final VariableType BOOLEAN = new VariableType() {
        @Override
        public String validate(VariableTypeValidationContext context, Object value) {
            if (value == null) {
                return "Expected a boolean, but got null!";
            }
            if (value instanceof Boolean) {
                return null;
            } else {
                return "Expected a boolean, but got a %s(%s) !".formatted(value.getClass().getSimpleName(), value);
            }
        }
    };

    public static final VariableType USER = new VariableType() {
        @Override
        public String validate(VariableTypeValidationContext context, Object value) {
            if (value == null) {
                return "Expected a user, but got null!";
            }
            if (value instanceof String) {
                return context.hasUser((String) value) ? null : "User %s does not exist!".formatted(value);
            } else {
                return "Expected a user, but got a %s(%s) !".formatted(value.getClass().getSimpleName(), value);
            }
        }
    };

    public static VariableType ENUM(String... items) {
        return (context, value) -> {
            if (value == null) {
                return "Expected one of %s, but got null!".formatted(String.join(", ", items));
            }
            if (value instanceof String) {
                for (String item : items) {
                    if (item.equalsIgnoreCase((String) value)) {
                        return null;
                    }
                }
                return "Expected one of %s, but got %s!".formatted(String.join(", ", items), value);
            } else {
                return "Expected one of %s, but got a %s(%s) !".formatted(String.join(", ", items), value.getClass().getSimpleName(), value);
            }
        };
    }

    static {
        simpleTypes.put("STRING", STRING);
        simpleTypes.put("NUMBER", NUMBER);
        simpleTypes.put("BOOLEAN", BOOLEAN);
        simpleTypes.put("USER", USER);
    }

    public static VariableType getType(String name) {
        return simpleTypes.get(name.toUpperCase());
    }
}
