package org.ranapat.sensors.gps.example.data.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Locations {
    @JsonProperty("raw")
    public final List<Location> raw;
    @JsonProperty("normalized")
    public final List<Location> processed;

    public Locations(
            final List<Location> raw,
            final List<Location> processed
    ) {
        this.raw = raw;
        this.processed = processed;
    }
}
