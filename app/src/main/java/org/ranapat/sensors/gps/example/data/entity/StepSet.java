package org.ranapat.sensors.gps.example.data.entity;

import java.util.Date;

public class StepSet implements DataEntity {
    public final int steps;
    public final Date start;
    public final Date end;
    public final String method;
    public final double accuracy;

    public StepSet(
            final int steps,
            final Date start,
            final Date end,
            final String method,
            final double accuracy
    ) {
        this.steps = steps;
        this.start = start;
        this.end = end;
        this.method = method;
        this.accuracy = accuracy;
    }
}
