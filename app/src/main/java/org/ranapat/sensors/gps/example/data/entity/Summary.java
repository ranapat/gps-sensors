package org.ranapat.sensors.gps.example.data.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Summary implements DataEntity {
    public final String sessionId;
    public final int index;

    public final Locations locations;
    @JsonProperty("pedometer")
    public final StepSummary stepSummary;

    public final long interval;
    public final TimeUnit timeUnits;

    public final float suspicious;

    public final Date start;
    public final Date end;

    public Summary(
            final String sessionId,
            final int index,

            final Locations locations,
            final StepSummary stepSummary,

            final long interval,
            final TimeUnit timeUnits,

            final float suspicious,

            final Date start,
            final Date end
    ) {
        this.sessionId = sessionId;
        this.index = index;

        this.locations = locations;
        this.stepSummary = stepSummary;

        this.interval = interval;
        this.timeUnits = timeUnits;

        this.suspicious = suspicious;

        this.start = start;
        this.end = end;
    }

}
