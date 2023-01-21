package org.ranapat.sensors.gps.example.services.location.processor.kalman;

import org.ranapat.instancefactory.Inject;
import org.ranapat.sensors.gps.example.logger.Logger;
import org.ranapat.sensors.gps.example.Settings;
import org.ranapat.sensors.gps.example.data.entity.DefaultSpeedMetersPerSecond;
import org.ranapat.sensors.gps.example.data.entity.Location;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.data.entity.User;
import org.ranapat.sensors.gps.example.observable.DefaultSpeedMetersPerSecondObservable;
import org.ranapat.sensors.gps.example.observable.UserObservable;
import org.ranapat.sensors.gps.example.services.location.processor.Processor;

import java.util.HashMap;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public final class Basic implements Processor {
    private final static float MIN_ACCURACY = 1f;

    @Inject private final UserObservable userObservable = null;
    @Inject private final DefaultSpeedMetersPerSecondObservable defaultSpeedMetersPerSecondObservable = null;

    private final CompositeDisposable compositeDisposable;

    private Session.ActivityType activityType;
    private float metersPerSecond;

    private long time;
    private double latitude;
    private double longitude;
    private float variance;

    public Basic() {
        compositeDisposable = new CompositeDisposable();

        reset();

        this.activityType = Session.ActivityType.Undefined;
        this.metersPerSecond = Settings.defaultSpeedMetersPerSeconds
                .get(Session.ActivityType.Undefined);
    }

    @Override
    public String name() {
        return "kalman.basic";
    }

    @Override
    public void setActivityType(final Session.ActivityType activityType) {
        if (this.activityType != activityType) {
            reset();

            this.activityType = activityType;
            this.metersPerSecond = Settings.defaultSpeedMetersPerSeconds
                    .get(activityType);

            compositeDisposable.add(fetchDefaultSpeedMetersPerSecond(activityType)
                    .subscribe(new Consumer<Float>() {
                        @Override
                        public void accept(final Float defaultSpeedMetersPerSecond) {
                            Logger.e("[ Kalman.Basic ] set speed m/s " + defaultSpeedMetersPerSecond);

                            Basic.this.metersPerSecond = defaultSpeedMetersPerSecond;
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(final Throwable throwable) {
                            Logger.e("[ Kalman.Basic ] unable to set speed m/s, fallback to  " + metersPerSecond);
                        }
                    })
            );
        }
    }

    @Override
    public void set(final Location location) {
        latitude = location.latitude;
        longitude = location.longitude;
        variance = location.accuracy * location.accuracy;
        time = location.time.getTime();
    }

    @Override
    public Location process(final Location location) {
        final float normalizedAccuracy = Math.max(location.accuracy, MIN_ACCURACY);
        if (variance < 0) {
            set(location);

            return location;
        } else {
            final long delta = location.time.getTime() - this.time;
            if (delta > 0) {
                variance += delta * metersPerSecond * metersPerSecond / 1000;
                this.time = location.time.getTime();
            }

            final float K = variance / (variance + location.accuracy * location.accuracy);
            latitude += K * (location.latitude - latitude);
            longitude += K * (location.longitude - longitude);
            variance = (1 - K) * variance;

            return location.duplicate(new HashMap<String, Object>() {{
                put("latitude", latitude);
                put("longitude", longitude);
                put("accuracy", calculateAccuracy());
            }});
        }
    }

    private void reset() {
        compositeDisposable.clear();

        variance = -1;
    }

    private float calculateAccuracy() {
        return (float) Math.sqrt(variance);
    }

    private Maybe<Float> fetchDefaultSpeedMetersPerSecond(final Session.ActivityType activityType) {
        return userObservable
                .fetch()
                .flatMap(new Function<User, MaybeSource<Float>>() {
                    @Override
                    public MaybeSource<Float> apply(final User user) {
                        return defaultSpeedMetersPerSecondObservable
                                .fetch(user.gender, activityType)
                                .flatMap(new Function<DefaultSpeedMetersPerSecond, MaybeSource<Float>>() {
                                    @Override
                                    public MaybeSource<Float> apply(final DefaultSpeedMetersPerSecond defaultSpeedMetersPerSecond) {
                                        return Maybe.just(defaultSpeedMetersPerSecond.value);
                                    }
                                })
                                .onErrorReturn(new Function<Throwable, Float>() {
                                    @Override
                                    public Float apply(final Throwable throwable) {
                                        return Settings.defaultSpeedMetersPerSeconds.get(activityType);
                                    }
                                });
                    }
                });
    }
}