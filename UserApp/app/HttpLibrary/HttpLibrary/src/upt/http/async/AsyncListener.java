package upt.http.async;

/*
 * Copyright (c) 2013. Upresh Tikam's. All rights reserved.
 */

/**
 * This interface provides a generic callback method signature that is used throughout the SDK
 * to return arguments on success or exception when they occur during asynchronous routines
 *
 * @param <T> Typed value for response object in onResponse callback
 */
public interface AsyncListener<T> {
    public void onResponse(final T response, final AsyncToken token, AsyncException exception);
}
