package org.ranapat.sensors.gps.example.services.stepper;

import io.reactivex.subjects.PublishSubject;

public final class StepperStatus {
    final public PublishSubject<Integer> stepsSoFar;

    public StepperStatus() {
        stepsSoFar = PublishSubject.create();
    }
}
