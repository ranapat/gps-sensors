package org.ranapat.sensors.gps.example;

import org.ranapat.sensors.gps.example.data.entity.Session;

import java.util.HashMap;
import java.util.Map;

final public class Settings {
    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static final String languageKey = BuildConfig.language_key;
    public static final String scriptKey = BuildConfig.script_key;
    public static final String defaultUserLanguage = BuildConfig.default_user_language;
    public static final String defaultUserScript = BuildConfig.default_user_script;

    public static final long debounceNavigationInMilliseconds = BuildConfig.debounce_navigation_in_milliseconds;

    public static float locationAccuracyBest = BuildConfig.location_accuracy_best;
    public static float locationAccuracyWorst = BuildConfig.location_accuracy_worst;

    public static double magnitudeDeltaThreshold = BuildConfig.default_magnitude_delta_threshold;
    public static long defaultLocationNetworkMinTimeMs = BuildConfig.default_location_network_min_time_ms;
    public static long defaultLocationGPSMinTimeMs = BuildConfig.default_location_gps_min_time_ms;
    public static float defaultLocationMinDistance = BuildConfig.default_location_min_distance;

    public static float defaultSpeedMetersPerSecond = BuildConfig.default_speed_meters_per_second;
    public static float defaultSpeedMetersPerSecondForStanding = BuildConfig.default_speed_meters_per_second_for_standing;
    public static float defaultSpeedMetersPerSecondForWalking = BuildConfig.default_speed_meters_per_second_for_walking;
    public static float defaultSpeedMetersPerSecondForJogging = BuildConfig.default_speed_meters_per_second_for_jogging;
    public static float defaultSpeedMetersPerSecondForSprinting = BuildConfig.default_speed_meters_per_second_for_sprinting;
    public static Map<Session.ActivityType, Float> defaultSpeedMetersPerSeconds = new HashMap<Session.ActivityType, Float>() {{
        put(Session.ActivityType.Undefined, defaultSpeedMetersPerSecond);
        put(Session.ActivityType.Standing, defaultSpeedMetersPerSecondForStanding);
        put(Session.ActivityType.Walking, defaultSpeedMetersPerSecondForWalking);
        put(Session.ActivityType.Jogging, defaultSpeedMetersPerSecondForJogging);
        put(Session.ActivityType.Sprinting, defaultSpeedMetersPerSecondForSprinting);
    }};

    public static int defaultHeartrate = BuildConfig.default_heartrate;
    public static int defaultHeartrateForStanding = BuildConfig.default_heartrate_for_standing;
    public static int defaultHeartrateForWalking = BuildConfig.default_heartrate_for_walking;
    public static int defaultHeartrateForJogging = BuildConfig.default_heartrate_for_jogging;
    public static int defaultHeartrateForSprinting = BuildConfig.default_heartrate_for_sprinting;
    public static Map<Session.ActivityType, Integer> defaultHeartrates = new HashMap<Session.ActivityType, Integer>() {{
        put(Session.ActivityType.Undefined, defaultHeartrate);
        put(Session.ActivityType.Standing, defaultHeartrateForStanding);
        put(Session.ActivityType.Walking, defaultHeartrateForWalking);
        put(Session.ActivityType.Jogging, defaultHeartrateForJogging);
        put(Session.ActivityType.Sprinting, defaultHeartrateForSprinting);
    }};

    public static long aggregateDataSecondsInterval = BuildConfig.aggregate_data_seconds_interval;
    public static long aggregateSyncSecondsInterval = BuildConfig.aggregate_sync_seconds_interval;
}
