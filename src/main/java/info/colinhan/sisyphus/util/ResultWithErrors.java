package info.colinhan.sisyphus.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record ResultWithErrors<TResult, TError>(TResult result, List<TError> errors) {
    public static <R, E> ResultWithErrors<R, E> of(R result) {
        return new ResultWithErrors<>(result, Collections.emptyList());
    }

    public static <R, E> ResultWithErrors<R, E> withError(R result, List<E> errors) {
        return new ResultWithErrors<>(result, errors);
    }

    @SafeVarargs
    public static <R, E> ResultWithErrors<R, E> withError(R result, E... errors) {
        return new ResultWithErrors<>(result, Arrays.asList(errors));
    }
}
