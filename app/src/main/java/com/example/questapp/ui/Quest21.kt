package com.example.questapp.ui

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.questapp.viewmodel.GameProgressViewModel
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
@Composable
fun Quest21Screen(navController: NavController, gameProgressViewModel: GameProgressViewModel = viewModel()) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val questId = "quest21"
    val questState by gameProgressViewModel.questProgress.collectAsState()
    val state = questState[questId] ?: error("Quest necunoscut")
    val totalScore by gameProgressViewModel.totalScore.collectAsState()

    val showQuestions = state.arrived && !state.answered
    val showResults = state.answered

    var internalShowQuestions by remember { mutableStateOf(showQuestions) }
    var internalShowResults by remember { mutableStateOf(showResults) }
    var attempts by remember { mutableStateOf(5) }
    var score by remember { mutableStateOf(5) }
    var isGameOver by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    var locationErrorMessage by remember { mutableStateOf("") }

    // Coordonate țintă (rotunjite la 4 zecimale)
    val targetLatitude = 47.0274
    val targetLongitude = 28.8282
    val radius = 100f // Rază de 100 metri

    val events = listOf(
        "Înființarea Facultății de Economie și Administrarea Afacerilor",
        "Lansarea primului program de masterat în 'Economie și Management'",
        "Deschiderea noului campus ASEM pe strada Banulescu-Bodoni",
        "Lansarea programului de licență în limba engleză",
        "Transformarea Bibliotecii ASEM într-un centru de resurse digitale",
        "Lansarea platformei de învățământ online ASEM"
    )

    val correctOrder = events

    var eventMap by remember {
        mutableStateOf(events.shuffled().mapIndexed { index, event -> (index + 1) to event }.toMap())
    }

    var selectedBlock1 by remember { mutableStateOf<Int?>(null) }
    var selectedBlock2 by remember { mutableStateOf<Int?>(null) }

    fun swapBlocks(position1: Int, position2: Int) {
        val newMap = eventMap.toMutableMap()
        val event1 = newMap[position1]
        val event2 = newMap[position2]
        if (event1 != null && event2 != null) {
            newMap[position1] = event2
            newMap[position2] = event1
            eventMap = newMap
        }
    }

    Scaffold(
        bottomBar = {
            if (internalShowResults) {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            gameProgressViewModel.setLastCompletedQuest("quest21")
                            navController.navigate("map")
                        },
                        icon = { Icon(Icons.Default.Map, "Hartă") },
                        label = { Text("Hartă") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("quest31") },
                        icon = { Icon(Icons.Default.PlayArrow, "Quest nou") },
                        label = { Text("Următorul Quest") }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F4C3))
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!internalShowQuestions && !internalShowResults) {
                Text("📚 Questul 2 📚", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Unde merg studenții să învețe despre bani și economie?",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )

                Button(
                    onClick = {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                // Calculăm distanța
                                val distance = FloatArray(1)
                                Location.distanceBetween(
                                    location.latitude, location.longitude,
                                    targetLatitude, targetLongitude,
                                    distance
                                )

                                // Logăm informații pentru depanare
                                Log.d(
                                    "Quest21Location",
                                    "Locație curentă: Lat=${location.latitude}, Lng=${location.longitude}, Acuratețe: ${location.accuracy}, Distanță: ${distance[0]} metri"
                                )

                                if (distance[0] <= radius) {
                                    internalShowQuestions = true
                                    gameProgressViewModel.markArrived(questId)
                                    locationErrorMessage = ""
                                } else {
                                    locationErrorMessage = "Nu ești în raza corectă!\n" +
                                            "Locația ta: Lat=${String.format("%.4f", location.latitude)}, Lng=${String.format("%.4f", location.longitude)}\n" +
                                            "Locația necesară: Lat=$targetLatitude, Lng=$targetLongitude\n" +
                                            "Distanța rămasă: ${String.format("%.2f", distance[0])} metri"
                                }
                            } else {
                                locationErrorMessage = "Nu am putut obține locația ta!"
                            }
                        }.addOnFailureListener {
                            locationErrorMessage = "Eroare la locație: ${it.message}"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                ) {
                    Text("Începe questul!", color = Color.White, fontSize = 20.sp)
                }

                if (locationErrorMessage.isNotEmpty()) {
                    Text(
                        locationErrorMessage,
                        fontSize = 16.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 10.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else if (internalShowQuestions && !internalShowResults) {
                Text("🕑 Aranjează evenimentele cronologic:", fontSize = 20.sp)

                eventMap.toList().sortedBy { it.first }.forEach { (position, event) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                if (selectedBlock1 == null) selectedBlock1 = position
                                else if (selectedBlock2 == null) {
                                    selectedBlock2 = position
                                    swapBlocks(selectedBlock1!!, selectedBlock2!!)
                                    selectedBlock1 = null
                                    selectedBlock2 = null
                                }
                            },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(event, fontSize = 16.sp, modifier = Modifier.padding(16.dp))
                    }
                }

                Button(
                    onClick = {
                        if (attempts > 0 && !internalShowResults) {
                            val isCorrect = eventMap.toList().sortedBy { it.first }.map { it.second } == correctOrder
                            if (isCorrect) {
                                gameProgressViewModel.markAnswered(questId, score)
                                internalShowResults = true
                            } else {
                                attempts--
                                score--
                                showError = true
                                if (attempts == 0) {
                                    isGameOver = true
                                    internalShowResults = true
                                    gameProgressViewModel.markAnswered(questId, 0)
                                }
                            }
                        }
                    },
                    enabled = attempts > 0 && !internalShowResults,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                ) {
                    Text("Verifică ordinea", color = Color.White, fontSize = 20.sp)
                }

                if (showError && attempts > 0) {
                    Text(
                        "Ai greșit ordinea! Mai ai $attempts încercări.",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                } else if (isGameOver && attempts == 0) {
                    Text(
                        "Nu mai ai încercări disponibile!",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            } else if (internalShowResults) {
                Text(
                    if (isGameOver) "Joc terminat! Ai acumulat 0 puncte."
                    else "Felicitări! Ai acumulat $score puncte.",
                    fontSize = 26.sp
                )
                Text(
                    "Punctaj total acumulat: $totalScore",
                    fontSize = 20.sp
                )
                correctOrder.forEach {
                    Text(
                        it,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}