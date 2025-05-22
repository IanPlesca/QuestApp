package com.example.questapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun WelcomeScreenUI(onNavigateNext: () -> Unit) {
    val context = LocalContext.current

    var hasLocationPermission by remember { mutableStateOf(false) }
    var hasCameraPermission by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasLocationPermission = granted }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        hasLocationPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        hasCameraPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    var showRules by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFB3E5FC), Color(0xFFE1F5FE))
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️ ATENȚIE MARE! ⚠️",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Text(
            text = "Fii extrem de atent la drum și la semafoare! Traversarea se face doar pe culoarea verde, respectând toate regulile de circulație! Siguranța este pe primul loc!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = {
                if (!hasLocationPermission) locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                if (!hasCameraPermission) cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Permite accesul la locație și cameră", color = Color.White)
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = { showRules = !showRules },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Regulile Jocului", color = Color.White)
        }

        AnimatedVisibility(visible = showRules, enter = fadeIn(), exit = fadeOut()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(10.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = " Cum se joacă?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0288D1),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Text(
                    text = "Echipele vor traversa pe străzile Chișinăului, vor folosi harta pentru a găsi checkpoint-urile ascunse și vor răspunde corect la întrebări pentru a acumula puncte!",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text("  Echipe și colaborare - Rămâneți împreună! Nu trimiteți spioni în alte echipe.", fontSize = 16.sp, color = Color.Black)
                Text("  Siguranța înainte de toate! Traversarea se face doar pe verde!", fontSize = 16.sp, color = Color.Black)
                Text("  Fair-play - Nu folosiți internetul pentru răspunsuri.", fontSize = 16.sp, color = Color.Black)
                Text("  Nu vă îndepărtați mai mult de 2 km de punctul inițial.", fontSize = 16.sp, color = Color.Black)
                Text("  Ajutor în caz de nevoie - Contactați numărul de telefon din aplicație.", fontSize = 16.sp, color = Color.Black)
                Text("  Cum câștigi? Echipa cu cele mai multe puncte, nu neapărat prima ajunsă!", fontSize = 16.sp, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = onNavigateNext,
            enabled = hasLocationPermission && hasCameraPermission,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (hasLocationPermission && hasCameraPermission) Color(0xFF00C853) else Color.Gray
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                "Începe aventura!",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
