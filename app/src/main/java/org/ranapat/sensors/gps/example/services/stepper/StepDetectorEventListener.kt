package org.ranapat.sensors.gps.example.services.stepper

import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import org.ranapat.instancefactory.Fi
import org.ranapat.sensors.gps.example.data.entity.StepSet
import java.util.*

class StepDetectorEventListener : SensorEventListener, AccuracyCalculator, DataCollector, StepLogger {
    override var accuracy: Int = SensorManager.SENSOR_STATUS_NO_CONTACT
    override val multiplier: Double get() = 0.66

    override val method: String get() = PackageManager.FEATURE_SENSOR_STEP_DETECTOR
    override val stepSet: ArrayList<StepSet> = arrayListOf()
    override val status: StepperStatus = Fi.get(StepperStatus::class.java)

    private var previousStepDate: Date? = null

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        this.accuracy = accuracy
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent ?: return

        val steps = sensorEvent.values.getOrNull(0)?.toInt() ?: 0

        val currentDate = add(steps, previousStepDate, sensorEvent.timestamp, normalizedAccuracy)
        previousStepDate = currentDate

        log(method, steps, currentDate, normalizedAccuracy)
    }
}