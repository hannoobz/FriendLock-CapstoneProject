package com.hannoobz.friendlock

import android.content.Context
import android.os.Bundle
import android.provider.Settings
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
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hannoobz.friendlock.ui.AppList
import com.hannoobz.friendlock.ui.RequestPermissionsScreen
import com.hannoobz.friendlock.ui.hasUsageStatsPermission
import com.hannoobz.friendlock.ui.isAccessibilityServiceEnabled
import com.hannoobz.friendlock.ui.viewmodels.AppListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FriendLockTheme(darkTheme = true, dynamicColor = false) {
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
                    MainApp()
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AppList : Screen("app_list")
    data object Settings : Screen("settings")
    data object Request : Screen("request")
}

@Composable
fun MainApp() {
    val context = LocalContext.current

    val navController: NavHostController = rememberNavController()
    val listState = rememberLazyListState()
    val viewModel: AppListViewModel = viewModel()
    val apps = viewModel.appList.collectAsState()

    for (item in apps.value) {
        viewModel.preloadIcons(context, listOf(item.packageName))
    }
    LaunchedEffect(true) {
        viewModel.loadApps(context)
    }
    NavHost(
        navController = navController,
        startDestination = Screen.Request.route,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(500)) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(500)) },
        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(500)) },
        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(500)) }
        ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.AppList.route) {
            AppList(
                navController = navController,
                viewModel = viewModel,
                apps = apps,
                listState = listState
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        composable(Screen.Request.route) {
            RequestPermissionsScreen(context) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Request.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
}


@Composable
fun SettingsScreen(navController: NavHostController) {
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FriendLockTheme(darkTheme = true) {
        MainApp()
    }
}