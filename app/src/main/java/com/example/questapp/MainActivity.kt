package com.example.questapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.questapp.ui.MapScreen
import com.example.questapp.ui.Quest11Screen
import com.example.questapp.ui.Quest21Screen
import com.example.questapp.ui.WelcomeScreenUI
import com.example.questapp.viewmodel.GameProgressViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.questapp.ui.Quest31Screen
import com.example.questapp.ui.Quest41Screen
import com.example.questapp.ui.Quest51Screen
import com.example.questapp.ui.Quest61Screen
import com.example.questapp.ui.Quest71Screen
import com.example.questapp.ui.Quest81Screen
import com.example.questapp.ui.SmileDetectorScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuestApp()
        }
    }
}

@Composable
fun QuestApp(gameProgressViewModel: GameProgressViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") { WelcomeScreenUI { navController.navigate("quest11") } }

        composable("quest11") { Quest11Screen(navController, gameProgressViewModel) }
        composable("quest21") { Quest21Screen(navController, gameProgressViewModel) }
        composable("quest31") { Quest31Screen(navController, gameProgressViewModel) }
        composable("quest41") { Quest41Screen(navController, gameProgressViewModel) }
        composable("quest51") { Quest51Screen(navController, gameProgressViewModel) }
        composable("quest61") { Quest61Screen(navController, gameProgressViewModel) }
        composable("quest71") { Quest71Screen(navController, gameProgressViewModel) }
        composable("quest81") { Quest81Screen(navController,gameProgressViewModel) }
        composable("SmileDetector") { SmileDetectorScreen(navController,gameProgressViewModel) }
        composable("map") { MapScreen(navController, gameProgressViewModel) }
    }
}