package org.ranapat.sensors.gps.example.services.location;

import org.ranapat.sensors.gps.example.data.entity.Location;

import io.reactivex.subjects.PublishSubject;

public final class LocationStatus {
    final public PublishSubject<Location> currentLocationRaw;
    final public PublishSubject<Location> currentLocationProcessed;

    public LocationStatus() {
        currentLocationRaw = PublishSubject.create();
        currentLocationProcessed = PublishSubject.create();
    }
}
