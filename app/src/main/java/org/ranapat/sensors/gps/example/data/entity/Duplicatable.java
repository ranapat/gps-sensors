package org.ranapat.sensors.gps.example.data.entity;

import java.util.Map;

public interface Duplicatable<T> {
    T duplicate(final Map<String, Object> parameters);
}
