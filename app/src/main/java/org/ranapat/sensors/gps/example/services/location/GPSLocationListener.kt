package org.ranapat.sensors.gps.example.services.location

import android.location.LocationListener
import android.location.LocationManager
import org.ranapat.instancefactory.Fi
import org.ranapat.sensors.gps.example.data.entity.Location
import org.ranapat.sensors.gps.example.services.location.processor.kalman.Basic
import java.util.*

class GPSLocationListener : LocationListener, DataCollector, LocationLogger {
    override val processor: Basic = Fi.get(Basic::class.java)
    override val status: LocationStatus = Fi.get(LocationStatus::class.java)

    override val method: String get() = LocationManager.GPS_PROVIDER
    override val locationSetRaw: ArrayList<Location> = arrayListOf()
    override val locationSetProcessed: ArrayList<Location> = arrayListOf()

    override fun onLocationChanged(location: android.location.Location) {
        log(method, addRaw(location), "raw data")
        log(method, addProcessed(location), "processed data")
    }
}