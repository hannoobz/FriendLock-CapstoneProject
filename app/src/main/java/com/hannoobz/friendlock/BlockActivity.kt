package com.hannoobz.friendlock

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowInsetsControllerCompat
import com.hannoobz.friendlock.ui.BlockOverlay
import com.hannoobz.friendlock.ui.theme.FriendLockTheme

class BlockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim)
                        .safeDrawingPadding()
                ) {
                    BlockOverlay(
                        topMessage = "You have locked this app",
                        onUnblock = {
                            finish()
                        }
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(homeIntent)
        finish()
    }
}

