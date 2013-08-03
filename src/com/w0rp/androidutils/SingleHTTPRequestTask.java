package com.w0rp.androidutils;

import java.io.IOException;

import org.apache.http.client.methods.HttpUriRequest;

/**
 * This class provides a convenient way of dealing with a single HTTP request
 * with an AsyncTask. The response value for this task will be a string
 * containing the request data. When a request fails, this string will be null.
 */
public abstract class SingleHTTPRequestTask
extends SingleAsyncTask<HttpUriRequest, Void, String> {
    @Override
    protected String doInBackground(HttpUriRequest request) {
        Net.Response response = Net.openRequest(request);

        if (!response.failure()) {
            try {
                return response.download();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}