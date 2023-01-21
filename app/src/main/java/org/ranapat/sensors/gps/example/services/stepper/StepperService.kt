package org.ranapat.sensors.gps.example.services.stepper

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import org.ranapat.instancefactory.Fi
import org.ranapat.sensors.gps.example.aggregator.Aggregator
import org.ranapat.sensors.gps.example.aggregator.AggregatorReporter
import org.ranapat.sensors.gps.example.data.entity.Session
import org.ranapat.sensors.gps.example.logger.Logger
import org.ranapat.sensors.gps.example.tools.OnItemListener

class StepperService : JobService() {
    private var sensorManager: SensorManager? = null

    private var stepCounterSensor: Sensor? = null
    private var stepDetectorSensor: Sensor? = null
    private var stepAccelerometerSensor: Sensor? = null

    private var currentDataCollector: DataCollector? = null

    private val stepCounterEventListener: StepCounterEventListener by lazy { StepCounterEventListener() }
    private val stepDetectorEventListener: StepDetectorEventListener by lazy { StepDetectorEventListener() }
    private val stepAccelerometerEventListener: StepAccelerometerEventListener by lazy { StepAccelerometerEventListener() }

    private val aggregator: Aggregator by lazy { Fi.get(Aggregator::class.java) }
    private val stepperServiceStarter: StepperServiceStarter by lazy { Fi.get(StepperServiceStarter::class.java) }

    private val aggregatorReporter: AggregatorReporter = AggregatorReporter {
        currentDataCollector?.toStepSummary()
    }
    private val sessionAction: OnItemListener<Session.ActivityType> = OnItemListener<Session.ActivityType> {
        //
    }

    override fun onStartJob(jobParameters: JobParameters?): Boolean {
        return if (handleStartJob()) {
            stepperServiceStarter.setStarted()

            Logger.e("[ StepperService ] jobStarted")

            true
        } else {
            false
        }
    }

    override fun onStopJob(jobParameters: JobParameters?): Boolean {
        unregisterSensors()
        unregisterAggregator()

        stepperServiceStarter.setNotStarted()

        Logger.e("[ StepperService ] jobStopped")

        return true
    }

    override fun onDestroy() {
        unregisterSensors()
        unregisterAggregator()

        stepperServiceStarter.setNotStarted()

        Logger.e("[ StepperService ] onDestroy")

        super.onDestroy()
    }

    private fun handleStartJob(): Boolean {
        return registerSensors() && registerAggregator()
    }

    private fun registerSensors(): Boolean {
        unregisterSensors()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        Logger.e("[ StepperService ] registerSensors:"
                + " hasStepCounter: ${packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)};"
                + " hasStepDetector: ${packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)};"
                + " hasAccelerometer: ${packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)}."
            )

        when {
            packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER) -> {
                stepCounterSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                stepCounterSensor?.let {
                    sensorManager?.registerListener(stepCounterEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
                }

                currentDataCollector = stepCounterEventListener

                Logger.e("[ StepperService ] registerSensors: stepCounter registered")
            }
            packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR) -> {
                stepDetectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
                stepDetectorSensor?.let {
                    sensorManager?.registerListener(stepDetectorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
                }

                currentDataCollector = stepDetectorEventListener

                Logger.e("[ StepperService ] registerSensors: stepDetector registered")
            }
            packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER) -> {
                stepAccelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                stepAccelerometerSensor?.let {
                    sensorManager?.registerListener(stepAccelerometerEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
                }

                currentDataCollector = stepAccelerometerEventListener

                Logger.e("[ StepperService ] registerSensors: accelerometer registered")
            }
        }

        return true
    }

    private fun unregisterSensors() {
        Logger.e("[ StepperService ] unregisterSensors")

        stepCounterSensor?.let {
            sensorManager?.unregisterListener(stepCounterEventListener)
        }
        stepDetectorSensor?.let {
            sensorManager?.unregisterListener(stepDetectorEventListener)
        }
        stepAccelerometerSensor?.let {
            sensorManager?.unregisterListener(stepAccelerometerEventListener)
        }

        sensorManager = null

        stepCounterSensor = null
        stepDetectorSensor = null
        stepAccelerometerSensor = null

        currentDataCollector = null
    }

    private fun registerAggregator(): Boolean {
        aggregator.register(
            this,
            aggregatorReporter,
            sessionAction, sessionAction
        )

        return true
    }

    private fun unregisterAggregator(): Boolean {
        aggregator.unregister(this)

        return true
    }

}