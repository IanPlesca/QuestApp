package com.example.questapp.ui

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import kotlinx.coroutines.launch

data class SongMatch(
    val verse: String,
    val correctSongTitle: String
)

val songMatches = listOf(
    SongMatch(
        "Le vom face rând pe rând pe toate\n" +
                "Dacă eu mai vreau şi tu mai vrei.", "Maluri de Prut"
    ),
    SongMatch(
        "Suntem în cuvânt şi-n toate\n" +
                "Floare de latinitate\n" +
                "Sub un cer cu stele sudice.", "Eminescu"
    ),
    SongMatch(
        "De-aș trăi vecia-ntreagă\n" +
                "O vecie mi-ai fi dragă", "Te iubesc"
    ),
    SongMatch(
        "Uite-o din nou trece pe drum fata zglobie.\n" +
                "Uite-un băiat floare şi-a pus la pălărie.", "Bucuraţi-vă"
    ),
    SongMatch(
        "Și la toți le-o dat cu carul\n" +
                "Numa' mie cu păharul", "Ș-așa-mi vine câteodată"
    )
)

val extraSongTitles = listOf(
    "Trei culori",
    "Suveranitatea",
    "Cosmos",
    "Bădiță",
    "Visele",
    "Dorul meu",
    "Cântă cucul",
    "Marea mea dragoste",
    "Frunză verde",
    "La mijloc de codru"
)

val allSongTitles = (songMatches.map { it.correctSongTitle } + extraSongTitles).shuffled()

@SuppressLint("MissingPermission")
@Composable
fun Quest61Screen(navController: NavController, gameProgressViewModel: GameProgressViewModel = viewModel()) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val questId = "quest61"
    val questState by gameProgressViewModel.questProgress.collectAsState()
    val state = questState[questId] ?: error("Quest necunoscut")
    val totalScore by gameProgressViewModel.totalScore.collectAsState()

    val showQuestions = state.arrived && !state.answered
    val showResults = state.answered

    var internalShowQuestions by remember { mutableStateOf(showQuestions) }
    var internalShowResults by remember { mutableStateOf(showResults) }
    var gamePoints by remember { mutableStateOf(state.points) }

    var locationErrorMessage by remember { mutableStateOf("") }

    // Coordonate pentru Parcul Valea Morilor (rotunjite la 4 zecimale)
    val targetLatitude = 47.0183
    val targetLongitude = 28.8218
    val radius = 100f // Rază de 100 metri

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(internalShowResults) {
        if (internalShowResults) {
            coroutineScope.launch {
                lazyListState.scrollToItem(0)
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (internalShowResults) {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            gameProgressViewModel.setLastCompletedQuest("quest61")
                            navController.navigate("map")
                        },
                        icon = { Icon(Icons.Default.Map, "Hartă") },
                        label = { Text("Hartă") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("quest71") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5DC))
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = lazyListState
        ) {
            item {
                if (!internalShowQuestions && !internalShowResults) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(top = 100.dp)
                    ) {
                        Text(
                            "🎵 Questul 6: Ion și Doina Aldea-Teodorovici 🎵",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF8B4513)
                        )
                        Text(
                            "Încearcă să înțelegi locația următoare după unele versuri ale unor cântece: 'versul1', 'versul2', 'versul3'.",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF8B4513)
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
                                            "Quest61Location",
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513))
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
                    }
                } else if (internalShowQuestions && !internalShowResults) {
                    SongMatchingGame(
                        onGameCompleted = { points ->
                            gamePoints = points
                            internalShowResults = true
                            gameProgressViewModel.markAnswered(questId, points)
                        },
                        onGameOver = { points ->
                            gamePoints = points
                            internalShowResults = true
                            gameProgressViewModel.markAnswered(questId, points)
                        }
                    )
                } else if (internalShowResults) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Rezultatele tale:",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF8B4513)
                        )
                        Text(
                            "Puncte obținute: $gamePoints / 10",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp),
                            color = Color(0xFF8B4513)
                        )
                        Text(
                            "Punctaj total: $totalScore",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                            color = Color(0xFF8B4513)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4B5))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "Știai că?",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF8B4513)
                                )
                                Text(
                                    "Statuia lui Ion și Doina Aldea-Teodorovici din Parcul Valea Morilor a fost dezvelită în 1997, la cinci ani după tragicul accident în care și-au pierdut viața. Cei doi artiști sunt considerați simboluri ale renașterii culturale și naționale a Moldovei, iar muzica lor continuă să inspire generații.",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(top = 8.dp),
                                    color = Color(0xFF8B4513)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SongMatchingGame(
    onGameCompleted: (Int) -> Unit,
    onGameOver: (Int) -> Unit
) {
    var attempts by remember { mutableStateOf(5) }
    var showError by remember { mutableStateOf(false) }
    val userSelections = remember { mutableStateListOf<String?>(*arrayOfNulls(songMatches.size)) }
    var isGameOver by remember { mutableStateOf(false) }
    var showResults by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Potrivește versurile cu titlurile cântecelor:",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp),
            color = Color(0xFF8B4513)
        )

        songMatches.forEachIndexed { index, songMatch ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = songMatch.verse,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    color = Color(0xFF8B4513)
                )

                var expanded by remember { mutableStateOf(false) }
                Box {
                    Button(
                        onClick = { expanded = true },
                        enabled = !showResults && attempts > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDEB887))
                    ) {
                        Text(
                            text = userSelections[index] ?: "Alege titlul",
                            fontSize = 14.sp,
                            color = Color(0xFF8B4513)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        allSongTitles.forEach { title ->
                            DropdownMenuItem(
                                text = { Text(title, color = Color(0xFF8B4513)) },
                                onClick = {
                                    userSelections[index] = title
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                val correctMatches = userSelections.mapIndexed { index, selectedTitle ->
                    selectedTitle == songMatches[index].correctSongTitle
                }.count { it }

                if (correctMatches == songMatches.size) {
                    showResults = true
                    onGameCompleted(correctMatches + attempts)
                } else {
                    attempts--
                    if (attempts <= 0) {
                        isGameOver = true
                        showResults = true
                        onGameOver(correctMatches)
                    } else {
                        showError = true
                    }
                }
            },
            enabled = attempts > 0 && userSelections.all { it != null } && !showResults,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513)),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Verifică răspunsurile", color = Color.White, fontSize = 20.sp)
        }

        Text(
            text = "Încercări rămase: $attempts",
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp),
            color = Color(0xFF8B4513)
        )

        if (showError && attempts > 0) {
            Text(
                "Greșit! Verifică răspunsurile.",
                color = Color.Red,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (showResults) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Rezultatele tale:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF8B4513)
            )
            songMatches.forEachIndexed { index, songMatch ->
                val isCorrect = userSelections[index] == songMatch.correctSongTitle
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        songMatch.verse,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF8B4513)
                    )
                    Text(
                        "Răspunsul tău: ${userSelections[index]}",
                        fontSize = 14.sp,
                        color = if (isCorrect) Color(0xFF006400) else Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Răspuns corect: ${songMatch.correctSongTitle}",
                        fontSize = 14.sp,
                        color = Color(0xFF006400),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}