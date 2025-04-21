package com.hannoobz.friendlock

import android.icu.text.CaseMap.Title
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hannoobz.friendlock.ui.theme.FriendLockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FriendLockTheme {
                    MainMenu(
                        modifier = Modifier
                    )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenu( modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val title = remember { mutableStateOf("FriendLock") }
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = modifier
                    .background(Color.Blue)
                    .padding(
                        top = 36.dp,
                        bottom = 36.dp
                    ),
                title = {
                    Text(
                        text = title.value,
                        color = Color(0xFF037E7D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 42.sp
                    )
                }
            )
        }
    )
    { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ){
            composable(route = "home"){
                HomeScreen()
            }
            composable(route = "block_app"){

            }
        }
    }
}


@Composable
fun HomeScreen(){
    LazyColumn {
        item {
            Text(
                text = "Something"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FriendLockTheme {
        MainMenu()
    }
}