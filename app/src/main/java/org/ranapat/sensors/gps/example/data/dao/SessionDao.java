package org.ranapat.sensors.gps.example.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.ranapat.sensors.gps.example.data.entity.Session;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

@Dao
public abstract class SessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract long doStore(final Session session);

    @Update
    protected abstract void doUpdate(final Session session);

    @Delete
    protected abstract void doDelete(final Session session);

    @Query("SELECT * FROM sessions ORDER BY id ASC")
    protected abstract List<Session> doFetchAll();

    @Query("SELECT * FROM sessions WHERE id=:id LIMIT 1")
    protected abstract Session doFetchById(final long id);

    @Query("SELECT * FROM sessions WHERE create_synced=0 OR (active=0 AND close_synced=0) OR active=1 ORDER BY id ASC LIMIT 1")
    protected abstract Session doFetchEarliestNotSynced();

    @Query("UPDATE sessions SET active=0, close_synced=0 WHERE active=1")
    protected abstract void doCloseAllOpen();

    @Query("DELETE FROM sessions")
    protected abstract void doDeleteAll();

    @Transaction
    protected Session doKeep(final Session session) {
        if (doFetchById(session.id) == null) {
            return session.duplicate(new HashMap<String, Object>() {{
                put("id", doStore(session));
            }});
        } else {
            doUpdate(session);

            return session;
        }
    }

    public Maybe<List<Session>> fetchAll() {
        return Maybe.fromCallable(new Callable<List<Session>>() {
            @Override
            public List<Session> call() {
                final List<Session> sessions = doFetchAll();

                return sessions;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Session> fetch(final long id) {
        return Maybe.fromCallable(new Callable<Session>() {
            @Override
            public Session call() {
                final Session session = doFetchById(id);

                return session;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Session> fetchEarliestNotSynced() {
        return Maybe.fromCallable(new Callable<Session>() {
            @Override
            public Session call() {
                final Session session = doFetchEarliestNotSynced();

                return session;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Boolean> closeAllOpen() {
        return Maybe.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                doCloseAllOpen();

                return true;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Session> keep(final Session session) {
        return Maybe.fromCallable(new Callable<Session>() {
            @Override
            public Session call() {
                return doKeep(session);
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<Session> delete(final Session session) {
        return Maybe.fromCallable(new Callable<Session>() {
            @Override
            public Session call() {
                doDelete(session);

                return session;
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
