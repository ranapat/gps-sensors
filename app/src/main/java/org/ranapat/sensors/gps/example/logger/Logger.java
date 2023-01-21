package org.ranapat.sensors.gps.example.logger;

import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;

public final class Logger {
    public static final PublishSubject<String> log = PublishSubject.create();

    private Logger() {
        //
    }

    public static void e(final String message) {
        log.onNext(message);

        Timber.e(message);
    }
}
