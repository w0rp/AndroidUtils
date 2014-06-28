package com.w0rp.androidutils;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.eclipse.jdt.annotation.Nullable;

public abstract class Iter {
    private static class NullIterator<T> implements Iterator<T> {
        @Override public boolean hasNext() { return false; }
        @Override public @Nullable T next() { return null; }
        @Override public void remove() { }
    }

    private static class NullIterable<T> implements Iterable<T> {
        @Override
        public Iterator<T> iterator() {
            return emptyIterator();
        }
    }

    public static class CastIterator<T> implements Iterator<T> {
        private final Class<T> cls;
        private final @Nullable Iterator<?> inIterator;
        private @Nullable T current;

        private CastIterator(Class<T> cls, @Nullable Iterator<?> in) {
            assert(cls != null);

            this.cls = cls;
            inIterator = in;
        }

        @Override
        public boolean hasNext() {
            if (current != null) {
                // Avoid looking for the next value when we're waiting
                // for next to be called.
                return true;
            }

            if (inIterator != null) {
                // Search for the next valid instance.
                while (inIterator.hasNext()) {
                    current = Coerce.cast(cls, inIterator.next());

                    if (current != null) {
                        return true;

                    }
                }
            }

            return false;
        }

        @Override
        public T next() {
            if (hasNext() && current != null) {
                // First dispose of our reference.
                T next = current;
                current = null;
                // Then return it.
                return next;
            }

            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            // Delegate remove to the original iterator.
            if (inIterator != null) {
                inIterator.remove();
            }
        }
    }

    public static class CastIterable<T> implements Iterable<T> {
        private final Class<T> cls;
        private final @Nullable Iterable<?> inIterable;

        private CastIterable(Class<T> cls, @Nullable Iterable<?> in) {
            this.cls = cls;
            inIterable = in;
        }

        @Override
        public CastIterator<T> iterator() {
            if (inIterable != null) {
                return new CastIterator<T>(cls, inIterable.iterator());
            }

            return emptyCastIterator();
        }
    }

    /**
     * Given an Iterator of an type, this class will create an
     * iterable returning an iterator running through only valid instances
     * of type T.
     */
    public static class IteratorIterable<T> implements Iterable<T> {
        private final CastIterator<T> castIterator;

        private IteratorIterable(Class<T> cls, @Nullable Iterator<?> iterator) {
            // Because we iterate always through the same iterator,
            // we can create the wrapper iterator in the constructor.
            if (iterator != null && iterator.hasNext()) {
                castIterator = new CastIterator<T>(cls, iterator);
            } else {
                // If the iterator is null or empty, we just re-use
                // a single instance to save on allocations.
                castIterator = emptyCastIterator();
            }
        }

        @Override
        public CastIterator<T> iterator() {
            return castIterator;
        }
    }

    private static final Iterator<?> NULL_ITERATOR =
        new NullIterator<Object>();

    private static final Iterable<?> NULL_ITERABLE =
        new NullIterable<Object>();

    private static final IteratorIterable<?> NULL_ITERATOR_ITERABLE =
        new IteratorIterable<Object>(Object.class, null);

    private static final CastIterator<?> NULL_CAST_ITERATOR =
        new CastIterator<Object>(Object.class, null);

    /**
     * @return A single instance of an empty iterator.
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> emptyIterator() {
        return (Iterator<T>) NULL_ITERATOR;
    }

    /**
     * @return A single instance of an empty iterable,
     *     which yield the single instance of an empty iterator.
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterable<T> emptyIterable() {
        return (Iterable<T>) NULL_ITERABLE;
    }

    /**
     * @return A single instance of an empty CastIterator.
     */
    @SuppressWarnings("unchecked")
    public static <T> CastIterator<T> emptyCastIterator() {
        return (CastIterator<T>) NULL_CAST_ITERATOR;
    }

    /**
     * @return A single instance of an empty IteratorIterable.
     */
    @SuppressWarnings("unchecked")
    public static <T> IteratorIterable<T> emptyIteratorIterable() {
        return (IteratorIterable<T>) NULL_ITERATOR_ITERABLE;
    }

    /**
     * Given an Iterable, return an Iterable iterating through the values
     * in the original Iterable, only where the objects are valid instances
     * of type T.
     *
     * null will be tolerated in all cases.
     *
     * @param cls The class to cast the obejcts with.
     * @param in The original Iterable
     * @return A new Iterable yielding only valid instances of type T.
     */
    public static <T> CastIterable<T> cast(
    Class<T> cls, @Nullable Iterable<?> in) {
        return new CastIterable<T>(cls, in);
    }

    /**
     * Given an iterator of any type, yield an Iterable iterating through
     * only objects which are valid instances of type T.
     *
     * null will be tolerated in all cases.
     */
    public static <T> IteratorIterable<T> cast(
    Class<T> cls, @Nullable Iterator<?> iterator) {
        return new IteratorIterable<T>(cls, iterator);
    }
}
