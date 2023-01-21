package org.ranapat.sensors.gps.example.observable.exceptions;

public class DefaultHeartRateUndefinedException extends IllegalStateException {
    public DefaultHeartRateUndefinedException() {
        super("Default Heart Rate Undefined");
    }
}
