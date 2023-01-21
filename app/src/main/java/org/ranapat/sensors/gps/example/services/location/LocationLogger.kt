package org.ranapat.sensors.gps.example.services.location

import org.ranapat.sensors.gps.example.data.entity.Location
import org.ranapat.sensors.gps.example.logger.Logger
import java.text.SimpleDateFormat
import java.util.*

interface LocationLogger {
    fun log(method: String, location: Location, extra: String? = null) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(location.time)

        Logger.e("[ LocationLogger ] " +
                "method: $method noted at $dateFormatter " +
                "latitude: ${location.latitude}, " +
                "longitude: ${location.longitude}, " +
                "altitude: ${location.altitude}, " +
                "accuracy: ${location.accuracy}. " +
                "Extra notes: ${extra ?: "none"}"
        )
    }
}