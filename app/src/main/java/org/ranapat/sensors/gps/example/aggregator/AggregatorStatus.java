package org.ranapat.sensors.gps.example.aggregator;

import org.json.JSONObject;
import org.ranapat.sensors.gps.example.data.entity.Package;
import org.ranapat.sensors.gps.example.data.entity.Session;
import org.ranapat.sensors.gps.example.data.entity.SessionStatistics;

import io.reactivex.subjects.PublishSubject;

public final class AggregatorStatus {
    final public PublishSubject<Long> seconds;
    final public PublishSubject<Long> countdown;
    final public PublishSubject<Package> kept;
    final public PublishSubject<Session> sessionCreated;
    final public PublishSubject<Session> sessionClosed;
    final public PublishSubject<SessionStatistics> sessionStatisticsLocal;
    final public PublishSubject<JSONObject> sessionStatisticsRemote;

    public AggregatorStatus() {
        seconds = PublishSubject.create();
        countdown = PublishSubject.create();
        kept = PublishSubject.create();
        sessionCreated = PublishSubject.create();
        sessionClosed = PublishSubject.create();
        sessionStatisticsLocal = PublishSubject.create();
        sessionStatisticsRemote = PublishSubject.create();
    }
}
