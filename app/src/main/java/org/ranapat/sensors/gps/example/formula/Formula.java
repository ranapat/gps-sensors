package org.ranapat.sensors.gps.example.formula;

public interface Formula<T> {
    void initialize(final Object... parameters);
    void setNext(final Object... parameters);
    T getNext();
}
