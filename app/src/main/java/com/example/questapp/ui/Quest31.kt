package com.example.questapp.ui

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.questapp.viewmodel.GameProgressViewModel
import com.google.android.gms.location.LocationServices

data class CrosswordQuestion(
    val question: String,
    val answer: String,
    val orientation: Orientation,
    val startRow: Int,
    val startCol: Int
)

enum class Orientation {
    HORIZONTAL, VERTICAL
}

val crosswordQuestions = listOf(
    CrosswordQuestion(
        question = "Compozitorul baletului „Lacul lebedelor”",
        answer = "CEAICOVSKI",
        orientation = Orientation.HORIZONTAL,
        startRow = 1,
        startCol = 0
    ),
    CrosswordQuestion(
        question = "Compozitorul operei „Flautul fermecat”",
        answer = "MOZART",
        orientation = Orientation.VERTICAL,
        startRow = 0,
        startCol = 5
    ),
    CrosswordQuestion(
        question = "Cel mai cunoscut balet rusesc",
        answer = "SPARTAC",
        orientation = Orientation.HORIZONTAL,
        startRow = 3,
        startCol = 0
    ),
    CrosswordQuestion(
        question = "Opera lui Verdi despre un bărbat cu o mască de fier",
        answer = "RIGOLETTO",
        orientation = Orientation.VERTICAL,
        startRow = 0,
        startCol = 9
    ),
    CrosswordQuestion(
        question = "Compozitorul operei „Carmen”",
        answer = "BIZET",
        orientation = Orientation.HORIZONTAL,
        startRow = 5,
        startCol = 1
    )
)

