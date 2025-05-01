package com.hannoobz.friendlock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import com.hannoobz.friendlock.ui.BlockOverlay
import com.hannoobz.friendlock.ui.theme.FriendLockTheme
import com.hannoobz.friendlock.utils.generateRandomSecret

class BlockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("otp_prefs", Context.MODE_PRIVATE)
        val ourSecret = prefs.getString("our_secret", null) ?: run {
            val newSecret = generateRandomSecret()
            prefs.edit().putString("our_secret", newSecret).apply()
            newSecret
        }

        setContent {
            FriendLockTheme {
                val view = window.decorView
                val darkIcons = false
                val backgroundColor = MaterialTheme.colorScheme.scrim.toArgb()

                SideEffect {
                    window.statusBarColor = backgroundColor
                    window.navigationBarColor = backgroundColor
                    val controller = WindowInsetsControllerCompat(window, view)
                    controller.isAppearanceLightStatusBars = darkIcons
                    controller.isAppearanceLightNavigationBars = darkIcons
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim)
                        .safeDrawingPadding()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        BlockOverlay(
                            topMessage = "You have locked this app",
                            onUnblock = { finish() }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Your User ID: $ourSecret",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
        finish()
    }
}


