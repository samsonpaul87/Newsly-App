package com.newsly.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.sentry.android.core.SentryAndroid
import io.sentry.SentryLevel

/**
 * Application class for Newsly.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class NewslyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initSentry()
    }

    private fun initSentry() {
        SentryAndroid.init(this) { options ->
            // Set DSN from manifest meta-data (configured via local.properties)
            options.dsn = BuildConfig.SENTRY_DSN

            // Set environment
            options.environment = if (BuildConfig.DEBUG) "development" else "production"

            // Set release version
            options.release = "${BuildConfig.APPLICATION_ID}@${BuildConfig.VERSION_NAME}+${BuildConfig.VERSION_CODE}"

            // Enable debug mode in debug builds
            options.isDebug = BuildConfig.DEBUG

            // Set sample rates
            options.tracesSampleRate = if (BuildConfig.DEBUG) 1.0 else 0.2
            options.profilesSampleRate = if (BuildConfig.DEBUG) 1.0 else 0.1

            // Set diagnostic level
            options.setDiagnosticLevel(if (BuildConfig.DEBUG) SentryLevel.DEBUG else SentryLevel.ERROR)

            // Enable automatic breadcrumbs
            options.isEnableAutoSessionTracking = true
            options.sessionTrackingIntervalMillis = 30000

            // Attach screenshots on errors (optional)
            options.isAttachScreenshot = true

            // Attach view hierarchy on errors
            options.isAttachViewHierarchy = true
        }
    }
}
