package org.ranapat.sensors.gps.example.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.ranapat.sensors.gps.example.data.entity.User;

import java.util.HashMap;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

@Dao
public abstract class UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract long doStore(final User user);

    @Update
    protected abstract void doUpdate(final User user);

    @Delete
    protected abstract void doDelete(final User user);

    @Query("DELETE FROM users")
    protected abstract void doDeleteAll();

    @Query("SELECT * FROM users WHERE id=:id LIMIT 1")
    protected abstract User doFetchById(final long id);

    @Query("SELECT * FROM users LIMIT 1")
    protected abstract User doFetch();

    @Transaction
    protected User doKeep(final User user) {
        if (doFetchById(user.id) == null) {
            doDeleteAll();

            return user.duplicate(new HashMap<String, Object>() {{
                put("id", doStore(user));
            }});
        } else {
            doUpdate(user);

            return user;
        }
    }

    public Maybe<User> fetch() {
        return Maybe.fromCallable(new Callable<User>() {
            @Override
            public User call() {
                final User user = doFetch();

                return user;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<User> keep(final User user) {
        return Maybe.fromCallable(new Callable<User>() {
            @Override
            public User call() {
                return doKeep(user);
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<User> delete(final User user) {
        return Maybe.fromCallable(new Callable<User>() {
            @Override
            public User call() {
                doDelete(user);

                return user;
            }
        }).subscribeOn(Schedulers.io());
    }
}