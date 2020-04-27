package com.example.revolut

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * This class is needed for instrumentation tests. Please do not remove.
 */
class MyAndroidJUnitRunner : AndroidJUnitRunner() {

    /**
     * used to override Application for instrumentation tests
     */
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, Application::class.java.name, context)
    }
}
