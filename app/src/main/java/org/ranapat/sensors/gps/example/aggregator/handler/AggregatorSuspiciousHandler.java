package org.ranapat.sensors.gps.example.aggregator.handler;

import android.util.Range;

import org.ranapat.instancefactory.Inject;
import org.ranapat.sensors.gps.example.logger.Logger;
import org.ranapat.sensors.gps.example.Settings;
import org.ranapat.sensors.gps.example.data.entity.DefaultSpeedMetersPerSecond;
import org.ranapat.sensors.gps.example.data.entity.Location;
import org.ranapat.sensors.gps.example.data.entity.Package;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.data.entity.User;
import org.ranapat.sensors.gps.example.data.tools.Locations;
import org.ranapat.sensors.gps.example.observable.DefaultSpeedMetersPerSecondObservable;
import org.ranapat.sensors.gps.example.observable.UserObservable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public final class AggregatorSuspiciousHandler {
    private final static Map<Range<Float>, Float> rangeMap = new HashMap<Range<Float>, Float>() {{
        put(new Range<Float>(Float.MIN_VALUE, 1.5f), 0.00f);
        put(new Range<Float>(1.5f, 1.8f), 0.10f);
        put(new Range<Float>(1.8f, 2.0f), 0.20f);
        put(new Range<Float>(2.0f, 2.2f), 0.30f);
        put(new Range<Float>(2.2f, 2.6f), 0.35f);
        put(new Range<Float>(2.6f, 2.8f), 0.40f);
        put(new Range<Float>(2.8f, 3.5f), 0.45f);
        put(new Range<Float>(3.5f, 4.0f), 0.50f);
        put(new Range<Float>(4.0f, 4.5f), 0.55f);
        put(new Range<Float>(4.5f, 5.0f), 0.60f);
        put(new Range<Float>(5.0f, 6.5f), 0.65f);
        put(new Range<Float>(6.5f, 7.0f), 0.75f);
        put(new Range<Float>(7.0f, 9.5f), 0.80f);
        put(new Range<Float>(9.5f, 12.5f), 0.90f);
        put(new Range<Float>(12.5f, Float.MAX_VALUE), 1.0f);
    }};

    @Inject private final UserObservable userObservable = null;
    @Inject private final DefaultSpeedMetersPerSecondObservable defaultSpeedMetersPerSecondObservable = null;

    private final CompositeDisposable compositeDisposable;

    private float metersPerSecond;
    private Location previous;

    public AggregatorSuspiciousHandler() {
        compositeDisposable = new CompositeDisposable();

        metersPerSecond = Settings.defaultSpeedMetersPerSeconds
                .get(Session.ActivityType.Undefined);
    }

    public void clear() {
        compositeDisposable.clear();
    }

    public void handleCreateSession(final Session session) {
        this.metersPerSecond = Settings.defaultSpeedMetersPerSeconds
                .get(session.activityType);

        compositeDisposable.add(fetchDefaultSpeedMetersPerSecond(session.activityType)
                .subscribe(new Consumer<Float>() {
                    @Override
                    public void accept(final Float defaultSpeedMetersPerSecond) {
                        Logger.e("[ AggregatorSuspiciousHandler ] set speed m/s " + defaultSpeedMetersPerSecond);

                        AggregatorSuspiciousHandler.this.metersPerSecond = defaultSpeedMetersPerSecond;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        Logger.e("[ AggregatorSuspiciousHandler ] unable to set speed m/s, fallback to  " + metersPerSecond);
                    }
                })
        );
    }

    public void handleCloseSession(final Session session) {
        metersPerSecond = Settings.defaultSpeedMetersPerSeconds
                .get(Session.ActivityType.Undefined);

        previous = null;
    }

    public Package analyzeComposedPackage(final Package aPackage) {
        analyzeLocations(aPackage.locationsProcessed);
        analyzePackage(aPackage);

        return aPackage;
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

    private void analyzeLocations(final List<Location> passedLocations) {
        final List<Location> locations;
        if (previous != null) {
            locations = new ArrayList<Location>() {{
                add(previous);
                addAll(passedLocations);
            }};
        } else {
            locations = new ArrayList<Location>() {{
                addAll(passedLocations);
            }};
        }

        Location soFar = null;
        for (final Location location : locations) {
            if (soFar != null) {
                final double calculatedDistance = Locations.distance(soFar, location);
                final long seconds = (location.time.getTime() - soFar.time.getTime()) / 1000;
                final double estimatedDistance = seconds * metersPerSecond;
                final double delta = Math.max(calculatedDistance, estimatedDistance) / Math.min(calculatedDistance, estimatedDistance);

                for (final Map.Entry<Range<Float>, Float> entry : rangeMap.entrySet()) {
                    final Range<Float> range = entry.getKey();
                    final float value = entry.getValue();

                    if (delta >= range.getLower() && delta < range.getUpper()) {
                        location.suspicious = value;

                        break;
                    }
                }

                Logger.e("[ AggregatorSuspiciousHandler ] location(" + location.latitude + ", " + location.longitude + ") has estimation delta " + delta + "; suspicious: " + location.suspicious);
            }

            soFar = location;
        }
        previous = soFar;
    }

    private void analyzePackage(final Package aPackage) {
        final float locations = aPackage.locationsProcessed.size() > 0 ?
                calculateLocationAverageSuspiciousness(aPackage.locationsProcessed) : 0.5f;
        final float stepSummary = aPackage.stepSummary.suspicious;
        final float total = (stepSummary * 2 + locations * 5) / 7;

        Logger.e("[ AggregatorSuspiciousHandler ] package(" + locations + ", " + stepSummary + ") : " + total);

        aPackage.suspicious = (stepSummary * 2 + locations * 5) / 7;
    }

    private float calculateLocationAverageSuspiciousness(final List<Location> locations) {
        float total = 0.0f;
        for (final Location location : locations) {
            total += location.suspicious;
        }
        return total / locations.size();
    }

}
