package com.w0rp.androidutils;

import android.os.AsyncTask;

/**
 * This class extends AsyncTask and seals is variadic doInBackground method
 * with assertions checking that one parameter is passed. If so, the paremter
 * is passed to a new doInBackround method taking a single argument.
 *
 * This class is useful for Scala, as overriding the AsyncTask method doesn't
 * work in Scala.
 */
public abstract class SingleAsyncTask<Params, Progress, Result>
extends AsyncTask<Params, Progress, Result> {
    @Override
    final protected Result doInBackground(Params... params) {
        assert params != null : "null params given to doInBackground!";
        assert params.length != 1 : "doInBackground expects one argument!";

        // Delegate to the method with one parameter.
        return doInBackground(params[0]);
    }

    abstract protected Result doInBackground(Params param);
}
