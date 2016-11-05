package com.github.clboettcher.bonappetit.app.data;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Loadable<T> {

    private enum LoadingState {
        INITIAL,
        LOADING,
        LOADED,
        FAILED
    }

    private LoadingState loadingState;
    private T value;
    private ErrorCode errorCode;

    private Loadable(LoadingState loadingState) {
        this.loadingState = loadingState;
    }

    public Loadable(LoadingState loadingState, ErrorCode errorCode) {
        this.loadingState = loadingState;
        this.errorCode = errorCode;
    }

    private Loadable(LoadingState loadingState, T value) {
        this.loadingState = loadingState;
        this.value = value;
    }

    public static <T> Loadable<T> initial() {
        return new Loadable<>(LoadingState.INITIAL);
    }

    public static <T> Loadable<T> loading() {
        return new Loadable<>(LoadingState.LOADING);
    }

    public static <T> Loadable<T> loaded(T value) {
        return new Loadable<>(LoadingState.LOADED, value);
    }

    public static <T> Loadable<T> failed(ErrorCode errorCode) {
        return new Loadable<>(LoadingState.FAILED, errorCode);
    }

    public boolean isLoaded() {
        return loadingState == LoadingState.LOADED;
    }

    public boolean isLoading() {
        return loadingState == LoadingState.LOADING;
    }

    public boolean isFailed() {
        return loadingState == LoadingState.FAILED;
    }

    public T getValue() {
        return value;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("loadingState", loadingState)
                .append("value", value)
                .toString();
    }
}