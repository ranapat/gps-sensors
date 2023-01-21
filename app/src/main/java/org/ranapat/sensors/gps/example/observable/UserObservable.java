package org.ranapat.sensors.gps.example.observable;

import org.ranapat.instancefactory.Fi;
import org.ranapat.sensors.gps.example.data.ApplicationDatabase;
import org.ranapat.sensors.gps.example.data.dao.UserDao;
import org.ranapat.sensors.gps.example.data.entity.User;
import org.ranapat.sensors.gps.example.observable.exceptions.UserUndefinedException;

import io.reactivex.Maybe;
import io.reactivex.functions.BiConsumer;

public class UserObservable {
    final private UserDao dao;

    public UserObservable() {
        dao = Fi.get(ApplicationDatabase.class).userDao();
    }

    public Maybe<User> fetch() {
        return dao
                .fetch()
                .defaultIfEmpty(new User(1L, "default", 25, User.Gender.Other, null, 65, 10))
                .doOnEvent(new BiConsumer<User, Throwable>() {
                    @Override
                    public void accept(final User user, final Throwable throwable) throws UserUndefinedException {
                        if (user == null) {
                            throw new UserUndefinedException();
                        }
                    }
                });
    }

    public Maybe<User> keep(final User user) {
        return dao
                .keep(user);
    }

    public Maybe<User> delete(final User user) {
        return dao
                .delete(user);
    }

}
