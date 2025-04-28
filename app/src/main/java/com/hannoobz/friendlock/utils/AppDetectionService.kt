package com.hannoobz.friendlock.utils

import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager
import com.hannoobz.friendlock.BlockActivity
import com.hannoobz.friendlock.data.DBProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppDetectionService : AccessibilityService() {
    private var previousAppPackage: String? = null
    private val APP_BLOCKER_PACKAGE_NAME = "com.hannoobz.friendlock"
    private val SYSTEM_UI_NAME = "com.android.systemui"

    override fun onServiceConnected() {
        super.onServiceConnected()
        val channel = NotificationChannel(
            "accessibility_service_channel",
            "Accessibility Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        val notification = Notification.Builder(this, "accessibility_service_channel")
            .setContentTitle("")
            .setContentText("")
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val currentInputMethod = Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val currentAppPackage = event.packageName?.toString()
            if (currentAppPackage != null) {
                if (currentAppPackage != previousAppPackage) {
                    if ((currentAppPackage != APP_BLOCKER_PACKAGE_NAME) && (currentAppPackage!=SYSTEM_UI_NAME) && (!currentInputMethod.contains(currentAppPackage, ignoreCase = true))) {
                        previousAppPackage = currentAppPackage
                        checkAndBlockApp(currentAppPackage)
                    }
                }
            }
        }
    }

    override fun onInterrupt() {
    }

    private fun checkAndBlockApp(packageName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val appName = DBProvider.getDatabase(applicationContext)
                .appDao()
                .getAppByPackageName(packageName)
            if (appName != null && appName.isChecked) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1500)
                    showOverlay()
                }
            } else {
            }
        }
    }

    private fun showOverlay() {
        val intent = Intent(this, BlockActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
