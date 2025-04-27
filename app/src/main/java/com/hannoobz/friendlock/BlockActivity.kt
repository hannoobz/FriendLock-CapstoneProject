package com.hannoobz.friendlock

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.hannoobz.friendlock.ui.BlockOverlay
import com.hannoobz.friendlock.ui.theme.FriendLockTheme

class BlockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FriendLockTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    BlockOverlay(
                        onUnblock = {
                            finish()
                        }
                    )
                }
            }
        }
    }
}