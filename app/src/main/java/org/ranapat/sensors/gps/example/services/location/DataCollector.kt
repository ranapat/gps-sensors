package org.ranapat.sensors.gps.example.services.location

import org.ranapat.sensors.gps.example.data.entity.Location
import org.ranapat.sensors.gps.example.data.entity.LocationSummaries
import org.ranapat.sensors.gps.example.data.entity.LocationSummary
import org.ranapat.sensors.gps.example.services.location.processor.Processor
import java.util.*

interface DataCollector {
    val processor: Processor
    val status: LocationStatus

    val method: String
    val locationSetRaw: ArrayList<Location>
    val locationSetProcessed: ArrayList<Location>

    fun addRaw(location: android.location.Location): Location {
        val time = Date(location.time)
        val locationRaw = Location(
            locationSetRaw.size,
            location.latitude, location.longitude,
            if (location.hasAltitude()) location.altitude else 0.0,
            time,
            if (location.hasAccuracy()) location.accuracy else 0.0f,
            method, null,
            0.0f
        ).accuracyToPercentage()

        locationSetRaw.add(locationRaw)
        status.currentLocationRaw.onNext(locationRaw)

        return locationRaw
    }

    fun addProcessed(location: android.location.Location): Location {
        val time = Date(location.time)
        val locationProcessed = processor.process(Location(
            locationSetProcessed.size,
            location.latitude, location.longitude,
            if (location.hasAltitude()) location.altitude else 0.0,
            time,
            if (location.hasAccuracy()) location.accuracy else 0.0f,
            method, processor.name(),
            0.0f
        )).accuracyToPercentage()

        locationSetProcessed.add(locationProcessed)
        status.currentLocationProcessed.onNext(locationProcessed)

        return locationProcessed
    }

    fun toLocationSummaries(to: Date = Date()): LocationSummaries {
        val resultRaw: ArrayList<Location> = arrayListOf()
        while (locationSetRaw.size > 0 && to > locationSetRaw[0].time) {
            val first: Location = locationSetRaw.removeFirst()
            resultRaw.add(first)
        }

        val resultProcessed: ArrayList<Location> = arrayListOf()
        while (locationSetProcessed.size > 0 && to > locationSetProcessed[0].time) {
            val first: Location = locationSetProcessed.removeFirst()
            resultProcessed.add(first)
        }

        return LocationSummaries(
            LocationSummary(resultRaw),
            LocationSummary(resultProcessed)
        )
    }

}