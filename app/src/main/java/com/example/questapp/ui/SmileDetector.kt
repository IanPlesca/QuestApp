package com.example.questapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.questapp.viewmodel.GameProgressViewModel
import com.example.questapp.viewmodel.QuestState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@Composable
fun SmileDetectorScreen(
    navController: NavController,
    gameProgressViewModel: GameProgressViewModel
) {
    val questId = "smileDetector"
    val questState by gameProgressViewModel.questProgress.collectAsState()
    val state = questState[questId] ?: QuestState()
    val totalScore by gameProgressViewModel.totalScore.collectAsState()

    // Starea pentru permisiunea camerei
    val cameraPermissionState = remember { mutableStateOf(false) }
    // Starea pentru zâmbet
    val isSmiling = remember { mutableStateOf("Necunoscut") }
    // Starea pentru punctaj
    val gamePoints = remember { mutableStateOf(state.points) }
    // Starea pentru a urmări dacă utilizatorul a zâmbit cel puțin o dată
    val hasSmiled = remember { mutableStateOf(false) }
    // Starea pentru pasul curent (0 = detectare, 1 = rezultate)
    var currentStep by remember { mutableStateOf(if (state.answered) 1 else 0) }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Executor pentru procesarea imaginilor
    val executor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    // Launcher pentru a solicita permisiunea camerei
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            cameraPermissionState.value = granted
        }
    )

    // Verifică și solicită permisiunea camerei
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionState.value = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentStep) {
        coroutineScope.launch {
            lazyListState.scrollToItem(0)
        }
    }

    Scaffold(
        bottomBar = {
            if (currentStep == 1) {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            gameProgressViewModel.setLastCompletedQuest("smileDetector")
                            navController.navigate("map") // Va fi MapScreen1
                        },
                        icon = { Icon(Icons.Default.Map, "Hartă") },
                        label = { Text("Hartă") }
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
                                .padding(top = 50.dp)
                        ) {
                            Text(
                                "😊 Quest Special: Detectează Zâmbetul 😊",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF8B4513)
                            )
                            Text(
                                "Zâmbește la cameră pentru a câștiga 1 punct!",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp),
                                color = Color(0xFF8B4513)
                            )

                            if (cameraPermissionState.value) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)
                                ) {
                                    CameraPreview(
                                        onImageCaptured = { imageProxy ->
                                            val image = InputImage.fromMediaImage(
                                                imageProxy.image!!,
                                                imageProxy.imageInfo.rotationDegrees
                                            )

                                            val options = FaceDetectorOptions.Builder()
                                                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                                                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                                                .build()

                                            val detector = FaceDetection.getClient(options)
                                            detector.process(image)
                                                .addOnSuccessListener { faces ->
                                                    for (face in faces) {
                                                        val smileProb = face.smilingProbability
                                                        if (smileProb != null) {
                                                            isSmiling.value = if (smileProb > 0.5) "Zâmbești!" else "Nu zâmbești"
                                                            if (smileProb > 0.5 && !hasSmiled.value) {
                                                                hasSmiled.value = true
                                                                gamePoints.value = 1 // +1 punct dacă zâmbești
                                                                gameProgressViewModel.markAnswered(questId, gamePoints.value)
                                                                currentStep = 1 // Treci automat la rezultate
                                                            }
                                                        }
                                                    }
                                                    imageProxy.close()
                                                }
                                                .addOnFailureListener { e ->
                                                    e.printStackTrace()
                                                    isSmiling.value = "Eroare"
                                                    imageProxy.close()
                                                }
                                        },
                                        executor = executor
                                    )

                                    Text(
                                        text = "Zâmbet: ${isSmiling.value}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp)
                                            .background(Color.White.copy(alpha = 0.7f)),
                                        textAlign = TextAlign.Center,
                                        color = Color(0xFF8B4513)
                                    )
                                }
                            } else {
                                Text(
                                    text = "Permisiunea pentru cameră este necesară!",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    color = Color.Red
                                )
                            }
                        }
                    }
                    1 -> {
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
                                "Ai zâmbit?",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 16.dp),
                                color = Color(0xFF8B4513)
                            )
                            Text(
                                if (hasSmiled.value) "Da!" else "Nu",
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = if (hasSmiled.value) Color(0xFF006400) else Color.Red
                            )

                            Text(
                                "Puncte obținute: ${gamePoints.value} / 1",
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
                                        "Zâmbetul este un limbaj universal! Studiile arată că zâmbetul nu doar că îmbunătățește starea ta de spirit, dar îi face și pe ceilalți să se simtă mai bine.",
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

    // Oprește executorul când pagina este distrusă
    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
        }
    }
}

@Composable
fun CameraPreview(
    onImageCaptured: (androidx.camera.core.ImageProxy) -> Unit,
    executor: ExecutorService
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Configurează previzualizarea
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // Configurează analiza imaginilor
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(executor) { imageProxy ->
                            onImageCaptured(imageProxy)
                        }
                    }

                // Folosește camera frontală
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}