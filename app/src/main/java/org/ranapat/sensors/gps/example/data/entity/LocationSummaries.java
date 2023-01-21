package org.ranapat.sensors.gps.example.data.entity;

public class LocationSummaries implements DataEntity {
    public final LocationSummary raw;
    public final LocationSummary processed;

    public LocationSummaries(final LocationSummary raw, final LocationSummary processed) {
        this.raw = raw;
        this.processed = processed;
    }
}
