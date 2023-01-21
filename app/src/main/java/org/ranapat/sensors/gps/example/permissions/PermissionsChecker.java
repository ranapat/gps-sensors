package org.ranapat.sensors.gps.example.permissions;

import androidx.fragment.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public final class PermissionsChecker {
    private static final Map<String, Boolean> granted = Collections.synchronizedMap(new HashMap<String, Boolean>());

    private static RxPermissions rxPermissions;

    private PermissionsChecker() {
        //
    }

    public static void initialize(final FragmentActivity activity) {
        rxPermissions = new RxPermissions(activity);
    }

    public static void dispose() {
        rxPermissions = null;
    }

    public static Observable<Boolean> request(final String... permissions) {
        if (rxPermissions != null) {
            return rxPermissions
                    .requestEachCombined(permissions)
                    .map(new Function<Permission, Boolean>() {
                        @Override
                        public Boolean apply(final Permission _permission) {
                            final boolean isGranted = _permission.granted;

                            for (final String permission : permissions) {
                                granted.put(permission, isGranted);
                            }

                            return isGranted;
                        }
                    });
        } else {
            return Observable.just(false);
        }
    }

    public static boolean check(final List<String> permissions) {
        if (permissions != null) {
            for (final String permission : permissions) {
                if (granted.containsKey(permission) && granted.get(permission)) {
                    //
                } else {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

}
