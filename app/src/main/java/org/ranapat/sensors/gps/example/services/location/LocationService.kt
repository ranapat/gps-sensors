package org.ranapat.sensors.gps.example.services.location

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.location.LocationManager
import org.ranapat.instancefactory.Fi
import org.ranapat.sensors.gps.example.Settings
import org.ranapat.sensors.gps.example.aggregator.Aggregator
import org.ranapat.sensors.gps.example.aggregator.AggregatorReporter
import org.ranapat.sensors.gps.example.data.entity.LocationSummaries
import org.ranapat.sensors.gps.example.data.entity.Session
import org.ranapat.sensors.gps.example.logger.Logger
import org.ranapat.sensors.gps.example.tools.OnItemListener
import java.lang.ref.WeakReference

class LocationService : JobService() {
    private var locationManager: WeakReference<LocationManager>? = null

    private var gpsLocationListener: GPSLocationListener? = null
    private var networkLocationListener: NetworkLocationListener? = null

    private val aggregator: Aggregator by lazy { Fi.get(Aggregator::class.java) }
    private val locationServiceStarter: LocationServiceStarter by lazy { Fi.get(LocationServiceStarter::class.java) }

    private val aggregatorReporter: AggregatorReporter = AggregatorReporter {
        val gpsLocationSummaries = gpsLocationListener!!.toLocationSummaries()
        val networkLocationSummaries = networkLocationListener!!.toLocationSummaries()

        LocationSummaries(
            if (gpsLocationSummaries.raw.isEmpty) networkLocationSummaries.raw else gpsLocationSummaries.raw,
            if (gpsLocationSummaries.processed.isEmpty) networkLocationSummaries.processed else gpsLocationSummaries.processed
        )
    }
    private val sessionCreated: OnItemListener<Session.ActivityType> = OnItemListener<Session.ActivityType> { activityType ->
        gpsLocationListener!!.processor.setActivityType(activityType)
        networkLocationListener!!.processor.setActivityType(activityType)
    }
    private val sessionClosed: OnItemListener<Session.ActivityType> = OnItemListener<Session.ActivityType> {
        gpsLocationListener!!.processor.setActivityType(Session.ActivityType.Undefined)
        networkLocationListener!!.processor.setActivityType(Session.ActivityType.Undefined)
    }

    override fun onStartJob(jobParameters: JobParameters?): Boolean {
        return if (handleStartJob()) {
            locationServiceStarter.setStarted()

            Logger.e("[ LocationService ] jobStarted")

            true
        } else {
            false
        }
    }

    override fun onStopJob(jobParameters: JobParameters?): Boolean {
        unregisterSensors()
        unregisterAggregator()

        locationServiceStarter.setNotStarted()

        Logger.e("[ LocationService ] jobStopped")

        return true
    }

    override fun onDestroy() {
        unregisterSensors()
        unregisterAggregator()

        locationServiceStarter.setNotStarted()

        Logger.e("[ LocationService ] onDestroy")

        super.onDestroy()
    }

    private fun handleStartJob(): Boolean {
        return registerSensors() && registerAggregator()
    }

    private fun registerSensors(): Boolean {
        unregisterSensors()

        locationManager = WeakReference(getSystemService(Context.LOCATION_SERVICE) as? LocationManager)

        locationManager?.get()?.let { locationManager ->
            gpsLocationListener = GPSLocationListener()
            networkLocationListener = NetworkLocationListener()

            try {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        Settings.defaultLocationGPSMinTimeMs,
                        Settings.defaultLocationMinDistance,
                        gpsLocationListener!!
                    )
                }
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        Settings.defaultLocationNetworkMinTimeMs,
                        Settings.defaultLocationMinDistance,
                        networkLocationListener!!
                    )
                }

                Logger.e("[ LocationService ] registerSensors: ${LocationManager.GPS_PROVIDER} and ${LocationManager.NETWORK_PROVIDER}")
            } catch (e: SecurityException) {
                return false
            } catch (e: Exception) {
                return false
            }
        }

        return true
    }

    private fun unregisterSensors() {
        Logger.e("[ LocationService ] unregisterSensors")

        if (gpsLocationListener != null) {
            locationManager?.get()?.removeUpdates(gpsLocationListener!!)
            gpsLocationListener = null
        }
        if (networkLocationListener != null) {
            locationManager?.get()?.removeUpdates(networkLocationListener!!)
            networkLocationListener = null
        }

        locationManager?.clear()
        locationManager = null
    }

    private fun registerAggregator(): Boolean {
        aggregator.register(
            this,
            aggregatorReporter,
            sessionCreated,
            sessionClosed
        )

        return true
    }

    private fun unregisterAggregator(): Boolean {
        aggregator.unregister(this)

        return true
    }
}