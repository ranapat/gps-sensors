package org.ranapat.sensors.gps.example.services.location;

import static java.util.Arrays.asList;

import android.Manifest;

import org.ranapat.sensors.gps.example.services.ServiceStarter;

import java.util.List;

public final class LocationServiceStarter extends ServiceStarter {
    public final static String PERMISSION_A = Manifest.permission.ACCESS_FINE_LOCATION;
    public final static String PERMISSION_B = Manifest.permission.ACCESS_COARSE_LOCATION;

    @Override
    protected int getJobId() {
        return 101;
    }

    @Override
    protected List<String> getPermissions() {
        return asList(PERMISSION_A, PERMISSION_B);
    }

    @Override
    protected Class getServiceClassName() {
        return LocationService.class;
    }

}
