package org.ranapat.sensors.gps.example.ui.main

import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.tabs.TabLayout
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import org.ranapat.sensors.gps.example.R
import org.ranapat.sensors.gps.example.Settings
import org.ranapat.sensors.gps.example.data.entity.Session
import org.ranapat.sensors.gps.example.logger.Logger
import org.ranapat.sensors.gps.example.permissions.PermissionsChecker
import org.ranapat.sensors.gps.example.services.location.LocationServiceStarter
import org.ranapat.sensors.gps.example.services.stepper.StepperServiceStarter
import org.ranapat.sensors.gps.example.ui.BaseActivity
import org.ranapat.sensors.gps.example.ui.common.States.*
import org.ranapat.sensors.gps.example.ui.map.MapFragment
import org.ranapat.sensors.gps.example.ui.tools.*
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {
    private lateinit var nextActivity: Class<out AppCompatActivity>

    override val layoutResource: Int = R.layout.activity_main
    override val viewModelClass = MainViewModel::class.java
    override val viewModel: MainViewModel by lazy { super.viewModel as MainViewModel }

    override val overridePendingTransition
        get() = false

    private inner class UIObjects {
        val closeSession: TextView = findViewById(R.id.closeSession)
        val createSessionJogging: TextView = findViewById(R.id.createSessionJogging)
        val createSessionSprinting: TextView = findViewById(R.id.createSessionSprinting)
        val createSessionStanding: TextView = findViewById(R.id.createSessionStanding)
        val createSessionWalking: TextView = findViewById(R.id.createSessionWalking)
        val currentSessionGoals: TextView = findViewById(R.id.currentSessionGoals)
        val goalCountdown: TextView = findViewById(R.id.goalCountdown)
        val logs: TextView = findViewById(R.id.logs)
        val logsScrollView: ScrollView = findViewById(R.id.logsScrollView)
        val map: MapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment
        val showCurrentLocationProcessed: TextView = findViewById(R.id.showCurrentLocationProcessed)
        val showCurrentLocationRaw: TextView = findViewById(R.id.showCurrentLocationRaw)
        val statistics: TextView = findViewById(R.id.statistics)
        val statisticsScrollView: ScrollView = findViewById(R.id.statisticsScrollView)
        val status: TextView = findViewById(R.id.status)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val timer: TextView = findViewById(R.id.timer)
        val toolbarAccount: CircleImageView = findViewById(R.id.toolbarAccount)
        val toolbarRating: TextView = findViewById(R.id.toolbarRating)
    }
    private lateinit var ui: UIObjects

    private var currentSessionGoal: Int = 0
    private var statisticsLocalVisible: Boolean = false
    private var statisticsLocal: String = ""
    private var statisticsRemote: String = ""

    private var logsAggregatorVisible: Boolean = false
    private var logsRawVisible = false
    private var logsAggregator: String = ""
    private var logsRaw: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.initialize()
    }

    override fun onResume() {
        super.onResume()

        viewModel.onResume()
    }

    override fun onDestroy() {
        /*viewModel.stopStepperService()
        viewModel.stopLocationService()
        viewModel.stopAggregator()*/

        PermissionsChecker.dispose()

        super.onDestroy()
    }

    override fun initialize() {
        super.initialize()

        ui = UIObjects()
    }

    override fun initializeUi() {
        ui.toolbarAccount.isVisible = true
        ui.toolbarRating.isVisible = true

        ui.createSessionStanding.isVisible = true
        ui.createSessionWalking.isVisible = true
        ui.createSessionJogging.isVisible = true
        ui.createSessionSprinting.isVisible = true

        ui.closeSession.isVisible = false
    }

    override fun initializeListeners() {
        subscription(viewModel.state
            .filter { shallThrottle(it) }
            .throttleFirst(Settings.debounceNavigationInMilliseconds, TimeUnit.MILLISECONDS)
            .subscribeUiThread(this) { state ->
                when (state) {
                    NAVIGATE -> navigate(nextActivity)
                    CLEAN_REDIRECT -> cleanRedirect(nextActivity)
                }
            }
        )
        subscription(viewModel.state
            .filter { !shallThrottle(it) }
            .subscribeUiThread(this) { state ->
                ui.status.text = "state changed to @$state"

                if (state == READY) {
                    ensurePermissions()
                }
            }
        )
        subscription(viewModel.next
            .subscribeUiThread(this) {
                nextActivity = it
            }
        )
        subscription(viewModel.user
            .subscribeUiThread(this) { user ->
                ui.toolbarAccount.setImageResource(
                    when (user.id) {
                        1L -> R.drawable.user_1
                        2L -> R.drawable.user_2
                        3L -> R.drawable.user_3
                        4L -> R.drawable.user_4
                        5L -> R.drawable.user_5
                        else -> R.drawable.user_1
                    }
                )
                ui.toolbarRating.text = formatRating(user.rating)
            }
        )
        subscription(viewModel.session
            .subscribeUiThread(this) { hasSession ->
                ui.createSessionStanding.isEnabled = true
                ui.createSessionWalking.isEnabled = true
                ui.createSessionJogging.isEnabled = true
                ui.createSessionSprinting.isEnabled = true
                ui.closeSession.isEnabled = true

                ui.createSessionStanding.alpha = 1.0f
                ui.createSessionWalking.alpha = 1.0f
                ui.createSessionJogging.alpha = 1.0f
                ui.createSessionSprinting.alpha = 1.0f
                ui.closeSession.alpha = 1.0f

                ui.createSessionStanding.isVisible = !hasSession
                ui.createSessionWalking.isVisible = !hasSession
                ui.createSessionJogging.isVisible = !hasSession
                ui.createSessionSprinting.isVisible = !hasSession
                ui.closeSession.isVisible = hasSession
            }
        )
        subscription(viewModel.currentLocationRaw
            .subscribeUiThread(this) { currentLocation ->
                ui.map.currentLocationRaw(currentLocation)
            }
        )
        subscription(viewModel.currentLocationProcessed
            .subscribeUiThread(this) { currentLocation ->
                ui.map.currentLocationProcessed(currentLocation, true)
            }
        )
        subscription(viewModel.stepsSoFar
            .subscribeUiThread(this) { steps ->
                ui.status.text = "steps in current package: $steps"
            }
        )
        subscription(viewModel.countdown
            .subscribeUiThread(this) { seconds ->
                ui.timer.text = seconds.toString()
            }
        )
        subscription(viewModel.sessionCreated
            .subscribeUiThread(this) { session ->
                currentSessionGoal = 0

                ui.goalCountdown.isVisible = false

                ui.currentSessionGoals.isVisible = true

                ui.map.clearCurrentLocationRaw()
                ui.map.clearCurrentLocationProcessed()
            }
        )
        subscription(viewModel.sessionClosed
            .subscribeUiThread(this) { session ->
                currentSessionGoal = 0

                ui.goalCountdown.isVisible = false

                ui.currentSessionGoals.isVisible = false

                ui.map.clearCurrentLocationRaw()
                ui.map.clearCurrentLocationProcessed()
            }
        )
        subscription(viewModel.sessionStatisticsLocal
            .subscribeUiThread(this) { statistics ->
                statisticsLocal = String.format(
                    "type: %s;\n\n" +
                            "duration: %s;\n" +
                            "calories : %.2f kal;\n" +
                            "steps : %d;\n" +
                            "distance : %.0f m;\n" +
                            "\n\n" +
                            "suspicious : %.0f%% (%s)",
                    statistics.activityType.name,
                    formatSeconds(statistics.duration.interval),
                    statistics.calories,
                    statistics.steps,
                    statistics.distance,
                    statistics.suspicious * 100,
                    when {
                        statistics.suspicious < 0.15 -> "amazing!!!"
                        statistics.suspicious < 0.25 -> "great!"
                        statistics.suspicious < 0.35 -> "ok"
                        statistics.suspicious < 0.55 -> "not accurate"
                        statistics.suspicious < 0.75 -> "not ok"
                        else -> "bad"
                    }
                )

                updateGoalCountdown(statistics.calories)

                infoStatistics()
            }
        )
        subscription(viewModel.sessionStatisticsRemote
            .subscribeUiThread(this) { statistics ->
                statisticsRemote = statistics.toString(2)

                infoStatistics()
            }
        )
        subscription(Logger.log
            .subscribeUiThread(this) { log ->
                if (log.contains("Aggregator")) {
                    logsAggregator = appendLog(log, logsAggregator)
                }
                logsRaw = appendLog(log, logsRaw)

                infoLogs()
            }
        )

        ui.currentSessionGoals.setOnClickListener {
            showCurrentSessionGoalsPopup()
        }
        ui.createSessionStanding.setOnClickListener {
            viewModel.createSession(Session.ActivityType.Standing)

            ui.createSessionStanding.isEnabled = false
            ui.createSessionWalking.isEnabled = false
            ui.createSessionJogging.isEnabled = false
            ui.createSessionSprinting.isEnabled = false

            ui.createSessionStanding.alpha = 0.4f
            ui.createSessionWalking.alpha = 0.4f
            ui.createSessionJogging.alpha = 0.4f
            ui.createSessionSprinting.alpha = 0.4f
        }
        ui.createSessionWalking.setOnClickListener {
            viewModel.createSession(Session.ActivityType.Walking)

            ui.createSessionStanding.isEnabled = false
            ui.createSessionWalking.isEnabled = false
            ui.createSessionJogging.isEnabled = false
            ui.createSessionSprinting.isEnabled = false

            ui.createSessionStanding.alpha = 0.4f
            ui.createSessionWalking.alpha = 0.4f
            ui.createSessionJogging.alpha = 0.4f
            ui.createSessionSprinting.alpha = 0.4f
        }
        ui.createSessionJogging.setOnClickListener {
            viewModel.createSession(Session.ActivityType.Jogging)

            ui.createSessionStanding.isEnabled = false
            ui.createSessionWalking.isEnabled = false
            ui.createSessionJogging.isEnabled = false
            ui.createSessionSprinting.isEnabled = false

            ui.createSessionStanding.alpha = 0.4f
            ui.createSessionWalking.alpha = 0.4f
            ui.createSessionJogging.alpha = 0.4f
            ui.createSessionSprinting.alpha = 0.4f
        }
        ui.createSessionSprinting.setOnClickListener {
            viewModel.createSession(Session.ActivityType.Sprinting)

            ui.createSessionStanding.isEnabled = false
            ui.createSessionWalking.isEnabled = false
            ui.createSessionJogging.isEnabled = false
            ui.createSessionSprinting.isEnabled = false

            ui.createSessionStanding.alpha = 0.4f
            ui.createSessionWalking.alpha = 0.4f
            ui.createSessionJogging.alpha = 0.4f
            ui.createSessionSprinting.alpha = 0.4f
        }
        ui.closeSession.setOnClickListener {
            viewModel.closeSession()

            ui.closeSession.isEnabled = false
            ui.closeSession.alpha = 0.4f
        }
        ui.toolbarAccount.setOnClickListener {
            //
        }
        ui.showCurrentLocationRaw.setOnClickListener {
            ui.map.currentLocationRawVisible = !ui.map.currentLocationRawVisible
            ui.showCurrentLocationRaw.alpha = if (ui.map.currentLocationRawVisible) 1.0f else 0.5f
        }
        ui.showCurrentLocationProcessed.setOnClickListener {
            ui.map.currentLocationProcessedVisible = !ui.map.currentLocationProcessedVisible
            ui.showCurrentLocationProcessed.alpha = if (ui.map.currentLocationProcessedVisible) 1.0f else 0.5f
        }
        ui.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                statisticsLocalVisible = tab?.position == 0

                logsAggregatorVisible = tab?.position == 1
                logsRawVisible = tab?.position == 2

                ui.statisticsScrollView.isVisible = tab?.position == 0
                ui.logsScrollView.isVisible = tab?.position == 1 || tab?.position == 2

                infoStatistics()
                infoLogs()
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })
    }

    private fun ensurePermissions() {
        PermissionsChecker.initialize(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            subscription(PermissionsChecker
                .request(StepperServiceStarter.PERMISSION, LocationServiceStarter.PERMISSION_A, LocationServiceStarter.PERMISSION_B)
                .subscribe({ isGranted ->
                    if (!isGranted) {
                        ui.status.text = "Permissions are not granted"
                    } else {
                        ui.status.text = "Permissions is granted"
                    }

                    ensureLocationEnabled()
                }, { _ ->
                    //
                })
            )
        } else {
            subscription(PermissionsChecker
                .request(LocationServiceStarter.PERMISSION_A, LocationServiceStarter.PERMISSION_B)
                .subscribe({ isGranted ->
                    if (!isGranted) {
                        ui.status.text = "Permissions are not granted"
                    } else {
                        ui.status.text = "Permissions is granted"
                    }

                    ensureLocationEnabled()
                }, {
                    //
                })
            )
        }
    }

    private fun ensureLocationEnabled() {
        try {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            val gpsProviderEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
            val networkProviderEnabled = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true

            if (!gpsProviderEnabled && !networkProviderEnabled){
                showLocationShallBeEnabled()
            } else {
                startServices()
            }
        } catch (e: SecurityException) {
            //
        } catch (e: Exception) {
            //
        }
    }

    private fun startServices() {
        subscription(Maybe.just(true)
            .subscribeOn(Schedulers.computation())
            .subscribe({
                viewModel.startStepperService()
                viewModel.startLocationService()
            }, {
                //
            })
        )
    }

    private fun infoStatistics() {
        infoStatistics(statisticsLocal)
    }

    private fun infoStatistics(message: String) {
        ui.statistics.text = message

        ui.statisticsScrollView.post {
            ui.statisticsScrollView.fullScroll(View.FOCUS_UP)
        }
    }

    private fun appendLog(message: String, container: String): String {
        val current = container.substring(0, container.length.coerceAtMost(1 * 10 * 1000))
        return "$message\n$current ..."
    }

    private fun infoLogs() {
        if (logsAggregatorVisible) {
            infoLogs(logsAggregator)
        } else if (logsRawVisible) {
            infoLogs(logsRaw)
        }
    }

    private fun infoLogs(message: String) {
        ui.logs.text = message
    }

    private fun updateGoalCountdown(calories: Float) {
        val percentage = calories / currentSessionGoal

        when {
            percentage < 0.1 -> ui.goalCountdown.setBackgroundResource(R.drawable.shape_circle_red)
            percentage < 0.2 -> ui.goalCountdown.setBackgroundResource(R.drawable.shape_circle_carrot_orange)
            percentage < 0.3 -> ui.goalCountdown.setBackgroundResource(R.drawable.shape_circle_light_tan)
            percentage < 0.7 -> ui.goalCountdown.setBackgroundResource(R.drawable.shape_circle_blue)
            percentage < 0.8 -> ui.goalCountdown.setBackgroundResource(R.drawable.shape_circle_light_green)
            else -> ui.goalCountdown.setBackgroundResource(R.drawable.shape_circle_dark_green)
        }

        ui.goalCountdown.text = "${(100 * 1f.coerceAtMost(percentage)).toInt().toString()} %"
    }

    private fun showCurrentSessionGoalsPopup() {
        val builder = AlertDialog.Builder(this@MainActivity)

        builder.setCancelable(false)
        builder.setIcon(R.drawable.ic_alert_orange_24dp)
        builder.setTitle(getString(R.string.current_session_goals_title))
        val dialogView = layoutInflater.inflate(R.layout.dialog_current_session_goals, null)
        builder.setView(dialogView)
        val confirmEditText = dialogView.findViewById<EditText>(R.id.confirmEditText)
        dialogView.findViewById<TextView>(R.id.descriptionTextView).text = getString(R.string.current_session_goals_body)

        builder.setPositiveButton(getString(R.string.current_session_goals_ok)) { _, _ ->
            try {
                val goal = confirmEditText.text.toString().toInt()

                currentSessionGoal = goal
                ui.goalCountdown.isVisible = true
                ui.goalCountdown.setBackgroundResource(R.drawable.shape_circle_red)
                updateGoalCountdown(0.0f)

                Toast.makeText(applicationContext, getString(R.string.current_session_goals_set, goal), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                //
            }
        }
        builder.setNegativeButton(getString(R.string.current_session_goals_cancel)) { _, _ ->
            //
        }
        builder.setCancelable(true)

        val dialog: AlertDialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.warning_alert_background)
        dialog.show()
    }

    private fun showLocationShallBeEnabled() {
        val builder = AlertDialog.Builder(this@MainActivity)

        builder.setIcon(R.drawable.ic_alert_orange_24dp)
        builder.setTitle(getString(R.string.location_shall_be_enabled_title))
        builder.setMessage(getString(R.string.location_shall_be_enabled_body))
        builder.setPositiveButton(getString(R.string.location_shall_be_enabled_ok)) { _, _ ->
            ensureLocationEnabled()
        }
        builder.setNegativeButton(getString(R.string.location_shall_be_enabled_cancel)) { _, _ ->
            finish()
        }
        builder.setCancelable(false)

        val dialog: AlertDialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.warning_alert_background)
        dialog.show()
    }

}