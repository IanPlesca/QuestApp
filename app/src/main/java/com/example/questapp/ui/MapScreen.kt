package com.example.questapp.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.questapp.R
import com.example.questapp.viewmodel.GameProgressViewModel
import com.google.rpc.Help

@Composable
fun MapScreen(navController: NavController, viewModel: GameProgressViewModel) {
    val lastCompletedQuest by viewModel.lastCompletedQuest.collectAsState()
    val totalScore by viewModel.totalScore.collectAsState()

    val mapImage = getMapImageResource(lastCompletedQuest)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = mapImage),
            contentDescription = "Hartă",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Punctaj total acumulat: $totalScore",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            BottomNavigationBarForMap(navController, viewModel)
        }
    }
}

// Această funcție returnează imaginea corectă după ultimul quest terminat
private fun getMapImageResource(lastCompletedQuest: String): Int {
    return when (lastCompletedQuest) {
        "quest11" -> R.drawable.map_checkpoint1
        "quest21" -> R.drawable.map_checkpoint2
        "quest31" -> R.drawable.map_checkpoint3
        "quest41" -> R.drawable.map_checkpoint4
        "quest51" -> R.drawable.map_checkpoint5
        "quest61" -> R.drawable.map_checkpoint6
        //"quest71" -> R.drawable.map_checkpoint7
        //"quest81" -> R.drawable.map_checkpoint8
        else -> R.drawable.map_checkpoint1
    }
}


// Bara de navigare corectată:
@Composable
fun BottomNavigationBarForMap(navController: NavController, viewModel: GameProgressViewModel) {
    val lastCompletedQuest by viewModel.lastCompletedQuest.collectAsState()

    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(lastCompletedQuest) },
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Înapoi la Quest") },
            label = { Text("Înapoi la Quest") }
        )

    }
}

//asem 47.0310651,28.8380289
//praisola 45.402829,11.2831223
//casa 45.399228,11.3144589
//


