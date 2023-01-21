package org.ranapat.sensors.gps.example.data.tools;

public class Locations {
    private final static int R = 6371;

    private Locations() {}

    public static double distance(final org.ranapat.sensors.gps.example.data.entity.Locations locations) {
        double distance = 0;
        org.ranapat.sensors.gps.example.data.entity.Location previous = null;

        for (final org.ranapat.sensors.gps.example.data.entity.Location current : locations.processed) {
            if (previous != null) {
                distance += distance(previous, current);
            }

            previous = current;
        }

        return distance;
    }

    public static double distance(
            final org.ranapat.sensors.gps.example.data.entity.Location pointA,
            final org.ranapat.sensors.gps.example.data.entity.Location pointB
    ) {
        double latDistance = Math.toRadians(pointB.latitude - pointA.latitude);
        double lonDistance = Math.toRadians(pointB.longitude - pointA.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(pointA.latitude)) * Math.cos(Math.toRadians(pointB.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000;

        double height = 0;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public static double distanceWithAltitude(
            final org.ranapat.sensors.gps.example.data.entity.Location pointA,
            final org.ranapat.sensors.gps.example.data.entity.Location pointB
    ) {
        double latDistance = Math.toRadians(pointB.latitude - pointA.latitude);
        double lonDistance = Math.toRadians(pointB.longitude - pointA.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(pointA.latitude)) * Math.cos(Math.toRadians(pointB.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000;

        double height = pointA.altitude - pointB.altitude;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public static double distanceSimplified(
            final org.ranapat.sensors.gps.example.data.entity.Location pointA,
            final org.ranapat.sensors.gps.example.data.entity.Location pointB
    ) {
        final double R = 6371e3;
        final double φ1 = (pointA.latitude * Math.PI) / 180;
        final double φ2 = (pointB.latitude * Math.PI) / 180;
        final double Δφ = ((pointB.latitude - pointA.latitude) * Math.PI) / 180;
        final double Δλ = ((pointB.longitude - pointA.longitude) * Math.PI) / 180;

        final double a =
                Math.sin(Δφ / 2) * Math.sin(Δφ / 2) + Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
