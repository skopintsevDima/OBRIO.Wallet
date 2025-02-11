package ua.obrio.wallet.presentation

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import ua.obrio.wallet.presentation.ui.activity.CrashActivity
import kotlin.system.exitProcess

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        setupGlobalExceptionHandler()
    }

    private fun setupGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("GlobalException", "Uncaught exception on thread ${thread.name}: $throwable")

            val intent = CrashActivity.newIntent(this)
            startActivity(intent)

            exitProcess(1)
        }
    }
}