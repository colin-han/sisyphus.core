package info.colinhan.sisyphus.model;

import info.colinhan.sisyphus.context.VariableValidationContext;

import java.util.*;
import java.util.stream.Collectors;

public final class VariableTypes {
    private VariableTypes() {
    }

    private static Map<String, VariableType> registeredTypes = new HashMap<>();

    public static void registerType(VariableType type) {
        String name = type.getName().toUpperCase();
        if (registeredTypes.containsKey(name)) {
            throw new IllegalArgumentException("VariableType %s is already registered!".formatted(type.getName()));
        }
        registeredTypes.put(name, type);
    }

    private static abstract class AbstractType implements VariableType {
        private final String name;

        public AbstractType(String name) {
            this.name = name;
            registerType(this);
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private static class SimpleType extends AbstractType {
        private final List<Class<?>> acceptableValueTypes = new ArrayList<>();
        private final List<VariableType> acceptableVariableTypes = new ArrayList<>();
        private final List<String> acceptableVariableTypeNames = new ArrayList<>();

        public SimpleType(String name, Object... acceptableArray) {
            super(name);
            for (Object acceptable : acceptableArray) {
                if (acceptable instanceof VariableType variableType) {
                    acceptableVariableTypes.add(variableType);
                } else if (acceptable instanceof Class<?> valueType) {
                    acceptableValueTypes.add(valueType);
                } else if (acceptable instanceof String typeName) {
                    acceptableVariableTypeNames.add(typeName);
                } else {
                    throw new IllegalArgumentException("Unknown acceptable type: " + acceptable);
                }
            }
        }

        @Override
        public String validate(VariableValidationContext context, Object value) {
            if (value == null) {
                return "Expected a %s, but got null!".formatted(this.getName());
            }
            if (value == this) {
                return null;
            }
            if (value instanceof VariableType variableType) {
                if (acceptableVariableTypes.contains(variableType)) {
                    return null;
                } else if (acceptableVariableTypeNames.contains(variableType.getName())) {
                    String name = variableType.getName();
                    VariableType type = VariableTypes.getType(name);
                    if (type != variableType) {
                        throw new IllegalArgumentException("VariableType %s is not registered!".formatted(name));
                    }
                    acceptableVariableTypeNames.remove(name);
                    acceptableVariableTypes.add(variableType);
                    return null;
                } else {
                    return "Expected a %s, but got a %s variable !".formatted(this.getName(), variableType.getName());
                }
            }
            for (VariableType assignableFrom : acceptableVariableTypes) {
                String error = assignableFrom.validate(context, value);
                if (error == null) {
                    return null;
                }
            }
            for (Class<?> assignableFrom : acceptableValueTypes) {
                if (assignableFrom.isInstance(value)) {
                    return null;
                }
            }
            return "Expected a %s, but got a %s(%s) !".formatted(this.getName(), value.getClass().getSimpleName(), value);
        }
    }

    private static class UnknownType extends AbstractType {
        public UnknownType() {
            super("UNKNOWN");
        }

        @Override
        public String validate(VariableValidationContext context, Object value) {
            return null;
        }
    }
    public static final VariableType UNKNOWN = new UnknownType();

    private static class AnyType extends AbstractType {
        public AnyType() {
            super("ANY");
        }

        @Override
        public String validate(VariableValidationContext context, Object value) {
            return null;
        }
    }
    public static final VariableType ANY = new AnyType();

    private static class StringType extends SimpleType {
        public StringType() {
            super("STRING", String.class);
        }
    }
    public static final VariableType STRING = new StringType();

    private static class NumberType extends SimpleType {
        public NumberType() {
            super("NUMBER", Number.class);
        }
    }
    public static final VariableType NUMBER = new NumberType();

    private static class BooleanType extends SimpleType {
        public BooleanType() {
            super("BOOLEAN", Boolean.class);
        }
    }
    public static final VariableType BOOLEAN = new BooleanType();

    private static class UserType extends AbstractType {
        public UserType() {
            super("USER");
        }

        @Override
        public String validate(VariableValidationContext context, Object value) {
            if (value == null) {
                return "Expected a USER, but got null!";
            }
            if (value == this) {
                return null;
            }
            if (value instanceof VariableType variableType) {
                return "Expected a USER, but got a %s variable !".formatted(variableType.getName());
            }
            if (value instanceof String) {
                return context.hasUser((String) value) ? null : "User %s does not exist!".formatted(value);
            } else {
                return "Expected a USER, but got a %s(%s) !".formatted(value.getClass().getSimpleName(), value);
            }
        }
    }
    public static final VariableType USER = new UserType();

    private static class EnumType extends AbstractType {
        private final List<String> items;

        public EnumType(String name, List<String> items) {
            super(name);
            this.items = items;
        }

        @Override
        public String validate(VariableValidationContext context, Object value) {
            if (value == null) {
                return "Expected one of %s, but got null!".formatted(String.join(", ", items));
            }
            if (value == this) {
                return null;
            }
            if (value instanceof String str) {
                String v = str.toUpperCase();
                if (items.contains(v)) {
                    return null;
                }
                return "Expected one of %s, but got %s!".formatted(String.join(", ", items), value);
            } else {
                return "Expected one of %s, but got a %s(%s) !".formatted(String.join(", ", items), value.getClass().getSimpleName(), value);
            }
        }
    }
    public static VariableType ENUM(String... items) {
        List<String> itemList = Arrays.stream(items)
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
        String name = "ENUM(" + String.join(", ", itemList) + ")";

        if (registeredTypes.containsKey(name)) {
            return registeredTypes.get(name);
        }

        return new EnumType(name, itemList);
    }

    private static class ArrayType extends AbstractType {
        private final VariableType itemType;

        public ArrayType(String name, VariableType itemType) {
            super(name);
            this.itemType = itemType;
        }
        @Override
        public String validate(VariableValidationContext context, Object value) {
            if (value == null) {
                return "Expected an ARRAY, but got null!";
            }
            if (value == this) {
                return null;
            }
            if (value instanceof Iterable) {
                for (Object item : (Iterable<?>) value) {
                    String error = itemType.validate(context, item);
                    if (error != null) {
                        return error;
                    }
                }
                return null;
            } else {
                return "Expected an ARRAY, but got a %s(%s) !".formatted(value.getClass().getSimpleName(), value);
            }
        }
    }
    public static VariableType ARRAY(VariableType itemType) {
        String name = "ARRAY(%s)".formatted(itemType.getName());

        if (registeredTypes.containsKey(name)) {
            return registeredTypes.get(name);
        }

        return new ArrayType(name, itemType);
    }

    private static class OptionalType extends AbstractType {
        private final VariableType type;

        public OptionalType(VariableType type) {
            super("OPTIONAL(%s)".formatted(type.getName()));
            this.type = type;
        }

        @Override
        public String validate(VariableValidationContext context, Object value) {
            if (value == null) {
                return null;
            }
            if (value == this) {
                return null;
            }
            return type.validate(context, value);
        }
    }
    public static VariableType OPTIONAL(VariableType type) {
        if (type instanceof OptionalType) {
            return type;
        }

        String name = "OPTIONAL(%s)".formatted(type.getName());

        if (registeredTypes.containsKey(name)) {
            return registeredTypes.get(name);
        }

        return new OptionalType(type);
    }

    public static VariableType getType(String name) {
        return registeredTypes.get(name.toUpperCase());
    }
}
