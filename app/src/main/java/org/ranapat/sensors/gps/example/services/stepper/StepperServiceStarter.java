package org.ranapat.sensors.gps.example.services.stepper;

import static java.util.Arrays.asList;

import android.Manifest;
import android.os.Build;

import org.ranapat.sensors.gps.example.services.ServiceStarter;

import java.util.List;

public final class StepperServiceStarter extends ServiceStarter {
    public final static String PERMISSION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? Manifest.permission.ACTIVITY_RECOGNITION : null;

    @Override
    protected int getJobId() {
        return 100;
    }

    @Override
    protected List<String> getPermissions() {
        return asList(PERMISSION);
    }

    @Override
    protected Class getServiceClassName() {
        return StepperService.class;
    }

}
