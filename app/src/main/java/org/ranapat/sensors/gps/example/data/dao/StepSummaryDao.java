package org.ranapat.sensors.gps.example.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import org.ranapat.sensors.gps.example.data.entity.StepSummary;

import java.util.HashMap;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

@Dao
public abstract class StepSummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract long doStore(final StepSummary stepSummary);

    @Update
    protected abstract void doUpdate(final StepSummary stepSummary);

    @Delete
    protected abstract void doDelete(final StepSummary stepSummary);

    @Query("SELECT SUM(step_summaries.steps) FROM step_summaries " +
            "LEFT JOIN packages ON packages.id=step_summaries.package_id " +
            "LEFT JOIN sessions ON sessions.id=packages.session_id " +
            "WHERE sessions.id=:sessionId")
    protected abstract int doFetchStepsBySessionId(final long sessionId);

    @Query("SELECT * FROM step_summaries WHERE package_id=:packageId LIMIT 1")
    protected abstract StepSummary doFetchByPackageId(final long packageId);

    @Query("SELECT * FROM step_summaries WHERE id=:id LIMIT 1")
    protected abstract StepSummary doFetchById(final long id);

    @Transaction
    protected StepSummary doKeep(final StepSummary stepSummary) {
        if (doFetchById(stepSummary.id) == null) {
            return stepSummary.duplicate(new HashMap<String, Object>() {{
                put("id", doStore(stepSummary));
            }});
        } else {
            doUpdate(stepSummary);

            return stepSummary;
        }
    }

    public Maybe<Integer> fetchStepsBySessionId(final long sessionId) {
        return Maybe.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() {
                final int steps = doFetchStepsBySessionId(sessionId);

                return steps;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<StepSummary> fetchByPackageId(final long packageId) {
        return Maybe.fromCallable(new Callable<StepSummary>() {
            @Override
            public StepSummary call() {
                final StepSummary stepSummary = doFetchByPackageId(packageId);

                return stepSummary;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<StepSummary> fetch(final long id) {
        return Maybe.fromCallable(new Callable<StepSummary>() {
            @Override
            public StepSummary call() {
                final StepSummary stepSummary = doFetchById(id);

                return stepSummary;
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<StepSummary> keep(final StepSummary stepSummary) {
        return Maybe.fromCallable(new Callable<StepSummary>() {
            @Override
            public StepSummary call() {
                return doKeep(stepSummary);
            }
        }).subscribeOn(Schedulers.io());
    }

    public Maybe<StepSummary> delete(final StepSummary stepSummary) {
        return Maybe.fromCallable(new Callable<StepSummary>() {
            @Override
            public StepSummary call() {
                doDelete(stepSummary);

                return stepSummary;
            }
        }).subscribeOn(Schedulers.io());
    }
}
