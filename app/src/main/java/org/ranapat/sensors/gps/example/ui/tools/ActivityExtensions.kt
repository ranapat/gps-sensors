package org.ranapat.sensors.gps.example.ui.tools

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.ranapat.sensors.gps.example.R
import org.ranapat.sensors.gps.example.ui.common.IntentParameters

fun AppCompatActivity.navigate(nextActivity: Class<out AppCompatActivity>, bundle: Bundle = Bundle.EMPTY) {
    val launchIntent = Intent()

    launchIntent.putExtras(bundle)
    launchIntent.setClass(this, nextActivity)

    startActivity(launchIntent)

    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
}

fun AppCompatActivity.redirect(nextActivity: Class<out AppCompatActivity>, bundle: Bundle = Bundle.EMPTY) {
    val launchIntent = Intent()

    launchIntent.putExtras(bundle)
    launchIntent.setClass(this, nextActivity)

    startActivity(launchIntent)

    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

    finish()
}

fun AppCompatActivity.cleanRedirect(nextActivity: Class<out AppCompatActivity>) {
    val launchIntent = Intent()

    launchIntent.setClass(this, nextActivity)
    launchIntent.addFlags(IntentParameters.CLEAR_BACK_STACK)

    startActivity(launchIntent)

    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

    finish()
}