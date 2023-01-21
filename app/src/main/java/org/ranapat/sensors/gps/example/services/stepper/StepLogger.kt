package org.ranapat.sensors.gps.example.services.stepper

import org.ranapat.sensors.gps.example.logger.Logger
import java.text.SimpleDateFormat
import java.util.*

interface StepLogger {
    fun log(method: String, steps: Int, date: Date, accuracy: Double, extra: String? = null) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)

        Logger.e("[ StepLogger ] method: $method noted at $dateFormatter $steps step(s) with accuracy $accuracy. Extra notes: ${extra ?: "none"}")
    }
}