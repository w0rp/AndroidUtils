package com.w0rp.androidutils;


/**
 * This object holds an exception with an HTTP response code.
 */
public final class NetworkFailure {
    private final Exception exception;
    private final int responseCode;

    public NetworkFailure(Exception exception, int responseCode) {
        this.exception = exception;
        this.responseCode = responseCode;
    }

    /**
     * @return The exception that originally caused the problem.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * @return The HTTP response code for the problem, which may be invalid.
     */
    public int getResponseCode() {
        return responseCode;
    }
}
