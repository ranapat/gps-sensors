package org.ranapat.sensors.gps.example.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.ranapat.sensors.gps.example.data.entity.Location;
import org.ranapat.sensors.gps.example.data.entity.Locations;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

@Dao
public abstract class LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract long doStore(final Location location);

    @Update
    protected abstract void doUpdate(final Location location);

    @Delete
    protected abstract void doDelete(final Location location);

    @Query("SELECT * FROM locations WHERE package_id=:packageId AND processed IS NULL ORDER BY `index` ASC")
    protected abstract List<Location> doFetchAllRawByPackageId(final long packageId);

    @Query("SELECT * FROM locations WHERE package_id=:packageId AND processed IS NOT NULL ORDER BY `index` ASC")
    protected abstract List<Location> doFetchAllProcessedByPackageId(final long packageId);

    @Query("SELECT * FROM locations WHERE package_id IN (SELECT id from packages WHERE session_id=:sessionId ORDER BY `index` ASC) AND processed IS NULL ORDER BY package_id ASC, `index` ASC")
    protected abstract List<Location> doFetchAllRawBySessionId(final long sessionId);

    @Query("SELECT * FROM locations WHERE package_id IN (SELECT id from packages WHERE session_id=:sessionId ORDER BY `index` ASC) AND processed IS NOT NULL ORDER BY package_id ASC, `index` ASC")
    protected abstract List<Location> doFetchAllProcessedBySessionId(final long sessionId);

    @Query("SELECT * FROM locations WHERE id=:id LIMIT 1")
    protected abstract Location doFetchById(final long id);

    @Transaction
    protected Location doKeep(final Location location) {
        if (doFetchById(location.id) == null) {
            return location.duplicate(new HashMap<String, Object>() {{
                put("id", doStore(location));
            }});
        } else {
            doUpdate(location);

            return location;
        }
    }

    public Maybe<List<Location>> fetchAllRawByPackageId(final long packageId) {
        return Maybe.fromCallable(new Callable<List<Location>>() {
            @Override
            public List<Location> call() {
                final List<Location> locations = doFetchAllRawByPackageId(packageId);

                return locations;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<List<Location>> fetchAllProcessedByPackageId(final long packageId) {
        return Maybe.fromCallable(new Callable<List<Location>>() {
            @Override
            public List<Location> call() {
                final List<Location> locations = doFetchAllProcessedByPackageId(packageId);

                return locations;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<List<Location>> fetchAllRawBySessionId(final long sessionId) {
        return Maybe.fromCallable(new Callable<List<Location>>() {
            @Override
            public List<Location> call() {
                final List<Location> locations = doFetchAllRawBySessionId(sessionId);

                return locations;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<List<Location>> fetchAllProcessedBySessionId(final long sessionId) {
        return Maybe.fromCallable(new Callable<List<Location>>() {
            @Override
            public List<Location> call() {
                final List<Location> locations = doFetchAllProcessedBySessionId(sessionId);

                return locations;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Locations> fetchAllBySessionId(final long sessionId) {
        return Maybe.fromCallable(new Callable<Locations>() {
            @Override
            public Locations call() {
                return new Locations(
                        doFetchAllRawBySessionId(sessionId),
                        doFetchAllProcessedBySessionId(sessionId)
                );
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Location> fetch(final long id) {
        return Maybe.fromCallable(new Callable<Location>() {
            @Override
            public Location call() {
                final Location location = doFetchById(id);

                return location;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Location> keep(final Location location) {
        return Maybe.fromCallable(new Callable<Location>() {
            @Override
            public Location call() {
                return doKeep(location);
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Location> delete(final Location location) {
        return Maybe.fromCallable(new Callable<Location>() {
            @Override
            public Location call() {
                doDelete(location);

                return location;
            }
        }).subscribeOn(Schedulers.io());
    }
}
