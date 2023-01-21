package org.ranapat.sensors.gps.example;

import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;

import com.google.android.gms.security.ProviderInstaller;

import org.jetbrains.annotations.Contract;
import org.ranapat.instancefactory.DebugFeedback;
import org.ranapat.instancefactory.InstanceFactory;
import org.ranapat.sensors.gps.example.logger.Logger;
import org.ranapat.sensors.gps.example.managements.ActivityLifecycleManager;

import java.io.IOException;
import java.net.SocketException;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import timber.log.Timber;

public class SensorsApplication extends Application {
    private static Application application;

    private ActivityLifecycleManager activityLifecycleManager;

    @Contract(pure = true)
    public static Application getApplication() {
        return application;
    }

    @Nullable
    public static Context getAppContext() {
        return application != null ? application.getApplicationContext() : null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;

        installServiceProviderIfNeeded();
        initializeTimber();
        initializeInstanceFactory();
        initializeApplicationLifecycleManager();
        handleRxUnhandledExceptions();
    }

    private static void installServiceProviderIfNeeded() {
        try {
            ProviderInstaller.installIfNeeded(getAppContext());

            final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            final SSLEngine engine = sslContext.createSSLEngine();
        } catch (Throwable e) {
            //
        }
    }

    private void initializeTimber() {
        if (Settings.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void initializeInstanceFactory() {
        if (Settings.DEBUG) {
            InstanceFactory.setDebugFeedback(new DebugFeedback() {
                private Map<String, Object> map;

                @Override
                public void attachMap(final Map<String, Object> map) {
                    this.map = map;
                }

                @Override
                public void handlePut(final String key, final Object value) {
                    Logger.e("#### InstanceFactory.debugFeedback :: handlePut :: key: " + key + ", value: " + value + ", size: " + map.size());
                }

                @Override
                public void handleGet(final String key) {
                    Logger.e("#### InstanceFactory.debugFeedback :: handleGet :: key: " + key);
                }

                @Override
                public void handleRemove(final String key) {
                    Logger.e("#### InstanceFactory.debugFeedback :: handleRemove :: key: " + key);
                }

                @Override
                public void handleClear() {
                    Logger.e("#### InstanceFactory.debugFeedback :: handleClear");
                }

                @Override
                public void handleInject(final Object instance) {
                    Logger.e("#### InstanceFactory.debugFeedback :: handleInject :: instance: " + instance);
                }
            });
        }
    }

    private void initializeApplicationLifecycleManager() {
        activityLifecycleManager = new ActivityLifecycleManager();
        registerActivityLifecycleCallbacks(activityLifecycleManager);
    }

    private void handleRxUnhandledExceptions() {
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(final Throwable throwable) {
                Throwable e = throwable;
                if (e instanceof UndeliverableException) {
                    e = e.getCause();
                }

                if ((e instanceof IOException) || (e instanceof SocketException)) {
                    // fine, irrelevant network problem or API that throws on cancellation
                    return;
                }

                if (e instanceof InterruptedException) {
                    // fine, some blocking code was interrupted by a dispose call
                    return;
                }

                if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
                    // that's likely a bug in the application
                    return;
                }

                if (e instanceof IllegalStateException) {
                    // that's a bug in RxJava or in a custom operator
                    return;
                }
            }
        });
    }

}
