package org.ranapat.sensors.gps.example.aggregator.handler;

import org.ranapat.instancefactory.Inject;
import org.ranapat.sensors.gps.example.Settings;
import org.ranapat.sensors.gps.example.data.entity.Calories;
import org.ranapat.sensors.gps.example.data.entity.DefaultHeartRate;
import org.ranapat.sensors.gps.example.data.entity.Package;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.data.entity.User;
import org.ranapat.sensors.gps.example.formula.Formula;
import org.ranapat.sensors.gps.example.formula.calories.CaloriesFactory;
import org.ranapat.sensors.gps.example.observable.DefaultHeartRateObservable;
import org.ranapat.sensors.gps.example.observable.UserObservable;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public final class AggregatorPackageHandler {
    @Inject private final UserObservable userObservable = null;
    @Inject private final DefaultHeartRateObservable defaultHeartRateObservable = null;

    private final CompositeDisposable compositeDisposable;

    private User user;
    private Formula<Float> caloriesFormula;
    private boolean initialized;

    public AggregatorPackageHandler() {
        compositeDisposable = new CompositeDisposable();
    }

    public void clear() {
        reset();
        compositeDisposable.clear();
    }

    public void handleCreateSession(final Session session) {
        reset();

        compositeDisposable.add(userObservable
                .fetch()
                .flatMap(new Function<User, MaybeSource<Integer>>() {
                    @Override
                    public MaybeSource<Integer> apply(final User user) {
                        AggregatorPackageHandler.this.user = user;

                        return defaultHeartRateObservable
                                .fetch(user.gender, session.activityType)
                                .flatMap(new Function<DefaultHeartRate, MaybeSource<Integer>>() {
                                    @Override
                                    public MaybeSource<Integer> apply(final DefaultHeartRate defaultHeartRate) {
                                        return Maybe.just(defaultHeartRate.value);
                                    }
                                })
                                .onErrorReturn(new Function<Throwable, Integer>() {
                                    @Override
                                    public Integer apply(final Throwable throwable) {
                                        return Settings.defaultHeartrates.get(session.activityType);
                                    }
                                });
                    }
                })
                .flatMap(new Function<Integer, MaybeSource<Boolean>>() {
                    @Override
                    public MaybeSource<Boolean> apply(final Integer defaultHeartRate) {
                        caloriesFormula = CaloriesFactory.get(session.caloriesCalculationMethod);
                        caloriesFormula.initialize(
                                user.gender,
                                user.age,
                                user.weight,
                                defaultHeartRate
                        );

                        return Maybe.just(true);
                    }
                })
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(final Boolean aBoolean) {
                        initialized = true;
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        initialized = false;
                    }
                })
        );
    }

    public void handleCloseSession(final Session session) {
        reset();
    }

    public Package normalizeComposedPackage(final Package aPackage) {
        if (isInitialized()) {
            caloriesFormula.setNext((float) (aPackage.index + 1) * (float) aPackage.interval / (float) 60);
            aPackage.calories = new Calories(
                    caloriesFormula.getNext(), null
            );
        }

        return aPackage;
    }

    private boolean isInitialized() {
        return initialized;
    }

    private void reset() {
        user = null;
        caloriesFormula = null;
        initialized = false;
    }
}
