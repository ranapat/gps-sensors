package org.ranapat.sensors.gps.example.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.ranapat.sensors.gps.example.data.entity.Package;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

@Dao
public abstract class PackageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract long doStore(final Package aPackage);

    @Update
    protected abstract void doUpdate(final Package aPackage);

    @Delete
    protected abstract void doDelete(final Package aPackage);

    @Query("SELECT * FROM packages")
    protected abstract List<Package> doFetchAll();

    @Query("SELECT * FROM packages WHERE session_id=:sessionId ORDER BY `index` ASC")
    protected abstract List<Package> doFetchAllBySessionId(final String sessionId);

    @Query("SELECT * FROM packages WHERE id=:id LIMIT 1")
    protected abstract Package doFetchById(final long id);

    @Query("SELECT * FROM packages WHERE session_id=:sessionId AND synced=0 ORDER BY id ASC LIMIT 1")
    protected abstract Package doFetchEarliestNotSynced(final long sessionId);

    @Query("SELECT * FROM packages WHERE session_id=:sessionId ORDER BY id DESC LIMIT 1")
    protected abstract Package doFetchLatestSoFar(final long sessionId);

    @Transaction
    protected Package doKeep(final Package aPackage) {
        if (doFetchById(aPackage.id) == null) {
            return aPackage.duplicate(new HashMap<String, Object>() {{
                put("id", doStore(aPackage));
            }});
        } else {
            doUpdate(aPackage);

            return aPackage;
        }
    }

    public Maybe<List<Package>> fetchAll() {
        return Maybe.fromCallable(new Callable<List<Package>>() {
            @Override
            public List<Package> call() {
                final List<Package> packages = doFetchAll();

                return packages;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<List<Package>> fetchAllBySessionId(final String sessionId) {
        return Maybe.fromCallable(new Callable<List<Package>>() {
            @Override
            public List<Package> call() {
                final List<Package> packages = doFetchAllBySessionId(sessionId);

                return packages;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Package> fetch(final long id) {
        return Maybe.fromCallable(new Callable<Package>() {
            @Override
            public Package call() {
                final Package aPackage = doFetchById(id);

                return aPackage;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Package> fetchEarliestNotSynced(final long sessionId) {
        return Maybe.fromCallable(new Callable<Package>() {
            @Override
            public Package call() {
                final Package aPackage = doFetchEarliestNotSynced(sessionId);

                return aPackage;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Package> fetchLatestSoFar(final long sessionId) {
        return Maybe.fromCallable(new Callable<Package>() {
            @Override
            public Package call() {
                final Package aPackage = doFetchLatestSoFar(sessionId);

                return aPackage;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Package> keep(final Package aPackage) {
        return Maybe.fromCallable(new Callable<Package>() {
            @Override
            public Package call() {
                return doKeep(aPackage);
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Package> update(final Package aPackage) {
        return Maybe.fromCallable(new Callable<Package>() {
            @Override
            public Package call() {
                doUpdate(aPackage);

                return aPackage;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Package> delete(final Package aPackage) {
        return Maybe.fromCallable(new Callable<Package>() {
            @Override
            public Package call() {
                doDelete(aPackage);

                return aPackage;
            }
        }).subscribeOn(Schedulers.io());
    }
}
