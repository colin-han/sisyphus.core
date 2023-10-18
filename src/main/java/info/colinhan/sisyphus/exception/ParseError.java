package info.colinhan.sisyphus.exception;

public record ParseError(int line, int column, int length, String message, Throwable cause) {

    @Override
    public Throwable cause() {
        return cause;
    }

    @Override
    public String toString() {
        return String.format("[%d,%d] %s", line, column, message);
    }
}
