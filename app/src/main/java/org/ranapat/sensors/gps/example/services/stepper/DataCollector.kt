package org.ranapat.sensors.gps.example.services.stepper

import android.os.SystemClock
import org.ranapat.sensors.gps.example.data.entity.StepSet
import org.ranapat.sensors.gps.example.data.entity.StepSummary
import org.ranapat.sensors.gps.example.data.entity.StepSummary.METHOD_UNDEFINED
import java.util.*

interface DataCollector {
    val method: String
    val stepSet: ArrayList<StepSet>
    val status: StepperStatus

    fun add(steps: Int, from: Date?, to: Long, accuracy: Double): Date {
        val toDate = toDate(to)

        stepSet.add(StepSet(
            steps, from, toDate, method, accuracy
        ))

        status()

        return toDate
    }

    fun toStepSummary(to: Date = Date()): StepSummary {
        var steps: Int = 0
        var combinedAccuracy: Double = 0.0
        var method: String = METHOD_UNDEFINED
        var suspicious: Float = 0.0f

        while (stepSet.size > 0 && to > stepSet[0].end) {
            val first: StepSet = stepSet.removeFirst()

            steps += first.steps
            combinedAccuracy += first.steps * first.accuracy
            method = first.method
        }

        if (steps == 0) {
            suspicious = 1.0f
        }

        val accuracy: Double = if (steps > 0) { combinedAccuracy / steps } else 0.0

        status()

        return StepSummary(steps, method, accuracy, suspicious)
    }

    private fun toDate(time: Long): Date {
        val lastDeviceBootTimeInMillis = System.currentTimeMillis() - SystemClock.elapsedRealtime()
        val sensorEventTimeInMillis = time / 1000_000

        return Date(lastDeviceBootTimeInMillis + sensorEventTimeInMillis)
    }

    private fun status() {
        status.stepsSoFar.onNext(stepSet.sumOf { it.steps })
    }
}