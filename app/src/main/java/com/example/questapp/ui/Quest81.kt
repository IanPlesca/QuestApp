package com.example.questapp.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.questapp.viewmodel.GameProgressViewModel
import com.example.questapp.viewmodel.QuestState
import kotlinx.coroutines.launch

@Composable
fun Quest81Screen(navController: NavController, gameProgressViewModel: GameProgressViewModel = viewModel()) {
    val questId = "quest81"
    val questState by gameProgressViewModel.questProgress.collectAsState()
    val state = questState[questId] ?: QuestState()
    val totalScore by gameProgressViewModel.totalScore.collectAsState()

    var currentStep by remember { mutableStateOf(if (state.answered) 4 else 0) }
    var userAnswers by remember { mutableStateOf(gameProgressViewModel.getAnswersForQuest(questId).toMutableMap()) }
    // Folosim MutableState direct, fără delegare 'by', pentru a evita ambiguitatea
    val gamePoints = remember { mutableStateOf(state.points) }

    // Răspunsuri corecte
    val correctBinary = "11100001"
    val correctOctal = "341"
    val correctHexadecimal = "E1"

    // Variante de răspuns
    val binaryOptions = listOf("11001100", "10101010", "11100001", "11110000", "10000001", "11111111")
    val octalOptions = listOf("225", "413", "331", "341", "421", "351")
    val hexadecimalOptions = listOf("FF", "A5", "D2", "C3", "B4", "E1")

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentStep) {
        coroutineScope.launch {
            lazyListState.scrollToItem(0)
        }
    }

    Scaffold(
        bottomBar = {
            if (currentStep == 4) {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            gameProgressViewModel.setLastCompletedQuest("quest81")
                            navController.navigate("map")
                        },
                        icon = { Icon(Icons.Default.Map, "Hartă") },
                        label = { Text("Hartă") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate("SmileDetector") },
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
                when (currentStep) {
                    0 -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(top = 100.dp)
                        ) {
                            Text(
                                "🔢 Questul 8: Transformări de baze numerice 🔢",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF8B4513)
                            )
                            Text(
                                "Asigură-te că ești lângă scara de granit din Parcul Valea Morilor. Dacă alte echipe încă numără treptele, te rugăm să nu le deranjezi și să nu le spui răspunsul corect!",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp),
                                color = Color.Red
                            )
                            Button(
                                onClick = { currentStep = 1 },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B4513)),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text("Începe questul!", color = Color.White, fontSize = 20.sp)
                            }
                        }
                    }
                    1 -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Transformă 225 (numărul de trepte al scării de granit) din zecimal în binar. Alege răspunsul din cele propuse.",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF8B4513)
                            )
                            Text(
                                "Ce înseamnă un număr într-o anumită bază?\n" +
                                        "Un sistem numeric de bază definește câte cifre sunt folosite. În baza 10 (zecimală), folosim cifrele 0-9, iar fiecare poziție este o putere a lui 10. În baza 2 (binară), folosim doar 0 și 1, iar fiecare poziție este o putere a lui 2.\n\n" +
                                        "Cum transformăm din zecimal în binar?\n" +
                                        "Împărțim succesiv la 2 și notăm resturile (0 sau 1). Resturile formează numărul binar, citit de jos în sus. De exemplu, pentru 27:\n" +
                                        "27 ÷ 2 = 13, rest 1\n" +
                                        "13 ÷ 2 = 6, rest 1\n" +
                                        "6 ÷ 2 = 3, rest 0\n" +
                                        "3 ÷ 2 = 1, rest 1\n" +
                                        "1 ÷ 2 = 0, rest 1\n" +
                                        "Rezultatul este 11011.",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp),
                                color = Color(0xFF8B4513)
                            )
                            binaryOptions.forEach { option ->
                                Button(
                                    onClick = {
                                        userAnswers[1] = option
                                        gameProgressViewModel.saveAnswer(questId, 1, option)
                                        if (option == correctBinary) {
                                            gamePoints.value = gamePoints.value + 5
                                        }
                                        currentStep = 2
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDEB887)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(option, color = Color(0xFF8B4513), fontSize = 16.sp)
                                }
                            }
                        }
                    }
                    2 -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Transformă 225 (numărul de trepte al scării de granit) din zecimal în octal. Alege răspunsul din cele propuse.",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF8B4513)
                            )
                            Text(
                                "Ce înseamnă un număr în baza 8 (octală)?\n" +
                                        "În baza 8, folosim cifrele 0-7, iar fiecare poziție este o putere a lui 8.\n\n" +
                                        "Cum transformăm din zecimal în octal?\n" +
                                        "Împărțim succesiv la 8 și notăm resturile (0-7). Resturile formează numărul octal, citit de jos în sus. De exemplu, pentru 27:\n" +
                                        "27 ÷ 8 = 3, rest 3\n" +
                                        "3 ÷ 8 = 0, rest 3\n" +
                                        "Rezultatul este 33.",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp),
                                color = Color(0xFF8B4513)
                            )
                            octalOptions.forEach { option ->
                                Button(
                                    onClick = {
                                        userAnswers[2] = option
                                        gameProgressViewModel.saveAnswer(questId, 2, option)
                                        if (option == correctOctal) {
                                            gamePoints.value = gamePoints.value + 5
                                        }
                                        currentStep = 3
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDEB887)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(option, color = Color(0xFF8B4513), fontSize = 16.sp)
                                }
                            }
                        }
                    }
                    3 -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Transformă 225 (numărul de trepte al scării de granit) din zecimal în hexadecimal. Alege răspunsul din cele propuse.",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,

                                color = Color(0xFF8B4513)
                            )
                            Text(
                                "Ce înseamnă un număr în baza 16 (hexadecimală)?\n" +
                                        "În baza 16, folosim cifrele 0-9 și literele A-F, unde A=10, B=11, C=12, D=13, E=14, F=15. Fiecare poziție este o putere a lui 16.\n\n" +
                                        "Cum transformăm din zecimal în hexadecimal?\n" +
                                        "Împărțim succesiv la 16 și notăm resturile (0-15). Resturile de la 10 la 15 sunt: 10=A, 11=B, 12=C, 13=D, 14=E, 15=F. De exemplu, pentru 27:\n" +
                                        "27 ÷ 16 = 1, rest 11 (11 = B)\n" +
                                        "1 ÷ 16 = 0, rest 1\n" +
                                        "Rezultatul este 1B.",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp),
                                color = Color(0xFF8B4513)
                            )
                            hexadecimalOptions.forEach { option ->
                                Button(
                                    onClick = {
                                        userAnswers[3] = option
                                        gameProgressViewModel.saveAnswer(questId, 3, option)
                                        if (option == correctHexadecimal) {
                                            gamePoints.value = gamePoints.value + 5
                                        }
                                        gameProgressViewModel.markAnswered(questId, gamePoints.value)
                                        currentStep = 4
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDEB887)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Text(option, color = Color(0xFF8B4513), fontSize = 16.sp)
                                }
                            }
                        }
                    }
                    4 -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                "Rezultatele tale:",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF8B4513)
                            )
                            Text(
                                "225 în binar:",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 16.dp),
                                color = Color(0xFF8B4513)
                            )
                            Text(
                                "Răspunsul tău: ${userAnswers[1] ?: "N/A"}",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = if (userAnswers[1] == correctBinary) Color(0xFF006400) else Color.Red
                            )
                            Text(
                                "Răspuns corect: $correctBinary",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF006400)
                            )

                            Text(
                                "225 în octal:",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 16.dp),
                                color = Color(0xFF8B4513)
                            )
                            Text(
                                "Răspunsul tău: ${userAnswers[2] ?: "N/A"}",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = if (userAnswers[2] == correctOctal) Color(0xFF006400) else Color.Red
                            )
                            Text(
                                "Răspuns corect: $correctOctal",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF006400)
                            )

                            Text(
                                "225 în hexadecimal:",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 16.dp),
                                color = Color(0xFF8B4513)
                            )
                            Text(
                                "Răspunsul tău: ${userAnswers[3] ?: "N/A"}",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = if (userAnswers[3] == correctHexadecimal) Color(0xFF006400) else Color.Red
                            )
                            Text(
                                "Răspuns corect: $correctHexadecimal",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF006400)
                            )

                            Text(
                                "Puncte obținute: ${gamePoints.value} / 15",
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 16.dp),
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
                                        "Transformarea numerelor între baze este o abilitate fundamentală în informatică. Baza binară (2) este folosită în calculatoare, baza octală (8) și hexadecimală (16) sunt utilizate pentru a representa numerele într-un format mai compact.",
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
}