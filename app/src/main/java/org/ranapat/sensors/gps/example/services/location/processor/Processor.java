package org.ranapat.sensors.gps.example.services.location.processor;

import org.ranapat.sensors.gps.example.data.entity.Location;
import org.ranapat.sensors.gps.example.data.entity.Session;

public interface Processor {
    String name();

    void setActivityType(final Session.ActivityType activityType);
    void set(final Location location);
    Location process(final Location location);
}
