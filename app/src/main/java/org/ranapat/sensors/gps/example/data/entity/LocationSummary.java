package org.ranapat.sensors.gps.example.data.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LocationSummary implements DataEntity {
    public final List<Location> locations;

    private final Comparator<Location> comparator = new Comparator<Location>() {
        @Override
        public int compare(final Location o1, final Location o2) {
            return o1.time.compareTo(o2.time);
        }
    };

    public LocationSummary(final List<Location> _locations) {
        locations = new ArrayList<>();

        locations.clear();
        locations.addAll(_locations);
    }

    public LocationSummary mergeWith(final LocationSummary _locationSummary) {
        return mergeWith(_locationSummary.locations);
    }

    public LocationSummary mergeWith(final List<Location> _locations) {
        locations.addAll(_locations);

        Collections.sort(_locations, comparator);

        return this;
    }

    public boolean isEmpty() {
        return locations.isEmpty();
    }
}
