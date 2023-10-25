package info.colinhan.sisyphus.util;

import java.util.Arrays;
import java.util.List;

public record ResultOrErrors<TResult, TError>(TResult result, List<TError> errors) {
    public static <R, E> ResultOrErrors<R, E> of(R result) {
        return new ResultOrErrors<>(result, null);
    }

    public static <R, E> ResultOrErrors<R, E> error(List<E> errors) {
        return new ResultOrErrors<>(null, errors);
    }

    @SafeVarargs
    public static <R, E> ResultOrErrors<R, E> error(E... errors) {
        return new ResultOrErrors<>(null, Arrays.asList(errors));
    }

    public boolean isSuccess() {
        return errors == null;
    }
}
