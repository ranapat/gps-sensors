package org.ranapat.sensors.gps.example.formula.calories;

import org.ranapat.sensors.gps.example.data.entity.User;
import org.ranapat.sensors.gps.example.formula.Formula;

public class BasicV1 implements Formula<Float> {
    private User.Gender gender;
    private int age;
    private float weight;
    private int heartRate;

    private float time;

    @Override
    public void initialize(final Object... parameters) {
        gender = parameters.length > 0 && parameters[0] != null ? (User.Gender) parameters[0] : User.Gender.Other;
        age = parameters.length > 1 && parameters[1] != null ? (int) parameters[1] : 0;
        weight = parameters.length > 2 && parameters[2] != null ? (float) parameters[2] : 0.0f;
        heartRate = parameters.length > 3 && parameters[3] != null ? (int) parameters[3] : 0;
    }

    @Override
    public void setNext(final Object... parameters) {
        if (parameters.length > 0 && parameters[0] != null) {
            time = (Float) parameters[0];
        }
    }

    @Override
    public Float getNext() {
        if (gender == User.Gender.Female) {
            return ((age * 0.074f) - (weight * 2.205f * 0.05741f) + (heartRate * 0.4472f) - 20.4022f) * time / 4.184f;
        } else {
            return ((age * 0.2017f) + (weight * 2.205f * 0.09036f) + (heartRate * 0.6309f) - 55.0969f) * time / 4.184f;
        }
    }
}
