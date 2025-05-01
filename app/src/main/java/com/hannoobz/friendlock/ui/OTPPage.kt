package com.hannoobz.friendlock.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hannoobz.friendlock.utils.TOTP
import com.hannoobz.friendlock.utils.generateRandomSecret
import kotlinx.coroutines.delay
import androidx.core.content.edit
import androidx.navigation.NavController

@Composable
fun OTPPage(context: Context, navController: NavController) {
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

    val theirSecret = remember { mutableStateOf(prefs.getString("their_secret", null)) }
    val inputSecret = remember { mutableStateOf("") }
    val otpCode = remember { mutableStateOf("") }
    val isOurSecret = remember { mutableStateOf(false) }
    val isInputEmpty = remember { mutableStateOf(false) }
    val timeStep = 30
    val clipboardManager = LocalClipboardManager.current

    val remainingTimeVisual = remember { mutableStateOf(1f) }


    fun validateSecret() {
        isOurSecret.value = inputSecret.value == ourSecret
        isInputEmpty.value = inputSecret.value.isNullOrEmpty()
    }

    LaunchedEffect(Unit) {
        while (true) {
            val now = System.currentTimeMillis()
            val stepStart = now - (now % (timeStep * 1000))
            val elapsed = now - stepStart
            val fraction = elapsed.toFloat() / (timeStep * 1000)
            remainingTimeVisual.value = 1f - fraction
            delay(10)
        }
    }

    LaunchedEffect(theirSecret.value) {
        if (theirSecret.value != null) {
            while (true) {
                otpCode.value = TOTP.generate(theirSecret.value!!)
                delay(1000)
            }
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Control Access for Your Friend",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        if (theirSecret.value == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Initialize Friend's ID",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                        )

                    OutlinedTextField(
                        value = inputSecret.value,
                        onValueChange = {
                            inputSecret.value = it
                            validateSecret() },
                        label = { Text("Enter Your Friend's User ID") },
                        modifier = Modifier.padding(top = 16.dp),
                        supportingText = {
                            if (isOurSecret.value) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "You cannot use your own ID",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            else if (isInputEmpty.value){
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "User ID is empty!",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                    )

                    Button(
                        onClick = {
                            validateSecret()
                            if (inputSecret.value.isNotEmpty() && !isOurSecret.value) {
                                prefs.edit { putString("their_secret", inputSecret.value) }
                                theirSecret.value = inputSecret.value
                            }
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Save ID", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 72.dp)
                    ,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "OTP for ${theirSecret.value}",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = otpCode.value,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                                .clickable {
                            clipboardManager.setText(AnnotatedString(otpCode.value))
                            Toast.makeText(context, "OTP copied!", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { remainingTimeVisual.value },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = ProgressIndicatorDefaults.linearTrackColor
                    )
                }
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Button(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        onClick = {
                            prefs.edit { putString("their_secret", null) }
                            theirSecret.value = null
                        }
                    ) {
                        Text("Reset friend's ID", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

    }
}
