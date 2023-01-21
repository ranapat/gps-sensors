package org.ranapat.sensors.gps.example.managements;

import android.content.Context;

import org.ranapat.instancefactory.Static;
import org.ranapat.sensors.gps.example.SensorsApplication;

@Static
public abstract class ApplicationContext extends Context {
    private ApplicationContext() {}

    public static Context getInstance() {
        return SensorsApplication.getAppContext();
    }
}
