package com.w0rp.androidutils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.eclipse.jdt.annotation.Nullable;

import android.util.Base64;

public class Net {
    public static class Response {
        public static final int GENERIC_FAILURE = 600;

        private final InputStream stream;
        private int responseCode = GENERIC_FAILURE;

        public Response(@Nullable InputStream stream) {
            this(stream, GENERIC_FAILURE);
        }

        public Response(@Nullable InputStream stream, int responseCode) {
            if (stream == null) {
                this.stream = IO.emptyInputStream();
            } else {
                this.stream = stream;
            }

            this.responseCode = responseCode;
        }

        /**
         * @return An input stream to download the request with.
         */
        public InputStream getStream() {
            return stream;
        }

        /**
         * @return An HTTP response code.
         */
        public int getResponseCode() {
            return responseCode;
        }

        /**
         * Download the entire request to a string.
         *
         * The InputStream will be automatically closed.
         *
         * @return All of the request data.
         */
        public String download() throws IOException {
            return IO.streamToString(stream);
        }

        /**
         * @return true if the response code is >= 400.
         */
        public boolean failure() {
            return responseCode >= 400;
        }
    }

    @SuppressWarnings("resource")
	public static Response openRequest(HttpUriRequest request) {
        @Nullable InputStream stream = null;
        int responseCode = Response.GENERIC_FAILURE;

        try {
            HttpResponse response = new DefaultHttpClient().execute(request);
            HttpEntity entity = response.getEntity();
            stream = entity.getContent();
            responseCode = response.getStatusLine().getStatusCode();
        } catch (IOException e) { }

        return new Response(stream, responseCode);
    }

    /*
     * @param
     *
     * @return A GET request object.
     */
    public static HttpGet prepareGet(URI uri, Header... headerList) {
        HttpGet request = new HttpGet(uri);

        for (Header header : headerList) {
            request.setHeader(header);
        }

        return request;
    }

    public static HttpGet prepareGet(URI uri) {
        return new HttpGet(uri);
    }

    public static Header authHeader(String username, String password) {
        return new BasicHeader("Authorization", "Basic "
            + base64Auth(username, password));
    }

    @SuppressWarnings("null")
    public static String base64Auth(
    @Nullable String username, @Nullable String password) {
        return Base64.encodeToString(
            (Coerce.def(username) + ":" + Coerce.def(password)).getBytes(),
            Base64.NO_WRAP
        );
    }
}
