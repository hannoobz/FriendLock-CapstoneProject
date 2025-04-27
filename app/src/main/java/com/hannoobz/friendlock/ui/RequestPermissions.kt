package com.hannoobz.friendlock.ui

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hannoobz.friendlock.utils.AppDetectionService
import java.util.Locale

@Composable
fun RequestPermissionsScreen(
    context: Context,
    onPermissionsGranted: () -> Unit
) {
    val hasUsageStatsPermission = remember { mutableStateOf(false) }
    val isAccessibilityServiceEnabled = remember { mutableStateOf(false) }
    val canDrawOverlays = remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasUsageStatsPermission.value = hasUsageStatsPermission(context)
                isAccessibilityServiceEnabled.value = isAccessibilityServiceEnabled(context)
                canDrawOverlays.value = Settings.canDrawOverlays(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(
        hasUsageStatsPermission.value,
        isAccessibilityServiceEnabled.value,
        canDrawOverlays.value
    ) {
        if (hasUsageStatsPermission.value && isAccessibilityServiceEnabled.value && canDrawOverlays.value) {
            onPermissionsGranted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "We need the following permissions to proceed:",
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "1. Usage Stats Permission", color = Color.White)
        Text(text = "2. Accessibility Service", color = Color.White)
        Text(text = "3. Display Over Apps Permission", color = Color.White)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                when {
                    !hasUsageStatsPermission.value -> openUsageAccessSettings(context)
                    !isAccessibilityServiceEnabled.value -> openAccessibilitySettings(context)
                    !canDrawOverlays.value -> openOverlayPermissionSettings(context)
                }
            }
        ) {
            Text("Grant Permissions")
        }
    }
}


fun hasUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as android.app.AppOpsManager
    val mode = appOps.checkOpNoThrow(
        android.app.AppOpsManager.OPSTR_GET_USAGE_STATS,
        android.os.Process.myUid(),
        context.packageName
    )
    return mode == android.app.AppOpsManager.MODE_ALLOWED
}

fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val componentName = ComponentName(context, AppDetectionService::class.java)
    return accessibilityManager
        .getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        .any {
           it.resolveInfo.serviceInfo.name == componentName.className
        }
}

fun openUsageAccessSettings(context: Context) {
    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}

fun openAccessibilitySettings(context: Context) {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}

fun openOverlayPermissionSettings(context: Context) {
    if ("xiaomi" == Build.MANUFACTURER.lowercase(Locale.ROOT)) {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.setClassName("com.miui.securitycenter",
            "com.miui.permcenter.permissions.PermissionsEditorActivity")
        intent.putExtra("extra_pkgname", context.packageName)
        context.startActivity(intent)
    } else {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        context.startActivity(intent)
    }
}
