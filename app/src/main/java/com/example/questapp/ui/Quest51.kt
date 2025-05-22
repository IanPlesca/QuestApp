package com.example.questapp.ui

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

val quizQuestions = listOf(
    QuizQuestion("În ce an a fost fondată Universitatea de Stat din Moldova?", listOf("1939", "1946", "1955"), 1),
    QuizQuestion("Ce formă are turnul situat pe strada Alexei Mateevici, parte a USM?", listOf("Hexagonală (6 laturi)", "Octogonală (8 laturi)", "Circulară"), 1),
    QuizQuestion("Ce funcție a avut inițial clădirea turnului înainte de a fi asociată cu USM?", listOf("Turn de apă", "Observator astronomic", "Clădire administrativă"), 0),
    QuizQuestion("Ce facultate a fost printre primele create la USM în 1946?", listOf("Facultatea de Drept", "Facultatea de Istorie și Filologie", "Facultatea de Medicină"), 1),
    QuizQuestion("Ce eveniment important legat de USM a avut loc în 2017?", listOf("Deschiderea unei capsule a timpului", "Inaugurarea unui nou campus", "Primirea premiului Nobel de către un profesor"), 0),
    QuizQuestion("Ce disciplină nu făcea parte din curricula inițială a USM?", listOf("Fizica", "Economia", "Biologia"), 1),
    QuizQuestion("În ce an a fost turnul integrat oficial în patrimoniul cultural al Chișinăului?", listOf("1963", "1980", "1990"), 0),
    QuizQuestion("Ce număr de studenți a avut USM în primul an de activitate?", listOf("150", "320", "500"), 1)
)

@SuppressLint("MissingPermission")
@Composable
fun Quest51Screen(navController: NavController, gameProgressViewModel: GameProgressViewModel = viewModel()) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val questId = "quest51"
    val questState by gameProgressViewModel.questProgress.collectAsState()
    val state = questState[questId] ?: error("Quest necunoscut")
    val totalScore by gameProgressViewModel.totalScore.collectAsState()

    val showQuestions = state.arrived && !state.answered
    val showResults = state.answered

    var internalShowQuestions by remember { mutableStateOf(showQuestions) }
    var internalShowResults by remember { mutableStateOf(showResults) }

    var locationErrorMessage by remember { mutableStateOf("") }
    val selectedAnswers = remember { mutableStateListOf<Int?>(*arrayOfNulls(quizQuestions.size)) }
    var submitted by remember { mutableStateOf(false) }

    // Coordonate pentru USM (rotunjite la 4 zecimale)
    val targetLatitude = 47.0196
    val targetLongitude = 28.8223
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
                            gameProgressViewModel.setLastCompletedQuest("quest51")
                            navController.navigate("map")
                        },
                        icon = { Icon(Icons.Default.Map, "Hartă") },
                        label = { Text("Hartă") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navController.navigate("quest61")
                        },
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
            state = lazyListState
        ) {
            item {
                if (!internalShowQuestions && !internalShowResults) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            "🎓 Questul 5: Istoria USM 🎓",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "„Ridicate pe strada Mitropolitului și la intersecția cu autorul poeziei despre limba noastră, vei observa ceva înalt.”",
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
                                            "Quest51Location",
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Răspunde la următoarele întrebări despre USM și turnul său:",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        quizQuestions.forEachIndexed { index, question ->
                            QuizQuestionItem(
                                question = question,
                                selectedAnswer = selectedAnswers[index],
                                onAnswerSelected = { selectedAnswers[index] = it },
                                enabled = !submitted
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Button(
                            onClick = {
                                if (selectedAnswers.all { it != null }) {
                                    submitted = true
                                    val points = calculatePoints(selectedAnswers)
                                    internalShowResults = true
                                    gameProgressViewModel.markAnswered(questId, points)
                                }
                            },
                            enabled = !submitted,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Trimite răspunsurile", color = Color.White, fontSize = 20.sp)
                        }
                    }
                } else if (internalShowResults) {
                    val points = calculatePoints(selectedAnswers)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Rezultatele tale:",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                        Text(
                            "Puncte obținute: $points / 8",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            "Punctaj total: $totalScore",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    "Știai că?",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF6A1B9A)
                                )
                                Text(
                                    "Pe 17 mai 2017, la USM a fost deschisă o capsulă a timpului plasată în 1967, cu ocazia aniversării a 50 de ani de la fondarea universității. În capsulă s-au găsit scrisori, fotografii, ziare și obiecte din epoca sovietică, inclusiv mesaje ale studenților de atunci adresate generațiilor viitoare. Acestea reflectau speranțele și viziunea lor despre viitorul educației și al Moldovei.",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (internalShowResults) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Răspunsuri corecte:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    quizQuestions.forEachIndexed { index, question ->
                        val isCorrect = selectedAnswers[index] == question.correctAnswerIndex
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                question.question,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Răspunsul tău: ${question.options[selectedAnswers[index] ?: 0]}",
                                fontSize = 14.sp,
                                color = if (isCorrect) Color(0xFF006400) else Color.Red,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Răspuns corect: ${question.options[question.correctAnswerIndex]}",
                                fontSize = 14.sp,
                                color = Color(0xFF006400),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun QuizQuestionItem(
    question: QuizQuestion,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Text(
            question.question,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        question.options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled) { onAnswerSelected(index) }
                    .padding(4.dp)
            ) {
                RadioButton(
                    selected = selectedAnswer == index,
                    onClick = { onAnswerSelected(index) },
                    enabled = enabled
                )
                Text(
                    text = option,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

fun calculatePoints(selectedAnswers: List<Int?>): Int {
    return selectedAnswers.mapIndexed { index, answer ->
        if (answer == quizQuestions[index].correctAnswerIndex) 1 else 0
    }.sum()
}