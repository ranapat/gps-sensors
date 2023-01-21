package org.ranapat.sensors.gps.example.observable.exceptions;

public class UserUndefinedException extends IllegalStateException {
    public UserUndefinedException() {
        super("User Undefined");
    }
}
