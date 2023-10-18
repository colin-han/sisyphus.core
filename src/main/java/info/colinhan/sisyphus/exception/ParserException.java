package info.colinhan.sisyphus.exception;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParserException extends Exception {
    private final List<ParseError> errors;

    public ParserException() {
        super();
        errors = Collections.emptyList();
    }

    public ParserException(String message) {
        super(message);
        errors = Collections.singletonList(new ParseError(0, 0, 0, message, null));
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
        errors = Collections.singletonList(new ParseError(0, 0, 0, message, cause));
    }

    public ParserException(List<ParseError> errors) {
        super(errors.stream().map(ParseError::toString).collect(Collectors.joining("\n")));
        this.errors = errors;
    }

    public List<ParseError> getErrors() {
        return errors;
    }

    public static RuntimeException withWrapper(String message) {
        return new RuntimeException(new ParserException(message));
    }
    public static RuntimeException withWrapper(String message, Throwable cause) {
        return new RuntimeException(new ParserException(message, cause));
    }

    public static ParserException unwrap(RuntimeException e) {
        if (e.getCause() instanceof ParserException) {
            return (ParserException) e.getCause();
        } else {
            throw e;
        }
    }
}
