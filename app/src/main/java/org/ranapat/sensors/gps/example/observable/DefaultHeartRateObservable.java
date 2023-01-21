package org.ranapat.sensors.gps.example.observable;

import org.ranapat.instancefactory.Fi;
import org.ranapat.sensors.gps.example.data.ApplicationDatabase;
import org.ranapat.sensors.gps.example.data.dao.DefaultHeartRateDao;
import org.ranapat.sensors.gps.example.data.entity.DefaultHeartRate;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.data.entity.User;
import org.ranapat.sensors.gps.example.observable.exceptions.DefaultHeartRateUndefinedException;

import io.reactivex.Maybe;
import io.reactivex.functions.BiConsumer;

public class DefaultHeartRateObservable {
    final private DefaultHeartRateDao dao;

    public DefaultHeartRateObservable() {
        dao = Fi.get(ApplicationDatabase.class).defaultHeartRateDao();
    }

    public Maybe<DefaultHeartRate> fetch(final User.Gender gender, final Session.ActivityType activityType) {
        return dao
                .fetchByGenderAndActivityType(gender, activityType)
                .doOnEvent(new BiConsumer<DefaultHeartRate, Throwable>() {
                    @Override
                    public void accept(final DefaultHeartRate defaultHeartRate, final Throwable throwable) throws DefaultHeartRateUndefinedException {
                        if (defaultHeartRate == null) {
                            throw new DefaultHeartRateUndefinedException();
                        }
                    }
                });
    }

}
