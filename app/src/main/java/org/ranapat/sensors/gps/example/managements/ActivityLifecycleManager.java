package org.ranapat.sensors.gps.example.managements;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

@lombok.Generated
public class ActivityLifecycleManager implements Application.ActivityLifecycleCallbacks {
    private int activityReferences;
    private boolean isActivityChangingConfigurations;

    public ActivityLifecycleManager() {
        activityReferences = 0;
        isActivityChangingConfigurations = false;
    }

    @Override
    public void onActivityStarted(final Activity activity) {
        if (++activityReferences == 1 && !isActivityChangingConfigurations) {
            // to focus
        }
    }

    @Override
    public void onActivityStopped(final Activity activity) {
        isActivityChangingConfigurations = activity.isChangingConfigurations();
        if (--activityReferences == 0 && !isActivityChangingConfigurations) {
            // to background
        }
    }

    @Override
    public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {
        //
    }

    @Override
    public void onActivityResumed(final Activity activity) {
        //
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        //
    }

    @Override
    public void onActivitySaveInstanceState(final Activity activity, final Bundle outState) {
        //
    }

    @Override
    public void onActivityDestroyed(final Activity activity) {
        //
    }
}
