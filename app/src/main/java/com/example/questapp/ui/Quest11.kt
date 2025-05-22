package com.example.questapp.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.questapp.viewmodel.GameProgressViewModel
import kotlin.random.Random

@Composable
fun Quest11Screen(navController: NavController, gameProgressViewModel: GameProgressViewModel = viewModel()) {
    val questId = "quest11"
    val questState by gameProgressViewModel.questProgress.collectAsState()
    val state = questState[questId] ?: error("Quest necunoscut")
    val totalScore by gameProgressViewModel.totalScore.collectAsState()
    val previousAnswers = gameProgressViewModel.getAnswersForQuest(questId)

    var showIntro by remember { mutableStateOf(!state.arrived && !state.answered) }
    var showQuestions by remember { mutableStateOf(state.arrived && !state.answered) }
    var showResults by remember { mutableStateOf(state.answered) }

    var currentQuestion by remember { mutableStateOf(0) }
    var selectedAnswers by remember { mutableStateOf(previousAnswers.toMutableMap()) }

    val questions = listOf(
        Question("De ce Moara Roșie are această culoare distinctivă?",
            listOf("Este construită din cărămidă roșie", "A fost incendiată", "Strategie împotriva hoților", "Vopsită periodic"), 0),
        Question("În ce an a fost construită Moara Roșie?",
            listOf("1850", "1901", "1789", "1925"), 0),
        Question("În ce an a fost inclusă Moara Roșie în Registrul Monumentelor?",
            listOf("1993", "2001", "1985", "2010"), 0),
        Question("Ce destinație a avut Moara Roșie după activitatea inițială?",
            listOf("Depozit de cereale", "Fabrică textilă", "Școală", "Centru cultural"), 0)
    )

    val shuffledQuestions = remember { questions.map { it.copy(answers = it.answers.shuffled(Random(System.currentTimeMillis()))) } }

    Scaffold(
        bottomBar = {
            NavigationBar {
                if (showResults) {
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            gameProgressViewModel.setLastCompletedQuest("quest11")
                            navController.navigate("map")
                        },
                        icon = { Icon(Icons.Default.Map, "Hartă") },
                        label = { Text("Hartă") }
                    )




                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("quest21") },
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
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (showIntro) {
                // **Introducere Quest**
                Text(
                    "🔴 Quest de început: Moara Roșie 🔴",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    "Prima ta provocare începe aici! Moara Roșie ascunde povești interesante despre trecutul său. Ești gata?",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    onClick = {
                        showIntro = false
                        showQuestions = true
                        gameProgressViewModel.markArrived(questId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Începe questul!", fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else if (showQuestions) {
                // **Întrebările Quest-ului**
                Text("Întrebarea ${currentQuestion + 1} din ${questions.size}", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                Text(shuffledQuestions[currentQuestion].text, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))

                shuffledQuestions[currentQuestion].answers.forEach { answer ->
                    Button(
                        onClick = {
                            gameProgressViewModel.saveAnswer(questId, currentQuestion, answer)
                            selectedAnswers[currentQuestion] = answer
                            if (currentQuestion == questions.lastIndex) {
                                val score = questions.indices.count { idx ->
                                    selectedAnswers[idx] == questions[idx].answers[questions[idx].correctIndex]
                                } * 5
                                gameProgressViewModel.markAnswered(questId, score)
                                showQuestions = false
                                showResults = true
                            } else currentQuestion++
                        },
                        modifier = Modifier.padding(4.dp).fillMaxWidth()
                    ) { Text(answer, color = Color.White, fontSize = 18.sp) }
                }
            } else if (showResults) {
                // **Rezultatele Quest-ului**
                Text("🎉 Felicitări! Ai acumulat $totalScore puncte! 🎉", fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))

                questions.forEachIndexed { index, question ->
                    val correctAnswer = question.answers[question.correctIndex]
                    val userAnswer = selectedAnswers[index] ?: "Niciun răspuns"
                    Text(
                        "${if (userAnswer == correctAnswer) "✔️" else "❌"} ${question.text}\nRăspunsul corect: $correctAnswer\nAi ales: $userAnswer",
                        color = if (userAnswer == correctAnswer) Color.Green else Color.Red,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

data class Question(val text: String, val answers: List<String>, val correctIndex: Int)