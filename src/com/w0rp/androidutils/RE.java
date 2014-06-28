package com.w0rp.androidutils;

import java.util.AbstractList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.Nullable;

public final class RE {
    /**
     * A List wrapper for a MatchList.
     */
    public static class MatchList extends AbstractList<String> {
        private final @Nullable MatchResult matchResult;

        private MatchList() {
            this.matchResult = null;
        }

        /**
         * Create a MatchList given a match result.
         *
         * If the result is null, the match list will be empty.
         *
         * @param matchResult The match result.
         */
        public MatchList(@Nullable MatchResult matchResult) {
            this.matchResult = matchResult;
        }

        /**
         * @return The MatchResult object backing this List, which may be null.
         */
        public @Nullable MatchResult getMatchResult() {
            return matchResult;
        }

        /**
         * Get an element of the match result, index 0 returning the whole match
         * and further indices returning a match group.
         *
         * @return The group at the given location.
         */
        @SuppressWarnings("null")
        @Override
        public String get(int location) {
            if (matchResult != null) {
                return matchResult.group(location);
            }

            throw new IllegalStateException("The MatchList was empty!");
        }

        /**
         * @return The number of groups in the match, plus the whole match.
         */
        @Override
        public int size() {
            if (matchResult != null) {
                return matchResult.groupCount() + 1;
            }

            return 0;
        }
    }

    /**
     * A shared immutable instance of an empty match list.
     */
    public static final MatchList emptyMatch = new MatchList();

    /**
     * Search for a single regular expression match in some input.
     *
     * @param pattern A regular expression pattern
     * @param input Some character input.
     * @return The match results as a MatchList, empty if no match is made.
     */
    public static MatchList search(Pattern pattern, CharSequence input) {
        Matcher matcher = pattern.matcher(input);

        if (!matcher.find()) {
            return emptyMatch;
        }

        return new MatchList(matcher.toMatchResult());
    }
}
