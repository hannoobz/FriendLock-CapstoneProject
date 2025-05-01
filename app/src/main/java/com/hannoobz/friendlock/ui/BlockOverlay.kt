package com.hannoobz.friendlock.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.hannoobz.friendlock.utils.TOTP
import com.hannoobz.friendlock.utils.generateRandomSecret
import kotlinx.coroutines.delay

@Composable
fun BlockOverlay(
    topMessage: String,
    onUnblock: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences("otp_prefs", Context.MODE_PRIVATE)
    }
    val ourSecret = remember {
        prefs.getString("our_secret", null) ?: run {
            val newSecret = generateRandomSecret()
            prefs.edit { putString("our_secret", newSecret) }
            newSecret
        }
    }
    val correctOtp = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        if (ourSecret != null) {
            while (true) {
                correctOtp.value = TOTP.generate(ourSecret!!)
                delay(1000)
            }
        }
    }
    val inputOtp = remember { mutableStateOf("") }
    val showError = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(top = 10.dp)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = topMessage,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = inputOtp.value,
                onValueChange = {
                    inputOtp.value = it
                    showError.value = false
                },
                label = { Text("Enter OTP from your friend") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 16.dp),
                isError = showError.value,
                supportingText = {
                    if (showError.value) {
                        Text(
                            text = "Wrong OTP",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            )

            Button(
                onClick = {
                    if (inputOtp.value == correctOtp.value) {
                        onUnblock()
                    } else {
                        showError.value = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text("Unlock", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
