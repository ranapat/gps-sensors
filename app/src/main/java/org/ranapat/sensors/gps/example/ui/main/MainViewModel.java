package org.ranapat.sensors.gps.example.ui.main;

import static org.ranapat.sensors.gps.example.ui.common.States.LOADING;
import static org.ranapat.sensors.gps.example.ui.common.States.READY;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;
import org.ranapat.instancefactory.Inject;
import org.ranapat.instancefactory.InstanceFactory;
import org.ranapat.sensors.gps.example.aggregator.Aggregator;
import org.ranapat.sensors.gps.example.aggregator.AggregatorStatus;
import org.ranapat.sensors.gps.example.data.entity.Location;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.data.entity.SessionStatistics;
import org.ranapat.sensors.gps.example.data.entity.User;
import org.ranapat.sensors.gps.example.observable.UserObservable;
import org.ranapat.sensors.gps.example.services.location.LocationServiceStarter;
import org.ranapat.sensors.gps.example.services.location.LocationStatus;
import org.ranapat.sensors.gps.example.services.stepper.StepperServiceStarter;
import org.ranapat.sensors.gps.example.services.stepper.StepperStatus;
import org.ranapat.sensors.gps.example.ui.BaseViewModel;

import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public class MainViewModel extends BaseViewModel {
    final public PublishSubject<String> state;
    final public PublishSubject<Class<? extends AppCompatActivity>> next;
    final public PublishSubject<User> user;
    final public PublishSubject<Boolean> session;
    final public PublishSubject<Location> currentLocationRaw;
    final public PublishSubject<Location> currentLocationProcessed;
    final public PublishSubject<Integer> stepsSoFar;
    final public PublishSubject<Long> countdown;
    final public PublishSubject<Session> sessionCreated;
    final public PublishSubject<Session> sessionClosed;
    final public PublishSubject<SessionStatistics> sessionStatisticsLocal;
    final public PublishSubject<JSONObject> sessionStatisticsRemote;

    @Inject final private LocationStatus locationStatus = null;
    @Inject final private StepperStatus stepperStatus = null;
    @Inject final private Aggregator aggregator = null;
    @Inject final private AggregatorStatus aggregatorStatus = null;
    @Inject final private StepperServiceStarter stepperServiceStarter = null;
    @Inject final private LocationServiceStarter locationServiceStarter = null;
    @Inject final private UserObservable userObservable = null;

    public MainViewModel() {
        super();

        InstanceFactory.inject(this);

        state = PublishSubject.create();
        next = PublishSubject.create();
        user = PublishSubject.create();
        session = PublishSubject.create();
        currentLocationRaw = PublishSubject.create();
        currentLocationProcessed = PublishSubject.create();
        stepsSoFar = PublishSubject.create();
        countdown = PublishSubject.create();
        sessionCreated = PublishSubject.create();
        sessionClosed = PublishSubject.create();
        sessionStatisticsLocal = PublishSubject.create();
        sessionStatisticsRemote = PublishSubject.create();
    }

    public void initialize() {
        state.onNext(LOADING);

        subscription(userObservable
                .fetch()
                .subscribe(new Consumer<User>() {
                    @Override
                    public void accept(final User user) {
                        MainViewModel.this.user.onNext(user);
                        state.onNext(READY);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        //
                    }
                })
        );

        initializeListeners();
    }

    public void onResume() {
        subscription(userObservable
                .fetch()
                .subscribe(new Consumer<User>() {
                    @Override
                    public void accept(final User user) {
                        propagateSessionId();

                        MainViewModel.this.user.onNext(user);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        //
                    }
                })
        );
    }

    public void createSession(final Session.ActivityType activityType) {
        subscription(aggregator
                .createSession(activityType)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(final Boolean success) {
                        propagateSessionId();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        throwable.printStackTrace();
                    }
                })
        );
    }

    public void closeSession() {
        subscription(aggregator
                .closeSession()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(final Boolean success) {
                        propagateSessionId();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        throwable.printStackTrace();
                    }
                })
        );
    }

    public void startStepperService() {
        stepperServiceStarter.start();
    }

    public void stopStepperService() {
        stepperServiceStarter.stop();
    }

    public void startLocationService() {
        locationServiceStarter.start();
    }

    public void stopLocationService() {
        locationServiceStarter.stop();
    }

    public void stopAggregator() {
        aggregator.stop();
    }

    private void propagateSessionId() {
        session.onNext(aggregator.getSessionId() != null);
    }

    private void initializeListeners() {
        subscription(locationStatus
                .currentLocationRaw
                .subscribe(new Consumer<Location>() {
                    @Override
                    public void accept(final Location location) {
                        currentLocationRaw.onNext(location);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        //
                    }
                })
        );
        subscription(locationStatus
                .currentLocationProcessed
                .subscribe(new Consumer<Location>() {
                    @Override
                    public void accept(final Location location) {
                        currentLocationProcessed.onNext(location);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        //
                    }
                })
        );

        subscription(stepperStatus
                .stepsSoFar
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(final Integer steps) {
                        stepsSoFar.onNext(steps);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        //
                    }
                })
        );
        subscription(aggregatorStatus
                .countdown
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(final Long seconds) {
                        countdown.onNext(seconds);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        //
                    }
                })
        );
        subscription(aggregatorStatus
                .sessionCreated
                .subscribe(new Consumer<Session>() {
                    @Override
                    public void accept(final Session session) {
                        MainViewModel.this.sessionCreated.onNext(session);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        //
                    }
                })
        );
        subscription(aggregatorStatus
                .sessionClosed
                .subscribe(new Consumer<Session>() {
                    @Override
                    public void accept(final Session session) {
                        MainViewModel.this.sessionClosed.onNext(session);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        //
                    }
                })
        );
        subscription(aggregatorStatus
                .sessionStatisticsLocal
                .subscribe(new Consumer<SessionStatistics>() {
                    @Override
                    public void accept(final SessionStatistics sessionStatistics) {
                        MainViewModel.this.sessionStatisticsLocal.onNext(sessionStatistics);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        //
                    }
                })
        );
        subscription(aggregatorStatus
                .sessionStatisticsRemote
                .subscribe(new Consumer<JSONObject>() {
                    @Override
                    public void accept(final JSONObject jsonObject) {
                        MainViewModel.this.sessionStatisticsRemote.onNext(jsonObject);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) {
                        //
                    }
                })
        );
    }
}