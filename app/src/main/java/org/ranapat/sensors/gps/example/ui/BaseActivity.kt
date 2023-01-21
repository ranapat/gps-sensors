package org.ranapat.sensors.gps.example.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.ranapat.instancefactory.Fi
import org.ranapat.sensors.gps.example.R
import org.ranapat.sensors.gps.example.managements.LocaleManager

abstract class BaseActivity : AppCompatActivity() {
    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }
    private val localeManager: LocaleManager by lazy { Fi.get(LocaleManager::class.java) }

    protected abstract val viewModelClass: Class<out BaseViewModel>
    protected open val viewModel: BaseViewModel by lazy { getViewModel(viewModelClass) }

    protected abstract val layoutResource: Int

    protected var processInitialization = true
    protected abstract fun initializeUi()
    protected abstract fun initializeListeners()

    protected open val overridePendingTransition
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layoutResource)

        if (processInitialization) {
            initialize()
            initializeUi()
            initializeListeners()
            initialized()
        }
    }

    override fun onPause() {
        super.onPause()

        if (overridePendingTransition) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        localeManager.setLocale(this)
    }

    protected open fun initialize() {
        //
    }

    protected open fun initialized() {
        //
    }

    protected fun <T : ViewModel?> getViewModel(modelClass: Class<T>): T {
        return ViewModelProvider(this).get(modelClass)
    }

    protected fun subscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

}