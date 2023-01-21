package org.ranapat.sensors.gps.example.services.stepper

import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import org.ranapat.instancefactory.Fi
import org.ranapat.sensors.gps.example.data.entity.StepSet
import java.util.*

class StepCounterEventListener : SensorEventListener, AccuracyCalculator, DataCollector, StepLogger {
    override var accuracy: Int = SensorManager.SENSOR_STATUS_NO_CONTACT
    override val multiplier: Double get() = 1.0

    override val method: String get() = PackageManager.FEATURE_SENSOR_STEP_COUNTER
    override val stepSet: ArrayList<StepSet> = arrayListOf()
    override val status: StepperStatus = Fi.get(StepperStatus::class.java)

    private var previousSteps: Int? = null
    private var previousStepDate: Date? = null

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        this.accuracy = accuracy
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent ?: return

        val steps = sensorEvent.values.getOrNull(0)?.toInt() ?: 0
        val stepsToRecord = if (previousSteps == null) {
            1
        } else {
            steps - (previousSteps ?: 0)
        }
        previousSteps = steps

        val currentDate = add(stepsToRecord, previousStepDate, sensorEvent.timestamp, normalizedAccuracy)
        previousStepDate = currentDate

        log(method, stepsToRecord, currentDate, normalizedAccuracy, "raw steps: $steps")
    }
}