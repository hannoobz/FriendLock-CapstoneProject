package com.hannoobz.friendlock.ui

import android.content.Context
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hannoobz.friendlock.utils.hasUsageStatsPermission
import com.hannoobz.friendlock.utils.isAccessibilityServiceEnabled
import com.hannoobz.friendlock.utils.openAccessibilitySettings
import com.hannoobz.friendlock.utils.openOverlayPermissionSettings
import com.hannoobz.friendlock.utils.openUsageAccessSettings

@Composable
fun RequestPermissionsScreen(
    context: Context,
    onPermissionsGranted: () -> Unit
) {
    val hasUsageStatsPermission = remember { mutableStateOf(false) }
    val isAccessibilityServiceEnabled = remember { mutableStateOf(false) }
    val canDrawOverlays = remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val buttonText = remember { mutableStateOf("") }

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
        if (!hasUsageStatsPermission.value) {
            buttonText.value = "Grant Usage Access"
        } else if (!isAccessibilityServiceEnabled.value) {
            buttonText.value = "Turn on Accessibility Access"
        } else if (!canDrawOverlays.value) {
            buttonText.value = "Allow Display Over Apps"
        }
        if (hasUsageStatsPermission.value && isAccessibilityServiceEnabled.value && canDrawOverlays.value) {
            onPermissionsGranted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
        ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "We need the following permissions to proceed:",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        PermissionRow(
            permissionText = "1. Usage Stats Permission",
            isGranted = hasUsageStatsPermission.value
        )
        PermissionRow(
            permissionText = "2. Accessibility Service",
            isGranted = isAccessibilityServiceEnabled.value
        )
        PermissionRow(
            permissionText = "3. Display Over Apps Permission",
            isGranted = canDrawOverlays.value
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                when {
                    !hasUsageStatsPermission.value -> openUsageAccessSettings(context)
                    !isAccessibilityServiceEnabled.value -> openAccessibilitySettings(context)
                    !canDrawOverlays.value -> openOverlayPermissionSettings(context)
                }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(buttonText.value, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun PermissionRow(
    permissionText: String,
    isGranted: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = permissionText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        if (isGranted) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Granted",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
