package org.ranapat.sensors.gps.example.ui;

import androidx.lifecycle.ViewModel;

import org.ranapat.instancefactory.Inject;
import org.ranapat.instancefactory.InstanceFactory;
import org.ranapat.sensors.gps.example.data.entity.NetworkStatus;
import org.ranapat.sensors.gps.example.managements.NetworkManager;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public abstract class BaseViewModel extends ViewModel {
    @Inject protected final NetworkManager networkManager = null;

    private final CompositeDisposable compositeDisposable;
    private Boolean isCurrentlyOnline;

    public BaseViewModel() {
        super();

        InstanceFactory.inject(this);

        compositeDisposable = new CompositeDisposable();
        isCurrentlyOnline = null;
    }

    public boolean isOnline() {
        return networkManager.isOnline();
    }

    public boolean isWifi() {
        return networkManager.isWifi();
    }

    public boolean isMobile() {
        return networkManager.isMobile();
    }

    public boolean isEthernet() {
        return networkManager.isEthernet();
    }

    @Override
    protected void onCleared() {
        compositeDisposable.clear();
    }

    protected void triggerNetworkStatus(final NetworkStatus networkStatus) {
        if (isCurrentlyOnline == null || isCurrentlyOnline != networkStatus.isOnline) {
            isCurrentlyOnline = networkStatus.isOnline;

            triggerNetworkStatus(isCurrentlyOnline);
        }
    }

    protected void triggerNetworkStatus(final boolean isOnline) {
        //
    }

    protected void registerForNetworkStatus() {
        subscription(this.networkManager
                .status
                .subscribe(new Consumer<NetworkStatus>() {
                    @Override
                    public void accept(final NetworkStatus networkStatus) {
                        triggerNetworkStatus(networkStatus);
                    }
                })
        );
    }

    protected void subscription(final Disposable disposable) {
        compositeDisposable.add(disposable);
    }
}
