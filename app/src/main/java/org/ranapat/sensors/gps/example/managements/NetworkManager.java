package org.ranapat.sensors.gps.example.managements;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import org.ranapat.instancefactory.Inject;
import org.ranapat.sensors.gps.example.data.entity.NetworkStatus;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

@lombok.Generated
public class NetworkManager {
    public final PublishSubject<NetworkStatus> status;

    @Inject(type = ApplicationContext.class) private final Context context;

    private final CompositeDisposable compositeDisposable;

    public NetworkManager(
            final Context context
    ) {
        status = PublishSubject.create();

        this.context = context;

        compositeDisposable = new CompositeDisposable();

        registerNetworkChangeCallback(new ConnectivityManager.NetworkCallback() {
            @lombok.Generated
            @Override
            public void onAvailable(final Network network) {
                super.onAvailable(network);

                updateStatus();
            }

            @lombok.Generated
            @Override
            public void onLost(final Network network) {
                super.onLost(network);

                updateStatus();
            }

            @lombok.Generated
            @Override
            public void onCapabilitiesChanged(final Network network, final NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);

                updateStatus();
            }
        });
    }

    public boolean isOnline() {
        return isWifi() || isMobile() || isEthernet();
    }

    public boolean isWifi() {
        return hasInternetForTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }

    public boolean isMobile() {
        return hasInternetForTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
    }

    public boolean isEthernet() {
        return hasInternetForTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
    }

    public void registerNetworkChangeCallback(final ConnectivityManager.NetworkCallback networkCallback) {
        final ConnectivityManager connectivityManager = getConnectivityManager();

        if (connectivityManager != null) {
            final NetworkRequest networkRequest = (new NetworkRequest.Builder())
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();

            connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
        }
    }

    public void unregisterNetworkChangeCallback(final ConnectivityManager.NetworkCallback networkCallback) {
        final ConnectivityManager connectivityManager = getConnectivityManager();

        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    public void updateStatus() {
        status.onNext(new NetworkStatus(
                isOnline(), isWifi(), isMobile(), isEthernet()
        ));
    }

    private boolean hasInternetForTransport(final int transportType) {
        final ConnectivityManager connectivityManager = getConnectivityManager();

        if (connectivityManager != null) {
            final Network[] networks = connectivityManager.getAllNetworks();

            for (final Network network : networks) {
                final NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);

                if (networkCapabilities != null) {
                    final boolean result = networkCapabilities.hasTransport(transportType)
                            && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);

                    if (result) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

}

