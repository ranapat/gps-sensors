package org.ranapat.sensors.gps.example.data.tools;

import org.ranapat.sensors.gps.example.data.entity.Package;

public final class Packages {
    private Packages() {
        //
    }

    public static boolean isValid(final Package aPackage) {
        return aPackage != null && aPackage.id >= 0;
    }

}
