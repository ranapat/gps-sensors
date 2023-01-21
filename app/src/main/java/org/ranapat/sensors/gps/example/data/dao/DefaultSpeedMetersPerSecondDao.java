package org.ranapat.sensors.gps.example.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.ranapat.sensors.gps.example.data.entity.DefaultSpeedMetersPerSecond;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.data.entity.User;

import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

@Dao
public abstract class DefaultSpeedMetersPerSecondDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract void doStore(final DefaultSpeedMetersPerSecond defaultSpeedMetersPerSecond);

    @Update
    protected abstract void doUpdate(final DefaultSpeedMetersPerSecond defaultSpeedMetersPerSecond);

    @Delete
    protected abstract void doDelete(final DefaultSpeedMetersPerSecond defaultSpeedMetersPerSecond);

    @Query("SELECT * FROM default_speed_meters_per_second WHERE gender=:gender AND activity_type=:activityType LIMIT 1")
    protected abstract DefaultSpeedMetersPerSecond doFetchByGenderAndActivityType(final User.Gender gender, final Session.ActivityType activityType);

    @Query("DELETE FROM default_speed_meters_per_second")
    protected abstract void doDeleteAll();

    @Transaction
    protected DefaultSpeedMetersPerSecond doKeep(final DefaultSpeedMetersPerSecond defaultSpeedMetersPerSecond) {
        if (doFetchByGenderAndActivityType(defaultSpeedMetersPerSecond.gender, defaultSpeedMetersPerSecond.activityType) == null) {
            doStore(defaultSpeedMetersPerSecond);
        } else {
            doUpdate(defaultSpeedMetersPerSecond);
        }

        return defaultSpeedMetersPerSecond;
    }

    public Maybe<DefaultSpeedMetersPerSecond> fetchByGenderAndActivityType(final User.Gender gender, final Session.ActivityType activityType) {
        return Maybe.fromCallable(new Callable<DefaultSpeedMetersPerSecond>() {
            @Override
            public DefaultSpeedMetersPerSecond call() {
                final DefaultSpeedMetersPerSecond defaultSpeedMetersPerSecond = doFetchByGenderAndActivityType(gender, activityType);

                return defaultSpeedMetersPerSecond;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<DefaultSpeedMetersPerSecond> keep(final DefaultSpeedMetersPerSecond defaultSpeedMetersPerSecond) {
        return Maybe.fromCallable(new Callable<DefaultSpeedMetersPerSecond>() {
            @Override
            public DefaultSpeedMetersPerSecond call() {
                return doKeep(defaultSpeedMetersPerSecond);
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<DefaultSpeedMetersPerSecond> delete(final DefaultSpeedMetersPerSecond defaultSpeedMetersPerSecond) {
        return Maybe.fromCallable(new Callable<DefaultSpeedMetersPerSecond>() {
            @Override
            public DefaultSpeedMetersPerSecond call() {
                doDelete(defaultSpeedMetersPerSecond);

                return defaultSpeedMetersPerSecond;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Boolean> deleteAll() {
        return Maybe.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                doDeleteAll();

                return true;
            }
        }).subscribeOn(Schedulers.io());
    }
}
