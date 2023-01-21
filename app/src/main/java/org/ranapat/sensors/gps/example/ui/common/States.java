package org.ranapat.sensors.gps.example.ui.common;

import static java.util.Arrays.asList;

import java.util.List;

public final class States {
    public static final String LOADING = "loading";
    public static final String ERROR = "error";
    public static final String READY = "ready";
    public static final String REFRESH = "refresh";

    public static final String BLOCKED = "blocked";
    public static final String UNBLOCKED = "unblocked";

    public static final String COMPLETE = "complete";
    public static final String FINISH = "finish";

    public static final String NAVIGATE = "navigate";
    public static final String REDIRECT = "redirect";
    public static final String CLEAN_REDIRECT = "cleanRedirect";

    public static final List<String> toThrottle = asList(COMPLETE, NAVIGATE, REDIRECT, CLEAN_REDIRECT);

    private States() {}

    public static boolean shallThrottle(final String state) {
        return toThrottle.contains(state);
    }
}
