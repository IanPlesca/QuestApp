package com.example.questapp.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.questapp.viewmodel.GameProgressViewModel
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Quest71Screen(navController: NavController, gameProgressViewModel: GameProgressViewModel = viewModel()) {
    val questId = "quest71"
    val questState by gameProgressViewModel.questProgress.collectAsState()
    val state = questState[questId] ?: error("Quest necunoscut")
    val totalScore by gameProgressViewModel.totalScore.collectAsState()

    var showQuestion by remember { mutableStateOf(!state.answered) }
    var showResults by remember { mutableStateOf(state.answered) }
    var userAnswer by remember { mutableStateOf("") }
    var gamePoints by remember { mutableStateOf(0) }

    // Numărul corect de trepte
    val correctNumberOfSteps = 225 // 213 + 8 + 4

    // Add LazyListState to control scrolling
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to top when showResults changes to true
    LaunchedEffect(showResults) {
        if (showResults) {
            coroutineScope.launch {
                lazyListState.scrollToItem(0)
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showResults) {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            gameProgressViewModel.setLastCompletedQuest("quest71")
                            navController.navigate("map")
                        },
                        icon = { Icon(Icons.Default.Map, "Hartă") },
                        label = { Text("Hartă") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("quest81") },
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
                .background(Color(0xFFF5F5DC)) // Bej cald, păstrăm tematica
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = lazyListState
        ) {
            item {
                if (showQuestion) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(top = 100.dp)
                    ) {
                        Text(
                            "🪜 Questul 7: Scara de granit din Valea Morilor 🪜",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF8B4513)
                        )
                        Text(
                            "Câte trepte are scara de granit din Parcul Valea Morilor?",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF8B4513)
                        )
                        Text(
                            "Indiciu: Numără toate treptele scării de granit, de la nivelul statuii lui Ion și Doina Aldea-Teodorovici până la cel mai jos nivel, inclusiv cele 4 trepte de la baza scării.",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = Color(0xFF8B4513)
                        )
                        Text(
                            "Atenție: Ai o singură șansă de a trimite răspunsul! Dacă răspunsul tău este în intervalul ±10 față de numărul corect, vei primi 4 puncte în loc de 5. Dacă este în afara acestui interval, vei primi 0 puncte.",
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = Color.Red
                        )

                        TextField(
                            value = userAnswer,
                            onValueChange = { userAnswer = it },
                            label = { Text("Introdu numărul de trepte") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFFFE4B5),
                                unfocusedContainerColor = Color(0xFFFFE4B5),
                                focusedTextColor = Color(0xFF8B4513),
                                unfocusedTextColor = Color(0xFF8B4513),
                                focusedLabelColor = Color(0xFF8B4513),
                                unfocusedLabelColor = Color(0xFF8B4513)
                            )
                        )

                        Button(
                            onClick = {
                                val answer = userAnswer.toIntOrNull()
                                if (answer != null) {
                                    val points = when {
                                        answer == correctNumberOfSteps -> 5 // Răspuns corect
                                        answer in (correctNumberOfSteps - 10)..(correctNumberOfSteps + 10) -> 4 // În intervalul ±10
                                        else -> 0 // În afara intervalului
                                    }
                                    gamePoints = points
                                    gameProgressViewModel.markAnswered(questId, points)
                                    showQuestion = false
                                    showResults = true
                                }
                            },
                            enabled = userAnswer.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513)),
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Trimite răspunsul", color = Color.White, fontSize = 20.sp)
                        }
                    }
                } else if (showResults) {
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
                            "Răspunsul tău: $userAnswer",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp),
                            color = if (userAnswer.toIntOrNull() == correctNumberOfSteps) Color(0xFF006400) else Color.Red
                        )
                        Text(
                            "Răspuns corect: $correctNumberOfSteps",
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp),
                            color = Color(0xFF006400)
                        )
                        Text(
                            "Puncte obținute: $gamePoints / 5",
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
                                    "Prin urmare, dacă nu ați știut, de fiecare dată când mergeți la Valea Morilor coborâți/urcați 213 trepte, plus 8 trepte amplasate înainte și 4 după cea centrală.",
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