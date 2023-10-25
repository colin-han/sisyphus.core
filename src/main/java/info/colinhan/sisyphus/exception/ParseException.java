package info.colinhan.sisyphus.exception;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParseException extends Exception {
    private final List<ParseError> errors;

    public ParseException() {
        super();
        errors = Collections.emptyList();
    }

    public ParseException(String message) {
        super(message);
        errors = Collections.singletonList(new ParseError(0, 0, 0, message, null));
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
        errors = Collections.singletonList(new ParseError(0, 0, 0, message, cause));
    }

    public ParseException(List<ParseError> errors) {
        super(errors.stream().map(ParseError::toString).collect(Collectors.joining("\n")));
        this.errors = errors;
    }

    public List<ParseError> getErrors() {
        return errors;
    }

    public static RuntimeException withWrapper(String message) {
        return new RuntimeException(new ParseException(message));
    }
    public static RuntimeException withWrapper(String message, Throwable cause) {
        return new RuntimeException(new ParseException(message, cause));
    }

    public static ParseException unwrap(RuntimeException e) {
        if (e.getCause() instanceof ParseException) {
            return (ParseException) e.getCause();
        } else {
            throw e;
        }
    }
}
