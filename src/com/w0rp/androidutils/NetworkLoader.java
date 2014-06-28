package com.w0rp.androidutils;

import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.eclipse.jdt.annotation.Nullable;

import android.os.AsyncTask;
import android.os.Looper;

/**
 * This class encapsulates management of a background network task for an
 * Android application. Network data is loaded via an HTTP request, and
 * parsed with some implementation of a parser. The requests save the value
 * of the Last-Modified header, and subclasses must implement their own
 * result caching.
 *
 * Concrete subclasses must implement the following:
 *
 * parsData(result) This method must return the result of parsing the data.
 * getURI() This method defines the URI to request.
 * onReceiveResult(result) Run on the UI thread when a result is retrieved.
 * onReceiveFaiure(failure) Run on the UI thread when something goes wrong.
 * useLastResult() Run on the UI thread when a 304: Not Modified is received.
 *
 * @param <Result> The result type returned by the background task.
 */
public abstract class NetworkLoader<Result> {
    private final class Task extends AsyncTask<Void, Void, Result> {
        private @Nullable NetworkFailure failure = null;
        private boolean modified = true;

        private HttpUriRequest prepareRequest() {
            if (lastModifiedString == null) {
                // Don't set the If-Modified-Since header when we can't.
                return Net.prepareGet(getURI());
            }

            // The If-Modified-Since header is sent with the request.
            return Net.prepareGet(getURI(),
                new BasicHeader("If-Modified-Since", lastModifiedString));
        }

        @Override
        protected @Nullable Result doInBackground(Void... params) {
            Result result = null;
            Header lastModified = null;
            int responseCode = 600;

            try {
                HttpUriRequest request = prepareRequest();
                HttpResponse response = new DefaultHttpClient().execute(
                    request);

                responseCode = response.getStatusLine().getStatusCode();
                lastModified = response.getFirstHeader("Last-Modified");

                if (responseCode == 304) {
                    // The post list hasn't been modified, so stop here.
                    modified = false;
                    return null;
                }

                String data = IO.streamToString(
                    response.getEntity().getContent());

                result = parseData(data);
            } catch (Exception exception) {
                failure = new NetworkFailure(exception, responseCode);
                return null;
            }

            if (lastModified != null) {
                // Set the last modified string, as set by the server exactly.
                lastModifiedString = lastModified.getValue();
            }

            return result;
        }

        @Override
        protected void onPostExecute(@Nullable Result result) {
            super.onPostExecute(result);

            if (failure != null) {
                onReceiveFailure(failure);
            } else {
                if (modified) {
                    onReceiveResult(result);
                } else {
                    useLastResult();
                }
            }

            // Clear the reference for the task to stop leaks.
            // onPostExecute happens in the UI thread, so as long as
            // execute also happens in the UI thread, there are no
            // synchronisation issues.
            currentTask = null;
        }
    }

    private @Nullable Task currentTask = null;
    private @Nullable String lastModifiedString;

    /**
     * Cancel the current network task. The task is run on another thread,
     * and the task may complete before being cancelled.
     *
     * This method MUST be run from the UI thread.
     */
    public final void cancel() {
    	assert Looper.getMainLooper().equals(Looper.myLooper())
            : "NetworkLoader operations must be run on the UI thread!";

        if (currentTask != null) {
            currentTask.cancel(true);
            currentTask = null;
        }
    }

    /**
     * Execute the network task, cancelling the current one if one is in
     * progress.
     *
     * This method MUST be run from the UI thread.
     */
    public final void execute() {
        cancel();

        currentTask = new Task();
        currentTask.execute();
    }

    /**
     * This method is called with the data from the HTTP response.
     *
     * This method will be called in some background thread,
     * so care should be taken with thread safety.
     *
     * This method should not be called when an error response is returned.
     *
     * @param data The HTTP response data.
     * @return The parsed result data.
     * @throws Exception Thrown when something goes wrong while parsing.
     */
    protected abstract Result parseData(String data) throws Exception;


    /**
     * This method is called to retrieve the URI to be used for network
     * requests.
     *
     * This method will be called in some background thread,
     * so care should be taken with thread safety.
     *
     * @return The URI to request.
     */
    protected abstract URI getURI();

    /**
     * This method will be called on the UI thread when the request completes,
     * with the result of parsing from parseData(data).
     *
     * @param result The parsed result.
     */
    protected abstract void onReceiveResult(@Nullable Result result);

    /**
     * This method will be called on the UI thread when the request fails.
     *
     * @param failure The reason the request failed.
     */
    protected abstract void onReceiveFailure(NetworkFailure failure);

    /**
     * This method will be called on the UI thread when the currently cached
     * request data should be used.
     */
    protected abstract void useLastResult();
}
