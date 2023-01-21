package org.ranapat.sensors.gps.example.observable;

import org.ranapat.instancefactory.Fi;
import org.ranapat.sensors.gps.example.data.ApplicationDatabase;
import org.ranapat.sensors.gps.example.data.dao.CaloriesDao;
import org.ranapat.sensors.gps.example.data.dao.LocationDao;
import org.ranapat.sensors.gps.example.data.dao.PackageDao;
import org.ranapat.sensors.gps.example.data.dao.SessionDao;
import org.ranapat.sensors.gps.example.data.dao.StepSummaryDao;
import org.ranapat.sensors.gps.example.data.entity.Calories;
import org.ranapat.sensors.gps.example.data.entity.Locations;
import org.ranapat.sensors.gps.example.data.entity.Package;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.data.entity.SessionStatistics;
import org.ranapat.sensors.gps.example.data.tools.Sessions;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.functions.Function;

public class SessionObservable {
    final private SessionDao sessionDao;
    final private PackageDao packageDao;
    final private StepSummaryDao stepSummaryDao;
    final private CaloriesDao caloriesDao;
    final private LocationDao locationDao;

    public SessionObservable() {
        sessionDao = Fi.get(ApplicationDatabase.class).sessionDao();
        packageDao = Fi.get(ApplicationDatabase.class).packageDao();
        stepSummaryDao = Fi.get(ApplicationDatabase.class).stepSummaryDao();
        caloriesDao = Fi.get(ApplicationDatabase.class).caloriesDao();
        locationDao = Fi.get(ApplicationDatabase.class).locationDao();
    }

    public Maybe<SessionStatistics> fetchLocalStatistics(final long sessionId) {
        final AtomicReference<Session> atomicSession = new AtomicReference<>();
        final AtomicReference<Package> atomicPackage = new AtomicReference<>();
        final AtomicReference<Calories> atomicCalories = new AtomicReference<>();
        final AtomicReference<Integer> atomicSteps = new AtomicReference<>();
        final AtomicReference<Locations> atomicLocations = new AtomicReference<>();

        return sessionDao
                .fetch(sessionId)
                .flatMap(new Function<Session, MaybeSource<Package>>() {
                    @Override
                    public MaybeSource<Package> apply(final Session session) {
                        atomicSession.set(session);

                        return packageDao
                                .fetchLatestSoFar(sessionId);
                    }
                })
                .flatMap(new Function<Package, MaybeSource<Calories>>() {
                    @Override
                    public MaybeSource<Calories> apply(final Package aPackage) {
                        atomicPackage.set(aPackage);

                        return caloriesDao
                                .fetchByPackageId(aPackage.id);
                    }
                })
                .flatMap(new Function<Calories, MaybeSource<Integer>>() {
                    @Override
                    public MaybeSource<Integer> apply(final Calories calories) {
                        atomicCalories.set(calories);

                        return stepSummaryDao
                                .fetchStepsBySessionId(sessionId);
                    }
                })
                .flatMap(new Function<Integer, MaybeSource<Locations>>() {
                    @Override
                    public MaybeSource<Locations> apply(final Integer steps) {
                        atomicSteps.set(steps);

                        return locationDao
                                .fetchAllBySessionId(sessionId);
                    }
                })
                .flatMap(new Function<Locations, MaybeSource<Boolean>>() {
                    @Override
                    public MaybeSource<Boolean> apply(final Locations locations) {
                        atomicLocations.set(locations);

                        return Maybe.just(true);
                    }
                })
                .flatMap(new Function<Boolean, MaybeSource<SessionStatistics>>() {
                    @Override
                    public MaybeSource<SessionStatistics> apply(final Boolean aBoolean) {
                        final Session session = atomicSession.get();
                        final Package aPackage = atomicPackage.get();
                        final Calories calories = atomicCalories.get();
                        final Integer steps = atomicSteps.get();
                        final Locations locations = atomicLocations.get();

                        return Maybe.just(new SessionStatistics(
                                session.sessionId,
                                session.createdAt,
                                session.closedAt,
                                session.activityType,
                                new SessionStatistics.Duration(
                                        (aPackage.index + 1) * aPackage.interval,
                                        aPackage.timeUnits
                                ),
                                calories.accumulatedLocally,
                                steps,
                                0.0f,
                                org.ranapat.sensors.gps.example.data.tools.Locations.distance(locations),
                                0.0f, 0.0f, 0.0f,
                                aPackage.suspicious
                        ));
                    }
                });
    }

    public Maybe<Session> fetchEarliestNotSynced() {
        return sessionDao
                .fetchEarliestNotSynced();
    }

    public Maybe<Session> create(final Session.ActivityType activityType) {
        final Date createdAt = new Date();

        return sessionDao
                .closeAllOpen()
                .flatMap(new Function<Boolean, MaybeSource<Session>>() {
                    @Override
                    public MaybeSource<Session> apply(final Boolean aBoolean) {
                        return sessionDao.keep(new Session(
                                Sessions.generateLocalId(createdAt),
                                activityType,
                                Calories.CalculationMethod.Undefined,
                                createdAt, null,
                                true,
                                false, null
                        ));
                    }
                });
    }

    public Maybe<Session> close(final long sessionId) {
        final Date closedAt = new Date();

        return sessionDao
                .fetch(sessionId)
                .flatMap(new Function<Session, MaybeSource<Session>>() {
                    @Override
                    public MaybeSource<Session> apply(final Session session) {
                        session.active = false;
                        session.closeSynced = false;
                        session.closedAt = closedAt;

                        return sessionDao
                                .keep(session);
                    }
                });
    }

    public Maybe<Session> keep(final Session session) {
        return sessionDao
                .keep(session);
    }

    public Maybe<Session> delete(final Session session) {
        return sessionDao
                .delete(session);
    }

    public Maybe<Boolean> deleteAll() {
        return sessionDao
                .deleteAll();
    }

}
