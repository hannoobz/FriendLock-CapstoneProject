package com.hannoobz.friendlock.ui

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hannoobz.friendlock.Screen
import com.hannoobz.friendlock.data.AppEntity
import com.hannoobz.friendlock.ui.components.AppItem
import com.hannoobz.friendlock.ui.viewmodels.AppListViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AppListViewModel,
    apps: List<AppEntity>
    ) {
    val orientation = LocalConfiguration.current.orientation
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Welcome to FriendLock 👋",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lock apps - Friends hold the key",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
            if(!isLandscape){
               Text(
                   text = "Top 5 Most Used Apps",
                   style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                   color = MaterialTheme.colorScheme.onBackground,
                   modifier = Modifier.padding(top = 24.dp)
               )
                Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if(apps.isNotEmpty()){
                    LazyColumn{
                        items(apps.size, key = { index -> apps[index].packageName }) { index ->
                            val app = apps[index]
                            Log.d("NAME","NAME IS ${app.packageName}")
                            AppItem(
                                app = app,
                                icon = viewModel.getAppIcon(app.packageName),
                                onCheckedChange = { },
                                isCheckBoxDisplayed = false
                            )
                        }
                    }
                }
                else{
                    Text("No apps found.")
                }
            }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Screen.BlockOverlay.route) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Text(
                        text = "Manage Locked Apps",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                OutlinedButton(
                    onClick = { navController.navigate(Screen.OTPPage.route) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.5.dp
                    )
                ) {
                    Text(
                        text = "Unlock for Friend",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

//@Preview
//@Composable
//fun HomeScreenPreview() {
//    FriendLockTheme(darkTheme = true) {
//        HomeScreen(navController = NavController(LocalContext.current))
//    }
//}
