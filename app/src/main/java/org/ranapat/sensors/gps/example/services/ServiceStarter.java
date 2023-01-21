package org.ranapat.sensors.gps.example.services;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import org.ranapat.instancefactory.Fi;
import org.ranapat.sensors.gps.example.managements.ApplicationContext;
import org.ranapat.sensors.gps.example.permissions.PermissionsChecker;

import java.lang.ref.WeakReference;
import java.util.List;

public abstract class ServiceStarter {
    protected WeakReference<Context> weakContext;

    protected boolean scheduled;
    protected boolean started;

    public ServiceStarter() {
        weakContext = new WeakReference<Context>(Fi.get(ApplicationContext.class));
    }

    protected abstract int getJobId();
    protected abstract List<String> getPermissions();
    protected abstract Class getServiceClassName();

    public boolean start() {
        if (!started) {
            if (PermissionsChecker.check(getPermissions())) {
                final Context context = weakContext.get();
                if (context != null) {
                    final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    final ComponentName componentName = new ComponentName(context, getServiceClassName());
                    final JobInfo jobInfo = new JobInfo.Builder(getJobId(), componentName)
                            .setPeriodic(15 * 60 * 1000)
                            .build();
                    jobScheduler.schedule(jobInfo);

                    scheduled = true;

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean stop() {
        if (scheduled) {
            scheduled = false;

            final Context context = weakContext.get();
            if (context != null) {
                final JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.cancel(getJobId());
            }

            return true;
        } else {
            return false;
        }
    }

    public void setStarted() {
        started = true;
    }

    public void setNotStarted() {
        started = false;
    }
}
