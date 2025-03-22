package com.weit2nd.deepfakedetector.data.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import javax.inject.Inject

class ActivityProvider @Inject constructor() : Application.ActivityLifecycleCallbacks {
    var currentActivity: Activity? = null
        private set

    fun start(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    fun stop(application: Application) {
        application.unregisterActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(
        activity: Activity,
        bundle: Bundle?,
    ) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(
        activity: Activity,
        bundle: Bundle,
    ) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
}
