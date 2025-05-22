package com.example.questapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.questapp.viewmodel.GameProgressViewModel
import com.example.questapp.viewmodel.LocationInfo
import com.example.questapp.viewmodel.LocationViewModel

@SuppressLint("MissingPermission")
@Composable
fun Quest41Screen(
    navController: NavController,
    gameProgressViewModel: GameProgressViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel()
) {
    val context = LocalContext.current
    val questId = "quest41"
    val questState by gameProgressViewModel.questProgress.collectAsState()
    val state = questState[questId] ?: error("Quest necunoscut")
    val totalScore by gameProgressViewModel.totalScore.collectAsState()

    val showQuestion = state.arrived && !state.answered
    val showResults = state.answered

    var internalShowQuestion by remember { mutableStateOf(showQuestion) }
    var internalShowResults by remember { mutableStateOf(showResults) }

    var answer by remember { mutableStateOf("") }
    var attempts by remember { mutableStateOf(3) }
    var showError by remember { mutableStateOf(false) }
    var points by remember { mutableStateOf(0) }

    // Coordonate Quest 41
    val targetLatitude = 47.0255
    val targetLongitude = 28.8298
    val radius = 100f // Rază de 100 metri

    // Informații locație
    val locationInfo by locationViewModel.locationInfo.collectAsState()

    // Începe actualizările de locație
    LaunchedEffect(Unit) {
        locationViewModel.startLocationUpdates(targetLatitude, targetLongitude, radius)
    }

    Scaffold(
        bottomBar = {
            if (internalShowResults) {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            gameProgressViewModel.setLastCompletedQuest("quest41")
                            navController.navigate("map")
                        },
                        icon = { Icon(Icons.Default.Map, "Hartă") },
                        label = { Text("Hartă") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("ajutor") },
                        icon = { Icon(Icons.Default.Help, "Ajutor") },
                        label = { Text("Ajutor") }
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFD1C4E9))
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                if (!internalShowQuestion && !internalShowResults) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            "🏞️ Questul 4: Observator în Parc 🏞️",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Câte bănci sunt în parcul central?",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )

                        // Afișare informații locație
                        LocationInfoDisplay(locationInfo, targetLatitude, targetLongitude)

                        Button(
                            onClick = {
                                if (locationInfo.isInRange) {
                                    internalShowQuestion = true
                                    gameProgressViewModel.markArrived(questId)
                                }
                            },
                            enabled = locationInfo.isInRange,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                        ) {
                            Text("Începe questul!", color = Color.White, fontSize = 20.sp)
                        }
                    }
                } else if (internalShowQuestion && !internalShowResults) {
                    Text(
                        "Câte bănci sunt în parcul central? (aproximativ 44)",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    OutlinedTextField(
                        value = answer,
                        onValueChange = { answer = it },
                        label = { Text("Introdu numărul de bănci") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    Button(
                        onClick = {
                            val userAnswer = answer.toIntOrNull()
                            if (userAnswer != null) {
                                val correctAnswer = 44
                                val difference = kotlin.math.abs(userAnswer - correctAnswer)
                                points = when {
                                    difference == 0 -> 10
                                    difference <= 2 -> 8
                                    difference <= 5 -> 5
                                    else -> 0
                                }
                                if (points > 0 || attempts == 1) {
                                    internalShowResults = true
                                    gameProgressViewModel.markAnswered(questId, points)
                                } else {
                                    attempts--
                                    showError = true
                                }
                            } else {
                                showError = true
                            }
                        },
                        enabled = attempts > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
                    ) {
                        Text("Verifică răspunsul", color = Color.White, fontSize = 20.sp)
                    }

                    Text(
                        text = "Încercări rămase: $attempts",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    if (showError && attempts > 0) {
                        Text(
                            "Răspuns greșit! Încearcă din nou.",
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                    }
                } else if (internalShowResults) {
                    Text(
                        text = if (points > 0)
                            "Bravo! Ai obținut $points puncte."
                        else "Joc terminat! Nu ai obținut puncte.",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Punctaj total: $totalScore",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun LocationInfoDisplay(
    locationInfo: LocationInfo,
    targetLatitude: Double,
    targetLongitude: Double
) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (locationInfo.errorMessage != null) {
            Text(
                text = locationInfo.errorMessage,
                fontSize = 16.sp,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        } else if (locationInfo.currentLocation != null) {
            Text(
                text = "Locația ta: Lat=${String.format("%.4f", locationInfo.currentLocation.latitude)}, " +
                        "Lng=${String.format("%.4f", locationInfo.currentLocation.longitude)}",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Locația necesară: Lat=$targetLatitude, Lng=$targetLongitude",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Distanța rămasă: ${String.format("%.2f", locationInfo.distanceToTarget)} metri",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            if (locationInfo.isInRange) {
                Text(
                    text = "Ești în raza corectă!",
                    fontSize = 16.sp,
                    color = Color.Green,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}