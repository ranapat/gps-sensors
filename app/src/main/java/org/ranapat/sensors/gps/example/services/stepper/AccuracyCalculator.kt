package org.ranapat.sensors.gps.example.services.stepper

import android.hardware.SensorManager

interface AccuracyCalculator {
    var accuracy: Int
    val multiplier: Double

    val normalizedAccuracy: Double get() = multiplier * when (accuracy) {
        SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> 1.0
        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> 0.66
        SensorManager.SENSOR_STATUS_ACCURACY_LOW -> 0.33
        SensorManager.SENSOR_STATUS_UNRELIABLE -> 0.10
        SensorManager.SENSOR_STATUS_NO_CONTACT -> 0.0
        else -> 0.0
    }
}