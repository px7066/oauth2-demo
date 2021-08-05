package com.github.px.custom.matcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

public interface RequestMatcher {
    boolean matches(HttpServletRequest var1);

    default RequestMatcher.MatchResult matcher(HttpServletRequest request) {
        boolean match = this.matches(request);
        return new RequestMatcher.MatchResult(match, Collections.emptyMap());
    }

    class MatchResult {
        private final boolean match;
        private final Map<String, String> variables;

        MatchResult(boolean match, Map<String, String> variables) {
            this.match = match;
            this.variables = variables;
        }

        public boolean isMatch() {
            return this.match;
        }

        public Map<String, String> getVariables() {
            return this.variables;
        }

        public static RequestMatcher.MatchResult match() {
            return new RequestMatcher.MatchResult(true, Collections.emptyMap());
        }

        public static RequestMatcher.MatchResult match(Map<String, String> variables) {
            return new RequestMatcher.MatchResult(true, variables);
        }

        public static RequestMatcher.MatchResult notMatch() {
            return new RequestMatcher.MatchResult(false, Collections.emptyMap());
        }
    }
}
