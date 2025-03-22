package com.weit2nd.deepfakedetector

import android.app.Application
import com.weit2nd.deepfakedetector.data.util.ActivityProvider
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HiltApplication : Application() {
    @Inject
    lateinit var activityProvider: ActivityProvider

    override fun onCreate() {
        super.onCreate()
        activityProvider.start(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        activityProvider.stop(this)
    }
}
