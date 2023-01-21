package org.ranapat.sensors.gps.example.aggregator.handler;

import org.ranapat.instancefactory.Inject;
import org.ranapat.sensors.gps.example.aggregator.AggregatorRegistered;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.logger.Logger;
import org.ranapat.sensors.gps.example.services.location.LocationService;
import org.ranapat.sensors.gps.example.services.location.LocationServiceStarter;
import org.ranapat.sensors.gps.example.services.stepper.StepperService;
import org.ranapat.sensors.gps.example.services.stepper.StepperServiceStarter;
import org.ranapat.sensors.gps.example.tools.OnItemListener;

import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;

public final class AggregatorServiceHandler {
    @Inject final private StepperServiceStarter stepperServiceStarter = null;
    @Inject final private LocationServiceStarter locationServiceStarter = null;

    private final CompositeDisposable compositeDisposable;

    public AggregatorServiceHandler() {
        compositeDisposable = new CompositeDisposable();
    }

    public void clear() {
        compositeDisposable.clear();
    }

    public void handleCreateSession(final Session session, Map<Class, AggregatorRegistered> registered) {
        for (Map.Entry<Class, AggregatorRegistered> entry : registered.entrySet()) {
            final OnItemListener<Session.ActivityType> onItemListener = entry.getValue().sessionCreated.get();
            if (onItemListener != null) {
                onItemListener.onItem(session.activityType);
            }
        }
    }

    public void handleCloseSession(final Session session, Map<Class, AggregatorRegistered> registered) {
        for (Map.Entry<Class, AggregatorRegistered> entry : registered.entrySet()) {
            final OnItemListener<Session.ActivityType> onItemListener = entry.getValue().sessionClosed.get();
            if (onItemListener != null) {
                onItemListener.onItem(session.activityType);
            }
        }
    }

    public void handleServiceUnregister(final Object object) {
        final Class _class = object.getClass();

        Logger.e("[ AggregatorServiceHandler ] service unregister " + _class);

        if (_class == StepperService.class) {
            Logger.e("[ AggregatorServiceHandler ] try to start again " + StepperService.class);

            stepperServiceStarter.start();
        } else if (_class == LocationService.class) {
            Logger.e("[ AggregatorServiceHandler ] try to start again " + LocationService.class);

            locationServiceStarter.start();
        }
    }

}
