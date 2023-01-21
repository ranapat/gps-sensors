package org.ranapat.sensors.gps.example.aggregator;

import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.tools.OnItemListener;

import java.lang.ref.WeakReference;

public final class AggregatorRegistered {
    public final WeakReference<AggregatorReporter> reporter;
    public final WeakReference<OnItemListener<Session.ActivityType>> sessionCreated;
    public final WeakReference<OnItemListener<Session.ActivityType>> sessionClosed;

    public AggregatorRegistered(
            final AggregatorReporter reporter,
            final OnItemListener<Session.ActivityType> sessionCreated,
            final OnItemListener<Session.ActivityType> sessionClosed
    ) {
        this.reporter = new WeakReference<>(reporter);
        this.sessionCreated = new WeakReference<>(sessionCreated);
        this.sessionClosed = new WeakReference<>(sessionClosed);
    }
}