@SuppressLint("MissingPermission")
@Composable
fun Quest31Screen(navController: NavController, gameProgressViewModel: GameProgressViewModel = viewModel()) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val questId = "quest31"
    val questState by gameProgressViewModel.questProgress.collectAsState()
    val state = questState[questId] ?: error("Quest necunoscut")
    val totalScore by gameProgressViewModel.totalScore.collectAsState()

    val showQuestions = state.arrived && !state.answered
    val showResults = state.answered

    var internalShowQuestions by remember { mutableStateOf(showQuestions) }
    var internalShowResults by remember { mutableStateOf(showResults) }

    var attempts by remember { mutableStateOf(5) }
    var wordPoints by remember { mutableStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    var locationErrorMessage by remember { mutableStateOf("") }

    // Coordonate actualizate (rotunjite la 4 zecimale)
    val targetLatitude = 47.0274
    val targetLongitude = 28.8282
    val radius = 100f // Rază de 100 metri

    // Inițializăm grila de răspunsuri (10x10, toate pozițiile sunt null inițial)
    val userAnswers = remember {
        Array(10) { Array<Char?>(10) { null } }
    }

    // Inițializăm litera selectată
    var selectedLetter by remember { mutableStateOf<Char?>(null) }

    // Generăm lista de litere disponibile (toate literele din răspunsuri)
    val availableLetters = remember {
        crosswordQuestions.flatMap { it.answer.toList() }.distinct().shuffled()
    }

    Scaffold(
        bottomBar = {
            if (internalShowResults) {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            gameProgressViewModel.setLastCompletedQuest("quest31")
                            navController.navigate("map")
                        },
                        icon = { Icon(Icons.Default.Map, "Hartă") },
                        label = { Text("Hartă") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("quest41") },
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
                .background(Color(0xFFD1C4E9))
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                if (!internalShowQuestions && !internalShowResults) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            "🎭 Questul 3: Ghicitoare 🎭",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Unde muzica clasică și baletul se întâlnesc într-un palat al artei?",
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
                                            "Quest31Location",
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A))
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
                    Text("Completează crossword-ul despre operă:", fontSize = 20.sp)

                    crosswordQuestions.forEachIndexed { index, question ->
                        Text(
                            text = "${index + 1}. ${question.question}",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    CrosswordGrid(
                        questions = crosswordQuestions,
                        userAnswers = userAnswers,
                        selectedLetter = selectedLetter,
                        onCellClick = { row, col ->
                            if (selectedLetter != null) {
                                userAnswers[row][col] = selectedLetter
                                selectedLetter = null
                            }
                        }
                    )

                    AvailableLetters(
                        letters = availableLetters,
                        onLetterSelected = { letter ->
                            selectedLetter = letter
                        }
                    )

                    Button(
                        onClick = {
                            val (correctWords, allCorrect) = checkAnswers(userAnswers, crosswordQuestions)
                            wordPoints = correctWords
                            if (allCorrect) {
                                val attemptPoints = attempts
                                val totalPoints = wordPoints + attemptPoints
                                internalShowResults = true
                                gameProgressViewModel.markAnswered(questId, totalPoints)
                            } else {
                                attempts--
                                if (attempts <= 0) {
                                    isGameOver = true
                                    internalShowResults = true
                                    gameProgressViewModel.markAnswered(questId, wordPoints)
                                } else {
                                    showError = true
                                }
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
                        Text("Greșit! Verifică răspunsurile.", color = Color.Red, fontSize = 16.sp)
                    }
                } else if (internalShowResults) {
                    val finalScore = if (isGameOver) wordPoints else wordPoints + attempts
                    Text(
                        text = if (isGameOver)
                            "Joc terminat! Ai obținut $wordPoints puncte pentru cuvinte corecte."
                        else "Bravo! Ai obținut ${finalScore} puncte (${wordPoints} pentru cuvinte + ${attempts} pentru încercări rămase).",
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
fun CrosswordGrid(
    questions: List<CrosswordQuestion>,
    userAnswers: Array<Array<Char?>>,
    selectedLetter: Char?,
    onCellClick: (Int, Int) -> Unit
) {
    val gridSize = 10
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val cellSize = (screenWidthDp - 32.dp) / gridSize
    val dynamicCellSize = cellSize.coerceAtMost(24.dp).coerceAtLeast(20.dp)
    val fontSize = (dynamicCellSize.value * 0.6f).sp
    val numberFontSize = (dynamicCellSize.value * 0.3f).sp

    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White)
    ) {
        for (i in 0 until gridSize) {
            Row {
                for (j in 0 until gridSize) {
                    val cellValue = userAnswers[i][j]
                    val isPartOfQuestion = questions.any { question ->
                        when (question.orientation) {
                            Orientation.HORIZONTAL -> i == question.startRow && j in question.startCol until question.startCol + question.answer.length
                            Orientation.VERTICAL -> j == question.startCol && i in question.startRow until question.startRow + question.answer.length
                        }
                    }

                    val isStartOfWord = questions.any { question ->
                        when (question.orientation) {
                            Orientation.HORIZONTAL -> i == question.startRow && j == question.startCol
                            Orientation.VERTICAL -> i == question.startRow && j == question.startCol
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(dynamicCellSize)
                            .border(1.dp, Color.Black)
                            .clickable {
                                if (isPartOfQuestion && selectedLetter != null) {
                                    onCellClick(i, j)
                                }
                            }
                            .background(if (isPartOfQuestion) Color.LightGray else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isStartOfWord) {
                            Text(
                                text = "${questions.indexOfFirst { it.startRow == i && it.startCol == j } + 1}",
                                fontSize = numberFontSize,
                                color = Color.Black,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(2.dp)
                            )
                        }
                        Text(
                            text = cellValue?.toString() ?: "",
                            fontSize = fontSize,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AvailableLetters(
    letters: List<Char>,
    onLetterSelected: (Char) -> Unit
) {
    val rows = letters.chunked(9)
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val letterBoxSize = (screenWidthDp - 32.dp) / 9
    val dynamicLetterBoxSize = letterBoxSize.coerceAtMost(32.dp).coerceAtLeast(24.dp)
    val fontSize = (dynamicLetterBoxSize.value * 0.5f).sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        rows.forEach { rowLetters ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowLetters.forEach { letter ->
                    Box(
                        modifier = Modifier
                            .size(dynamicLetterBoxSize)
                            .background(Color.LightGray)
                            .clickable { onLetterSelected(letter) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letter.toString(),
                            fontSize = fontSize,
                            color = Color.Black
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

fun checkAnswers(
    userAnswers: Array<Array<Char?>>,
    questions: List<CrosswordQuestion>
): Pair<Int, Boolean> {
    var correctWords = 0

    val allCorrect = questions.all { question ->
        val answer = question.answer
        val isCorrect = when (question.orientation) {
            Orientation.HORIZONTAL -> {
                answer.indices.all { index ->
                    userAnswers[question.startRow][question.startCol + index] == answer[index]
                }
            }
            Orientation.VERTICAL -> {
                answer.indices.all { index ->
                    userAnswers[question.startRow + index][question.startCol] == answer[index]
                }
            }
        }
        if (isCorrect) correctWords++
        isCorrect
    }

    return Pair(correctWords, allCorrect)
}