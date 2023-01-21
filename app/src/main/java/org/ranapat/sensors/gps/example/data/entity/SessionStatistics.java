package org.ranapat.sensors.gps.example.data.entity;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SessionStatistics implements DataEntity {
    public static class Duration {
        public final long interval;
        public final TimeUnit timeUnit;

        public Duration(final long interval, final TimeUnit timeUnit) {
            this.interval = interval;
            this.timeUnit = timeUnit;
        }
    }

    public final String sessionId;
    public final Date createdAt;
    public final Date closedAt;
    public final Session.ActivityType activityType;
    public final Duration duration;
    public final float calories;
    public final int steps;
    public final float stepsPerSecond;
    public final double distance;
    public final float minSpeed;
    public final float maxSpeed;
    public final float averageSpeed;
    public final float suspicious;

    public SessionStatistics(
            final String sessionId,
            final Date createdAt,
            final Date closedAt,
            final Session.ActivityType activityType,
            final Duration duration,
            final float calories,
            final int steps,
            final float stepsPerSecond,
            final double distance,
            final float minSpeed,
            final float maxSpeed,
            final float averageSpeed,
            final float suspicious
    ) {
        this.sessionId = sessionId;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.activityType = activityType;
        this.duration = duration;
        this.calories = calories;
        this.steps = steps;
        this.stepsPerSecond = stepsPerSecond;
        this.distance = distance;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.averageSpeed = averageSpeed;
        this.suspicious = suspicious;
    }
}
