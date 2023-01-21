package org.ranapat.sensors.gps.example.observable;

import org.ranapat.instancefactory.Fi;
import org.ranapat.sensors.gps.example.data.ApplicationDatabase;
import org.ranapat.sensors.gps.example.data.dao.CaloriesDao;
import org.ranapat.sensors.gps.example.data.dao.LocationDao;
import org.ranapat.sensors.gps.example.data.dao.PackageDao;
import org.ranapat.sensors.gps.example.data.dao.SessionDao;
import org.ranapat.sensors.gps.example.data.dao.StepSummaryDao;
import org.ranapat.sensors.gps.example.data.entity.Calories;
import org.ranapat.sensors.gps.example.data.entity.Location;
import org.ranapat.sensors.gps.example.data.entity.Locations;
import org.ranapat.sensors.gps.example.data.entity.Package;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.data.entity.StepSummary;
import org.ranapat.sensors.gps.example.data.entity.Summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.functions.Function;

public class PackageObservable {
    final private SessionDao sessionDao;
    final private PackageDao packageDao;
    final private LocationDao locationDao;
    final private StepSummaryDao stepSummaryDao;
    final private CaloriesDao caloriesDao;

    public PackageObservable() {
        sessionDao = Fi.get(ApplicationDatabase.class).sessionDao();
        packageDao = Fi.get(ApplicationDatabase.class).packageDao();
        locationDao = Fi.get(ApplicationDatabase.class).locationDao();
        stepSummaryDao = Fi.get(ApplicationDatabase.class).stepSummaryDao();
        caloriesDao = Fi.get(ApplicationDatabase.class).caloriesDao();
    }

    public Maybe<Summary> toSummary(final Package aPackage) {
        final AtomicReference<Session> atomicSession = new AtomicReference<>();
        final AtomicReference<List<Location>> atomicLocationsRaw = new AtomicReference<>();
        final AtomicReference<List<Location>> atomicLocationsProcessed = new AtomicReference<>();
        final AtomicReference<StepSummary> atomicStepSummary = new AtomicReference<>();
        final AtomicReference<Calories> atomicCalories = new AtomicReference<>();

        return sessionDao
                .fetch(aPackage.sessionId)
                .flatMap(new Function<Session, MaybeSource<List<Location>>>() {
                    @Override
                    public MaybeSource<List<Location>> apply(final Session session) {
                        atomicSession.set(session);

                        return locationDao
                                .fetchAllRawByPackageId(aPackage.id)
                                .defaultIfEmpty(new ArrayList<Location>());
                    }
                })
                .flatMap(new Function<List<Location>, MaybeSource<List<Location>>>() {
                    @Override
                    public MaybeSource<List<Location>> apply(final List<Location> locations) {
                        atomicLocationsRaw.set(locations);

                        return locationDao
                                .fetchAllProcessedByPackageId(aPackage.id)
                                .defaultIfEmpty(new ArrayList<Location>());
                    }
                })
                .flatMap(new Function<List<Location>, MaybeSource<StepSummary>>() {
                    @Override
                    public MaybeSource<StepSummary> apply(final List<Location> locations) {
                        atomicLocationsProcessed.set(locations);

                        return stepSummaryDao
                                .fetchByPackageId(aPackage.id)
                                .defaultIfEmpty(StepSummary.undefined());
                    }
                })
                .flatMap(new Function<StepSummary, MaybeSource<Calories>>() {
                    @Override
                    public MaybeSource<Calories> apply(final StepSummary stepSummary) {
                        atomicStepSummary.set(stepSummary);

                        return caloriesDao
                                .fetchByPackageId(aPackage.id)
                                .defaultIfEmpty(Calories.undefined());
                    }
                })
                .flatMap(new Function<Calories, MaybeSource<Summary>>() {
                    @Override
                    public MaybeSource<Summary> apply(final Calories calories) {
                        atomicCalories.set(calories);

                        return Maybe.just(new Summary(
                                atomicSession.get().sessionId,
                                aPackage.index,

                                new Locations(
                                        atomicLocationsRaw.get(),
                                        atomicLocationsProcessed.get()
                                ),
                                atomicStepSummary.get(),

                                aPackage.interval,
                                aPackage.timeUnits,

                                aPackage.suspicious,

                                aPackage.start,
                                aPackage.end
                        ));
                    }
                });
    }

