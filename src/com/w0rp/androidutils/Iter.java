package com.w0rp.androidutils;

import java.util.Iterator;

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
            return new NullIterator<T>();
        }
    }

    public static final Iterator<?> NULL_ITERATOR =
        new NullIterator<Object>();

    public static final Iterable<?> NULL_ITERABLE =
        new NullIterable<Object>();

    private static class CastIterator<T> implements Iterator<T> {
        private final Class<T> cls;
        private final @Nullable Iterator<?> inIterator;
        private @Nullable T current;

        public CastIterator(Class<T> cls, @Nullable Iterator<?> in) {
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
        public @Nullable T next() {
            if (hasNext() && current != null) {
                // First dispose of our reference.
                T next = current;
                current = null;
                // Then return it.
                return next;
            }

            return null;
        }

        @Override
        public void remove() {
            // Delegate remove to the original iterator.
            if (inIterator != null) {
                inIterator.remove();
            }
        }
    }

    private static class CastIterable<T> implements Iterable<T> {
        private final Class<T> cls;
        private final @Nullable Iterable<?> inIterable;

        public CastIterable(Class<T> cls, @Nullable Iterable<?> in) {
            this.cls = cls;
            inIterable = in;
        }

        @Override
        public Iterator<T> iterator() {
            if (inIterable != null) {
                return new CastIterator<T>(cls, inIterable.iterator());
            }

            @SuppressWarnings("unchecked")
            Iterator<T> nullIterator = (Iterator<T>) Iter.NULL_ITERATOR;

            return nullIterator;
        }
    }

    /**
     * Given an Iterator of an type, this class will create an
     * iterable returning an iterator running through only valid instances
     * of type T.
     */
    private static class IteratorIterable<T> implements Iterable<T> {
        private final Class<T> cls;
        private final @Nullable Iterator<?> iterator;

        public IteratorIterable(Class<T> cls, @Nullable Iterator<?> iterator) {
            this.cls = cls;
            this.iterator = iterator;
        }

        @Override
        public Iterator<T> iterator() {
            return new CastIterator<T>(cls, iterator);
        }
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
    public static <T> Iterable<T> cast(Class<T> cls, @Nullable Iterable<?> in) {
        return new CastIterable<T>(cls, in);
    }

    /**
     * Given an iterator of any type, yield an Iterable iterating through
     * only objects which are valid instances of type T.
     *
     * null will be tolerated in all cases.
     */
    public static <T> Iterable<T> cast(
    Class<T> cls, @Nullable Iterator<?> iterator) {
        return new IteratorIterable<T>(cls, iterator);
    }
}
