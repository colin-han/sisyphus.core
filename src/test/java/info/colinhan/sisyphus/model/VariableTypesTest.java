package info.colinhan.sisyphus.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static info.colinhan.sisyphus.model.VariableTypes.*;
import static org.junit.jupiter.api.Assertions.*;

class VariableTypesTest {

    private static void assertNotAssignableFrom(VariableType type, Object value) {
        assertNotNull(type.validate(null, value));
    }

    private static void assertAssignableFrom(VariableType left, Object value) {
        assertNull(left.validate(null, value));
    }

    @Test
    void enum_type_define() {
        assertEquals(ENUM("a", "b"), ENUM("b", "a"));
        assertEquals(ENUM("a", "a"), ENUM("a"));
        assertEquals(ENUM("a"), ENUM("A"));
        assertEquals(ENUM("ABC"), ENUM("Abc"));
    }

    @Test
    void enum_type_compatible() {
        assertAssignableFrom(ENUM("a", "b"), ENUM("a"));
        assertNotAssignableFrom(ENUM("a", "b"), ENUM("c"));
        assertNotAssignableFrom(ENUM("a", "b"), ENUM("a", "c"));
        assertNotAssignableFrom(ENUM("a", "b"), ENUM("a", "b", "c"));
        assertAssignableFrom(ENUM("a", "b"), ENUM("b", "a"));
    }

    @Test
    void string_accept_enum() {
        assertAssignableFrom(STRING, ENUM("a"));
    }

    @Test
    void string_type() {
        assertAssignableFrom(STRING, "a");
        assertNotAssignableFrom(STRING, 1);
        assertNotAssignableFrom(STRING, null);
        assertAssignableFrom(STRING, STRING);
        assertAssignableFrom(STRING, String.class);
    }

    @Test
    void number_type() {
        assertAssignableFrom(NUMBER, 1);
        assertAssignableFrom(NUMBER, 1.0);
        assertAssignableFrom(NUMBER, 1L);
        assertAssignableFrom(NUMBER, 1.0D);
        assertAssignableFrom(NUMBER, 1.0F);
        assertAssignableFrom(NUMBER, BigDecimal.valueOf(1));
        assertNotAssignableFrom(NUMBER,"a");
        assertNotAssignableFrom(NUMBER,null);
        assertAssignableFrom(NUMBER, NUMBER);
        assertAssignableFrom(NUMBER, Double.class);
        assertAssignableFrom(NUMBER, Long.class);
    }

    @Test
    void array_type() {
        assertAssignableFrom(ARRAY(STRING), new String[]{});
        assertAssignableFrom(ARRAY(STRING), new String[]{"a"});
        assertAssignableFrom(ARRAY(STRING), new String[]{"a", "b"});
        assertAssignableFrom(ARRAY(STRING), Collections.singleton("a"));
        assertAssignableFrom(ARRAY(STRING), Collections.singletonList("a"));
        assertNotAssignableFrom(ARRAY(STRING), new Object[]{});

    }

    @Test
    void optional() {
        assertAssignableFrom(OPTIONAL(STRING), null);
        assertAssignableFrom(OPTIONAL(STRING), "a");
        assertAssignableFrom(OPTIONAL(STRING), ENUM("a"));
        assertAssignableFrom(OPTIONAL(ENUM("a", "b")), ENUM("a"));
    }

    @Test
    void test_getElementType() {
        assertEquals(STRING, getElementType(ARRAY(STRING)));
        assertEquals(UNKNOWN, getElementType(OPTIONAL(STRING)));
        assertEquals(UNKNOWN, getElementType(STRING));
        assertEquals(OPTIONAL(STRING), getElementType(ARRAY(OPTIONAL(STRING))));
    }

    @Test
    void array_type_with_invalid_item() {
        assertNotAssignableFrom(ARRAY(STRING), new Object[]{1, 2, 3});
        assertNotAssignableFrom(ARRAY(NUMBER), new Object[]{"1", "2", "3"});
    }

    @Test
    void optional_with_invalid_value() {
        assertNotAssignableFrom(OPTIONAL(NUMBER), "not a number");
        assertNotAssignableFrom(OPTIONAL(ENUM("a", "b")), "not a valid enum value");
    }

    @Test
    void getElementType_with_nested_array() {
        assertEquals(STRING, getElementType(ARRAY(STRING)));
        assertEquals(NUMBER, getElementType(ARRAY(NUMBER)));
        assertEquals(ARRAY(STRING), getElementType(ARRAY(ARRAY(STRING))));
    }
}