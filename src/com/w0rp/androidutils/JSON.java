package com.w0rp.androidutils;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.jdt.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.w0rp.androidutils.Iter.CastIterable;
import com.w0rp.androidutils.Iter.IteratorIterable;

/*
 * This class provides various utility methods for constructing and
 * deconstructing JSON sequences.
 */
public abstract class JSON {
    public static class JSONArrayIterator implements Iterator<Object> {
        private final JSONArray arr;
        private int current = 0;

        public JSONArrayIterator(JSONArray arr) {
            this.arr = arr;
        }

        @Override
        public boolean hasNext() {
            return arr != null && current < this.arr.length();
        }

        @Override
        public @Nullable Object next() {
            if (hasNext()) {
                return arr.opt(current++);
            }

            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            // TODO: Implement this?
        }
    }

    /**
     * @param arr A JSONArray. null will be tolerated.
     * @return An Iterator through the values of the JSONArray.
     */
    public static IteratorIterable<Object> iter(@Nullable JSONArray arr) {
        if (arr == null || arr.length() == 0) {
            return Iter.emptyIteratorIterable();
        }

        return Iter.cast(Object.class, new JSONArrayIterator(arr));
    }

    /**
     * @param arr A JSONArray. null will be tolerated.
     * @return An Iterator through the JSONObject values of the JSONArray.
     */
    public static CastIterable<JSONObject> objIter(@Nullable JSONArray arr) {
        return Iter.cast(JSONObject.class, iter(arr));
    }

    /**
     * @param obj An object to pull a JSONArray from.
     * @param key The key for the JSONArray.
     * @return An Iterator through the values of the JSONArray,
     *     null objects will result in empty Iterables.
     */
    public static IteratorIterable<Object> iter(
    @Nullable JSONObject obj, String key) {
        return iter(obj != null ? obj.optJSONArray(key) : null);
    }

    /**
     * @param obj An object to pull a JSONArray from.
     * @param key The key for the JSONArray.
     * @return An Iterator through the JSONObject values of the JSONArray.
     */
    public static CastIterable<JSONObject> objIter(
    @Nullable JSONObject obj, String key) {
        return Iter.cast(JSONObject.class, iter(obj, key));
    }

    /**
     * Produce an iterable through a JSONObject's keys.
     *
     * null will be tolerated in all cases.
     *
     * @return An Iterable iterating through an object's keys.
     */
    public static IteratorIterable<String> keys(@Nullable JSONObject obj) {
        if (obj == null) {
            // We use the stronger type information here to enhance information
            // about null analysis in foreach loops.
            return Iter.emptyIteratorIterable();
        }

        return Iter.cast(String.class, obj.keys());
    }

    /**
     * Call obj.optString with checked null analysis.
     *
     * @param obj The JSON object.
     * @param key The key in the object.
     *
     * @return The string value, or an otherwise an empty string.
     */
    @SuppressWarnings("null")
    public static String optString(JSONObject obj, String key) {
        return obj.optString(key);
    }

    /**
     * Call obj.getString with checked null analysis.
     *
     * @param obj The JSON object.
     * @param key The key in the object.
     *
     * @return The string value.
     * @throw JSONException if the value is missing.
     */
    @SuppressWarnings("null")
    public static String getString(JSONObject obj, String key)
    throws JSONException {
        return obj.getString(key);
    }
}
