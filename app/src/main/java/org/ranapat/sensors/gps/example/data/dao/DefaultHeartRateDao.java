package org.ranapat.sensors.gps.example.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.ranapat.sensors.gps.example.data.entity.DefaultHeartRate;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.data.entity.User;

import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

@Dao
public abstract class DefaultHeartRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract void doStore(final DefaultHeartRate defaultHeartRate);

    @Update
    protected abstract void doUpdate(final DefaultHeartRate defaultHeartRate);

    @Delete
    protected abstract void doDelete(final DefaultHeartRate defaultHeartRate);

    @Query("SELECT * FROM default_heart_rate WHERE gender=:gender AND activity_type=:activityType LIMIT 1")
    protected abstract DefaultHeartRate doFetchByGenderAndActivityType(final User.Gender gender, final Session.ActivityType activityType);

    @Query("DELETE FROM default_heart_rate")
    protected abstract void doDeleteAll();

    @Transaction
    protected DefaultHeartRate doKeep(final DefaultHeartRate defaultHeartRate) {
        if (doFetchByGenderAndActivityType(defaultHeartRate.gender, defaultHeartRate.activityType) == null) {
            doStore(defaultHeartRate);
        } else {
            doUpdate(defaultHeartRate);
        }

        return defaultHeartRate;
    }

    public Maybe<DefaultHeartRate> fetchByGenderAndActivityType(final User.Gender gender, final Session.ActivityType activityType) {
        return Maybe.fromCallable(new Callable<DefaultHeartRate>() {
            @Override
            public DefaultHeartRate call() {
                final DefaultHeartRate defaultHeartRate = doFetchByGenderAndActivityType(gender, activityType);

                return defaultHeartRate;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<DefaultHeartRate> keep(final DefaultHeartRate defaultHeartRate) {
        return Maybe.fromCallable(new Callable<DefaultHeartRate>() {
            @Override
            public DefaultHeartRate call() {
                return doKeep(defaultHeartRate);
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<DefaultHeartRate> delete(final DefaultHeartRate defaultHeartRate) {
        return Maybe.fromCallable(new Callable<DefaultHeartRate>() {
            @Override
            public DefaultHeartRate call() {
                doDelete(defaultHeartRate);

                return defaultHeartRate;
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
