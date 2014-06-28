package com.w0rp.androidutils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

/**
 * This namespace defines utility functions for coercion of types and values.
 *
 * cast() is a helper method for instanceof and cast.
 * def() coerces a default value for a nullable type.
 * len() coerces a length for a nullable type. (0 for null)
 * empty(x) returns len(x) == 0;
 */
public abstract class Coerce {
    /**
     * Attempt to cast a reference to an given class.
     *
     * @param cls A class to cast the reference to.
     * @param ref A reference to cast.
     * @return The reference casted to an instance of T, or null.
     */
    public static @Nullable <T> T cast(Class<T> cls, @Nullable Object ref) {
        if (cls.isInstance(ref)) {
            return cls.cast(ref);
        }

        return null;
    }

    /**
     * Coerce null collections to empty collections.
     *
     * @param in A collection which may be null.
     * @return The collection, or an empty collection if it was null.
     */
    @SuppressWarnings("null")
    public static <T> Collection<T> def(@Nullable Collection<T> in) {
        if (in == null) {
            return Collections.emptyList();
        }

        return in;
    }

    /**
     * Coerce null lists to empty lists.
     *
     * @param in A list which may be null.
     * @return The list, or an empty list if it was null.
     */
    @SuppressWarnings("null")
    public static <T> List<T> def(@Nullable List<T> in) {
        if (in == null) {
            return Collections.emptyList();
        }

        return in;
    }

    /**
     * Coerce null sets to empty sets.
     *
     * @param in A set which may be null.
     * @return The set, or an empty set if it was null.
     */
    @SuppressWarnings("null")
    public static <T> Set<T> def(@Nullable Set<T> in) {
        if (in == null) {
            return Collections.emptySet();
        }

        return in;
    }

    /**
     * Coerce null maps to empty maps.
     *
     * @param in A map which may be null.
     * @return The map, or an empty map if it was null.
     */
    @SuppressWarnings("null")
    public static <K, V> Map<K, V> def(@Nullable Map<K, V> in) {
        if (in == null) {
            return Collections.emptyMap();
        }

        return in;
    }

    /**
     * Coerce null strings to empty strings.
     *
     * @param in A string which may be null.
     * @return The string, or an empty string if it was null.
     */
    public static String def(@Nullable String in) {
        return in == null ? "" : in;
    }

    /**
     * Coerce null characters to 0.
     *
     * @param in A character which may be null.
     * @return The character, or 0 if it was null.
     */
    public static char def (@Nullable Character in) {
        return in == null ? '\0' : in;
    }

    /**
     * Coerce null doubles to 0.
     *
     * @param in A double which may be null.
     * @return The double, or 0 if it was null.
     */
    public static double def(@Nullable Double in) {
        return in == null ? 0d : in;
    }

    /**
     * Coerce null floats to 0.
     *
     * @param in A float which may be null.
     * @return The float, or 0 if it was null.
     */
    public static float def(@Nullable Float in) {
        return in == null ? 0f : in;
    }

    /**
     * Coerce null longs to 0.
     *
     * @param in A long which may be null.
     * @return The long, or 0 if it was null.
     */
    public static long def(@Nullable Long in) {
        return in == null ? 0L : in;
    }

    /**
     * Coerce null integers to 0.
     *
     * @param in An integer which may be null.
     * @return The integer, or 0 if it was null.
     */
    public static int def(@Nullable Integer in) {
        return in == null ? 0 : in;
    }

    /**
     * Coerce null shorts to 0.
     *
     * @param in A short which may be null.
     * @return The short, or 0 if it was null.
     */
    public static short def (@Nullable Short in) {
        return in == null ? 0 : in;
    }

    /**
     * Coerce null bytes to 0.
     *
     * @param in A byte which may be null.
     * @return The byte, or 0 if it was null.
     */
    public static byte def (@Nullable Byte in) {
        return in == null ? 0 : in;
    }

    /**
     * Coerce null booleans to false.
     *
     * @param in A byte which may be null.
     * @return The byte, or 0 if it was null.
     */
    public static boolean def(@Nullable Boolean in) {
        return in == null ? false : in;
    }

    /**
     * Compute length for nullable strings.
     *
     * @param in A string.
     * @return The length of the string, or 0 if it was null.
     */
    public static int len(@Nullable String in) {
        return in == null ? 0 : in.length();
    }

    /**
     * Compute length for nullable collections.
     *
     * @param in A collection.
     * @return The length of the collection, or 0 if it was null.
     */
    public static <T> int len(@Nullable Collection<T> in) {
        return in == null ? 0 : in.size();
    }

    /**
     * Compute length for nullable maps.
     *
     * @param in A map.
     * @return The length of the map, or 0 if it was null.
     */
    public static <K, V> int len(@Nullable Map<K, V> in) {
        return in == null ? 0 : in.size();
    }

    /**
     * @param in A string.
     * @return true if the string is null or empty.
     */
    public static boolean empty(@Nullable String in) {
        return len(in) == 0;
    }

    /**
     * @param in A collection.
     * @return true if the collection is null or empty.
     */
    public static <T> boolean empty(@Nullable Collection<T> in) {
        return len(in) == 0;
    }

    /**
     * @param in A map.
     * @return true if the map is null or empty.
     */
    public static <K, V> boolean empty(Map<K, V> in) {
        return len(in) == 0;
    }
}
