package org.ranapat.sensors.gps.example.aggregator;

import org.ranapat.instancefactory.Inject;
import org.ranapat.instancefactory.InstanceFactory;
import org.ranapat.sensors.gps.example.Settings;
import org.ranapat.sensors.gps.example.aggregator.handler.AggregatorPackageHandler;
import org.ranapat.sensors.gps.example.aggregator.handler.AggregatorServiceHandler;
import org.ranapat.sensors.gps.example.aggregator.handler.AggregatorSuspiciousHandler;
import org.ranapat.sensors.gps.example.data.entity.LocationSummaries;
import org.ranapat.sensors.gps.example.data.entity.Package;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.data.entity.SessionStatistics;
import org.ranapat.sensors.gps.example.data.entity.StepSummary;
import org.ranapat.sensors.gps.example.logger.Logger;
import org.ranapat.sensors.gps.example.observable.PackageObservable;
import org.ranapat.sensors.gps.example.observable.SessionObservable;
import org.ranapat.sensors.gps.example.services.location.LocationService;
import org.ranapat.sensors.gps.example.services.stepper.StepperService;
import org.ranapat.sensors.gps.example.tools.OnItemListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public final class Aggregator {
    public static final long DATA_INTERVAL_IN_SECONDS = Settings.aggregateDataSecondsInterval;
    public static final long SYNC_INTERVAL_IN_SECONDS = Settings.aggregateSyncSecondsInterval;

    @Inject private final AggregatorServiceHandler aggregatorServiceHandler = null;
    @Inject private final AggregatorPackageHandler aggregatorPackageHandler = null;
    @Inject private final AggregatorSuspiciousHandler aggregatorSuspiciousHandler = null;
    @Inject private final AggregatorStatus aggregatorStatus = null;

    @Inject private final SessionObservable sessionObservable = null;
    @Inject private final PackageObservable packageObservable = null;

    private  final Map<Class, AggregatorRegistered> registered;
    private final CompositeDisposable compositeDisposable;

    private long secondsSinceDataCollected;
    private long secondsSinceSync;

    private Date previousComposition;
    private StepSummary currentStepSummary;
    private LocationSummaries currentLocationsSummaries;

    private Disposable looper;
    private int keepInProgress;
    private boolean syncInProgress;

    private Long sessionId;
    private int index;

    public Aggregator() {
        InstanceFactory.inject(this);

        registered = new HashMap<>();

        compositeDisposable = new CompositeDisposable();

        keepInProgress = 0;
        syncInProgress = false;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public Maybe<Boolean> createSession(final Session.ActivityType activityType) {
        sessionId = null;
        index = 0;

        return sessionObservable
                .create(activityType)
                .flatMap(new Function<Session, MaybeSource<Boolean>>() {
                    @Override
                    public MaybeSource<Boolean> apply(final Session session) {
                        sessionId = session.id;

                        aggregatorStatus.sessionCreated.onNext(session);

                        aggregatorServiceHandler.handleCreateSession(session, registered);
                        aggregatorPackageHandler.handleCreateSession(session);
                        aggregatorSuspiciousHandler.handleCreateSession(session);

                        return Maybe.just(true);
                    }
                });
    }

    public Maybe<Boolean> closeSession() {
        if (sessionId != null) {
            final Long _sessionId = sessionId;

            sessionId = null;
            index = 0;

            return sessionObservable
                    .close(_sessionId)
                    .flatMap(new Function<Session, MaybeSource<Boolean>>() {
                        @Override
                        public MaybeSource<Boolean> apply(final Session session) {
                            aggregatorStatus.sessionClosed.onNext(session);

                            aggregatorServiceHandler.handleCloseSession(session, registered);
                            aggregatorPackageHandler.handleCloseSession(session);
                            aggregatorSuspiciousHandler.handleCloseSession(session);

                            return Maybe.just(true);
                        }
                    });
        } else {
            return Maybe.just(false);
        }
    }

    public void register(
            final Object object,
            final AggregatorReporter aggregatorReporter,
            final OnItemListener<Session.ActivityType> sessionCreated,
            final OnItemListener<Session.ActivityType> sessionClosed
    ) {
        final Class _class = object.getClass();
        final boolean shallStart = looper == null;

        registered.put(_class, new AggregatorRegistered(
                aggregatorReporter, sessionCreated, sessionClosed
        ));

        if (shallStart) {
            startLooper();
            startListeners();
        }
    }

    public void unregister(final Object object) {
        final Class _class = object.getClass();

        if (registered.containsKey(_class)) {
            registered.remove(_class);
        }

        aggregatorServiceHandler.handleServiceUnregister(object);

        if (registered.size() == 0) {
            //
        }
    }

    public void stop() {
        closeSession().subscribe();

        stopLooper();
        compositeDisposable.clear();

        aggregatorServiceHandler.clear();
        aggregatorPackageHandler.clear();
        aggregatorSuspiciousHandler.clear();

        syncInProgress = false;
        keepInProgress = 0;
    }

    private void startLooper() {
        stopLooper();

        Logger.e("[ Aggregator ] startLooper");

        looper = Observable
                .interval(1, TimeUnit.SECONDS)
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(final Long iteration) {
                        if (shallCollectData()) {
                            collectData();

                            final Package aPackage = compose();
                            clean();
                            keep(aPackage);
                        }

                        if (shallSync()) {
                            sync();
                        }

                        aggregatorStatus.seconds.onNext(secondsSinceDataCollected);
                        aggregatorStatus.countdown.onNext(DATA_INTERVAL_IN_SECONDS - secondsSinceDataCollected);
                    }
                })
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(final Long iteration) {
                        //
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        //
                    }
                });
    }

    private void startListeners() {
        //
    }

    private void stopLooper() {
        if (looper != null) {
            Logger.e("[ Aggregator ] stopLooper");

            looper.dispose();
            looper = null;
        }
    }

    private boolean shallCollectData() {
        if (++secondsSinceDataCollected > DATA_INTERVAL_IN_SECONDS) {
            secondsSinceDataCollected = 0;
        }

        return secondsSinceDataCollected == 0;
    }

    private boolean shallSync() {
        if (++secondsSinceSync > SYNC_INTERVAL_IN_SECONDS) {
            secondsSinceSync = 0;
        }

        return secondsSinceSync == 0;
    }

    private void collectData() {
        for (Map.Entry<Class, AggregatorRegistered> entry : registered.entrySet()) {
            if (entry.getKey() == StepperService.class) {
                final AggregatorReporter reporter = entry.getValue().reporter.get();
                if (reporter != null) {
                    currentStepSummary = (StepSummary) reporter.report();
                }
            } else if (entry.getKey() == LocationService.class) {
                final AggregatorReporter reporter = entry.getValue().reporter.get();
                if (reporter != null) {
                    currentLocationsSummaries = (LocationSummaries) reporter.report();
                }
            }
        }
    }

    private Package compose() {
        if (sessionId != null) {
            final Date now = new Date();

            final Package rawPackage = new Package(
                    sessionId, index++,
                    Settings.aggregateDataSecondsInterval, TimeUnit.SECONDS,
                    previousComposition, now,
                    0.0f,
                    false,
                    currentLocationsSummaries == null || currentLocationsSummaries.raw == null ? new ArrayList<>() : currentLocationsSummaries.raw.locations,
                    currentLocationsSummaries == null || currentLocationsSummaries.processed == null ? new ArrayList<>() : currentLocationsSummaries.processed.locations,
                    currentStepSummary == null ? StepSummary.undefined() : currentStepSummary,
                    null
            );
            final Package normalizedPackage = aggregatorPackageHandler.normalizeComposedPackage(
                    rawPackage
            );
            final Package analyzedPackage = aggregatorSuspiciousHandler.analyzeComposedPackage(
                    normalizedPackage
            );

            previousComposition = now;

            Logger.e("[ Aggregator ] package composed");

            return analyzedPackage;
        } else {
            // Logger.e("[ Aggregator ] session not started");

            return null;
        }
    }

    private void clean() {
        currentLocationsSummaries = null;
        currentStepSummary = null;
    }

    private void keep(final Package aPackage) {
        if (aPackage != null) {
            ++keepInProgress;

            compositeDisposable.add(packageObservable
                    .keep(aPackage)
                    .flatMap(new Function<Package, MaybeSource<SessionStatistics>>() {
                        @Override
                        public MaybeSource<SessionStatistics> apply(final Package aPackage) {
                            aggregatorStatus.kept.onNext(aPackage);

                            return sessionObservable
                                    .fetchLocalStatistics(aPackage.sessionId);
                        }
                    })
                    .subscribe(new Consumer<SessionStatistics>() {
                        @Override
                        public void accept(final SessionStatistics sessionStatistics) {
                            aggregatorStatus.sessionStatisticsLocal.onNext(sessionStatistics);

                            keepInProgress = Math.max(0, keepInProgress - 1);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(final Throwable throwable) {
                            keepInProgress = Math.max(0, keepInProgress - 1);
                        }
                    })
            );
        }
    }

    private void sync() {
        //
    }

}