    public Maybe<Package> keep(final Package aPackage) {
        final AtomicReference<Package> atomicPackage = new AtomicReference<>();

        return packageDao
                .keep(aPackage)
                .flatMap(new Function<Package, MaybeSource<List<Location>>>() {
                    @Override
                    public MaybeSource<List<Location>> apply(final Package aPackage) {
                        atomicPackage.set(aPackage);

                        final List<Maybe<Location>> disposables = new ArrayList<>();

                        for (final Location location : aPackage.locationsRaw) {
                            disposables.add(locationDao
                                    .keep(location.duplicate(new HashMap<String, Object>() {{
                                        put("packageId", aPackage.id);
                                    }}))
                            );
                        }

                        return Maybe.concat(disposables)
                                .toList().toMaybe();
                    }
                })
                .flatMap(new Function<List<Location>, MaybeSource<List<Location>>>() {
                    @Override
                    public MaybeSource<List<Location>> apply(final List<Location> locations) {
                        final Package aPackage = atomicPackage.get();

                        aPackage.locationsRaw = locations;

                        final List<Maybe<Location>> disposables = new ArrayList<>();

                        for (final Location location : aPackage.locationsProcessed) {
                            disposables.add(locationDao
                                    .keep(location.duplicate(new HashMap<String, Object>() {{
                                        put("packageId", aPackage.id);
                                    }}))
                            );
                        }

                        return Maybe.concat(disposables)
                                .toList().toMaybe();
                    }
                })
                .flatMap(new Function<List<Location>, MaybeSource<Package>>() {
                    @Override
                    public MaybeSource<Package> apply(final List<Location> locations) {
                        final Package aPackage = atomicPackage.get();

                        aPackage.locationsProcessed = locations;

                        return Maybe.just(aPackage);
                    }
                })
                .flatMap(new Function<Package, MaybeSource<StepSummary>>() {
                    @Override
                    public MaybeSource<StepSummary> apply(final Package aPackage) {
                        return stepSummaryDao
                                .keep(aPackage.stepSummary.duplicate(new HashMap<String, Object>() {{
                                    put("packageId", aPackage.id);
                                }}));
                    }
                })
                .flatMap(new Function<StepSummary, MaybeSource<Package>>() {
                    @Override
                    public MaybeSource<Package> apply(final StepSummary stepSummary) {
                        final Package aPackage = atomicPackage.get();

                        aPackage.stepSummary = stepSummary;

                        return Maybe.just(aPackage);
                    }
                })
                .flatMap(new Function<Package, MaybeSource<Calories>>() {
                    @Override
                    public MaybeSource<Calories> apply(final Package aPackage) {
                        return caloriesDao
                                .keep(aPackage.calories.duplicate(new HashMap<String, Object>() {{
                                    put("packageId", aPackage.id);
                                }}));
                    }
                })
                .flatMap(new Function<Calories, MaybeSource<Package>>() {
                    @Override
                    public MaybeSource<Package> apply(final Calories calories) {
                        final Package aPackage = atomicPackage.get();

                        aPackage.calories = calories;

                        return Maybe.just(aPackage);
                    }
                });
    }

    public Maybe<Package> update(final Package aPackage) {
        return packageDao
                .update(aPackage);
    }

    public Maybe<Package> updateCaloriesAccumulatedRemotely(final Package aPackage, final float accumulatedRemotely) {
        return caloriesDao
                .fetchByPackageId(aPackage.id)
                .flatMap(new Function<Calories, MaybeSource<Calories>>() {
                    @Override
                    public MaybeSource<Calories> apply(final Calories calories) {
                        return caloriesDao
                                .keep(calories.duplicate(new HashMap<String, Object>() {{
                                    put("accumulatedRemotely", accumulatedRemotely);
                                }}));
                    }
                })
                .flatMap(new Function<Calories, MaybeSource<Package>>() {
                    @Override
                    public MaybeSource<Package> apply(final Calories calories) {
                        return Maybe.just(aPackage);
                    }
                });
    }

    public Maybe<Package> fetchEarliestNotSynced(final long sessionId) {
        return packageDao
                .fetchEarliestNotSynced(sessionId);
    }

}
