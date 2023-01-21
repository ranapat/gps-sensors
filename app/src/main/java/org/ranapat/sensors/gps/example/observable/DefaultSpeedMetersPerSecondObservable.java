package org.ranapat.sensors.gps.example.observable;

import org.ranapat.instancefactory.Fi;
import org.ranapat.sensors.gps.example.data.ApplicationDatabase;
import org.ranapat.sensors.gps.example.data.dao.DefaultSpeedMetersPerSecondDao;
import org.ranapat.sensors.gps.example.data.entity.DefaultSpeedMetersPerSecond;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.data.entity.User;
import org.ranapat.sensors.gps.example.observable.exceptions.DefaultSpeedMetersPerSecondUndefinedException;

import io.reactivex.Maybe;
import io.reactivex.functions.BiConsumer;

public class DefaultSpeedMetersPerSecondObservable {
    final private DefaultSpeedMetersPerSecondDao dao;

    public DefaultSpeedMetersPerSecondObservable() {
        dao = Fi.get(ApplicationDatabase.class).defaultSpeedMetersPerSecondDao();
    }

    public Maybe<DefaultSpeedMetersPerSecond> fetch(final User.Gender gender, final Session.ActivityType activityType) {
        return dao
                .fetchByGenderAndActivityType(gender, activityType)
                .doOnEvent(new BiConsumer<DefaultSpeedMetersPerSecond, Throwable>() {
                    @Override
                    public void accept(final DefaultSpeedMetersPerSecond defaultSpeedMetersPerSecond, final Throwable throwable) throws DefaultSpeedMetersPerSecondUndefinedException {
                        if (defaultSpeedMetersPerSecond == null) {
                            throw new DefaultSpeedMetersPerSecondUndefinedException();
                        }
                    }
                });
    }

}
