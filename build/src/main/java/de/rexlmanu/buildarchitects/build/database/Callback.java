package de.rexlmanu.buildarchitects.build.database;

public interface Callback<T> {

    void onSuccess(final T result);

    void onFailure(final Throwable cause);
}
