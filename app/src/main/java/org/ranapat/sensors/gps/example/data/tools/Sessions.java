package org.ranapat.sensors.gps.example.data.tools;

import org.ranapat.sensors.gps.example.data.entity.Session;

import java.util.Date;

public final class Sessions {
    private Sessions() {
        //
    }

    public static boolean isValid(final String id) {
        return id != null && id.length() == 24;
    }

    public static boolean isValid(final Session session) {
        return session != null && session.id >= 0;
    }

    public static String generateLocalId(final Date createdAt) {
        return "local-" + createdAt.getTime();
    }

}
