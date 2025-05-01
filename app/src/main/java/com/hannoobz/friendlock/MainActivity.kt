package com.hannoobz.friendlock

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hannoobz.friendlock.ui.HomeScreen
import com.hannoobz.friendlock.ui.theme.FriendLockTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hannoobz.friendlock.data.AppEntity
import com.hannoobz.friendlock.ui.AppList
import com.hannoobz.friendlock.ui.BlockOverlay
import com.hannoobz.friendlock.ui.OTPPage
import com.hannoobz.friendlock.ui.RequestPermissionsScreen
import com.hannoobz.friendlock.ui.viewmodels.AppListViewModel
import com.hannoobz.friendlock.utils.generateRandomSecret

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FriendLockTheme(darkTheme = true, dynamicColor = false) {
                val view = window.decorView
                val darkIcons = false
                val backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = 0.4f).toArgb()
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
                    MainApp()
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AppList : Screen("app_list")
    data object Request : Screen("request")
    data object OTPPage: Screen("otp_page")
    data object BlockOverlay: Screen("block")
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val navController: NavHostController = rememberNavController()
    val listState = rememberLazyListState()
    val viewModel: AppListViewModel = viewModel()
    val apps = viewModel.appList.collectAsState()
    val top5apps = remember {
        mutableStateOf<List<AppEntity>>(emptyList())
    }
    
    LaunchedEffect(apps.value) {
        if (apps.value.isNotEmpty() && top5apps.value.isEmpty()) {
            top5apps.value = apps.value
                .sortedByDescending { it.timeUsedMs }
                .take(5)
        }
    }

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
    val iconsLoaded = remember { mutableStateOf(false) }
    LaunchedEffect(apps.value) {
        if (apps.value.isNotEmpty()) {
            apps.value.forEach { item ->
                viewModel.preloadIcons(context, listOf(item.packageName))
            }
            iconsLoaded.value = true
        }
    }
    LaunchedEffect(iconsLoaded.value) {
        if (iconsLoaded.value) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Request.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Request.route,
                enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(500)) },
                exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(500)) },
                popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(500)) },
                popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(500)) }
            ) {
                composable(Screen.Home.route) {
                    if (iconsLoaded.value) {
                        HomeScreen(
                            navController = navController,
                            viewModel = viewModel,
                            apps = top5apps.value,
                        )
                    } else {
                        CircularProgressIndicator()
                    }
                }
                composable(Screen.AppList.route) {
                    AppList(
                        navController = navController,
                        viewModel = viewModel,
                        apps = apps,
                        listState = listState
                    )
                }
                composable(Screen.BlockOverlay.route) {
                    BlockOverlay("You need a permission to manage the locked apps") {
                        navController.navigate(Screen.AppList.route) {
                            popUpTo(Screen.BlockOverlay.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
                composable(Screen.OTPPage.route) {
                    OTPPage(
                        context = context,
                        navController = navController
                    )
                }
                composable(Screen.Request.route) {
                    RequestPermissionsScreen(context) {
                        viewModel.loadApps(context)
                    }
                }
            }
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









@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FriendLockTheme(darkTheme = true) {
        MainApp()
    }
}