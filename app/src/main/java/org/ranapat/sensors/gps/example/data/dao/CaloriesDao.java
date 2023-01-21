package org.ranapat.sensors.gps.example.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.ranapat.sensors.gps.example.data.entity.Calories;

import java.util.HashMap;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

@Dao
public abstract class CaloriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract long doStore(final Calories calories);

    @Update
    protected abstract void doUpdate(final Calories calories);

    @Delete
    protected abstract void doDelete(final Calories calories);

    @Query("SELECT * FROM calories WHERE package_id=:packageId LIMIT 1")
    protected abstract Calories doFetchByPackageId(final long packageId);

    @Query("SELECT * FROM calories WHERE id=:id LIMIT 1")
    protected abstract Calories doFetchById(final long id);

    @Transaction
    protected Calories doKeep(final Calories calories) {
        if (doFetchById(calories.id) == null) {
            return calories.duplicate(new HashMap<String, Object>() {{
                put("id", doStore(calories));
            }});
        } else {
            doUpdate(calories);

            return calories;
        }
    }

    public Maybe<Calories> fetchByPackageId(final long packageId) {
        return Maybe.fromCallable(new Callable<Calories>() {
            @Override
            public Calories call() {
                final Calories calories = doFetchByPackageId(packageId);

                return calories;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Calories> fetch(final long id) {
        return Maybe.fromCallable(new Callable<Calories>() {
            @Override
            public Calories call() {
                final Calories calories = doFetchById(id);

                return calories;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Calories> keep(final Calories calories) {
        return Maybe.fromCallable(new Callable<Calories>() {
            @Override
            public Calories call() {
                return doKeep(calories);
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Calories> delete(final Calories calories) {
        return Maybe.fromCallable(new Callable<Calories>() {
            @Override
            public Calories call() {
                doDelete(calories);

                return calories;
            }
        }).subscribeOn(Schedulers.io());
    }
}
