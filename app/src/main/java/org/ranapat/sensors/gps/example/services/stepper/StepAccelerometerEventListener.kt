package org.ranapat.sensors.gps.example.services.stepper

import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import org.ranapat.instancefactory.Fi
import org.ranapat.sensors.gps.example.Settings
import org.ranapat.sensors.gps.example.data.entity.StepSet
import java.util.*
import kotlin.math.sqrt

class StepAccelerometerEventListener : SensorEventListener, AccuracyCalculator, DataCollector, StepLogger {
    override var accuracy: Int = SensorManager.SENSOR_STATUS_NO_CONTACT
    override val multiplier: Double get() = 0.33

    override val method: String get() = PackageManager.FEATURE_SENSOR_ACCELEROMETER
    override val stepSet: ArrayList<StepSet> = arrayListOf()
    override val status: StepperStatus = Fi.get(StepperStatus::class.java)

    private var previousMagnitude: Double = 0.0
    private var previousStepDate: Date? = null

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        this.accuracy = accuracy
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent ?: return

        val accelerationX = sensorEvent.values.getOrNull(0)?.toDouble()
        val accelerationY = sensorEvent.values.getOrNull(1)?.toDouble()
        val accelerationZ = sensorEvent.values.getOrNull(2)?.toDouble()

        if (accelerationX != null && accelerationY != null && accelerationZ != null) {
            val magnitude = sqrt(accelerationX * accelerationX + accelerationY * accelerationY + accelerationZ * accelerationZ)
            val magnitudeDelta = magnitude - previousMagnitude
            previousMagnitude = magnitude

            if (magnitudeDelta > Settings.magnitudeDeltaThreshold) {
                val currentDate = add(1, previousStepDate, sensorEvent.timestamp, normalizedAccuracy)
                previousStepDate = currentDate

                log(method, 1, currentDate, normalizedAccuracy, "magnitude delta: $magnitudeDelta")
            }
        }
    }
}