package org.ranapat.sensors.gps.example.data.entity;

public class NetworkStatus implements DataEntity {
    public final boolean isOnline;
    public final boolean isWifi;
    public final boolean isMobile;
    public final boolean isEthernet;

    public NetworkStatus(
            final boolean isOnline,
            final boolean isWifi,
            final boolean isMobile,
            final boolean isEthernet
    ) {
        this.isOnline = isOnline;
        this.isWifi = isWifi;
        this.isMobile = isMobile;
        this.isEthernet = isEthernet;
    }
}
