package com.example.cravedash

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

/** Walk up the Context wrapper chain to find the Activity. */
fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

/** Real-world distance in metres (Haversine). */
fun haversineMeters(a: LatLng, b: LatLng): Double {
    val R    = 6_371_000.0
    val dLat = Math.toRadians(b.latitude  - a.latitude)
    val dLng = Math.toRadians(b.longitude - a.longitude)
    val h = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(a.latitude)) * cos(Math.toRadians(b.latitude)) *
            sin(dLng / 2).pow(2)
    return 2 * R * asin(sqrt(h))
}

/** Compass bearing from `from` → `to` in degrees [0, 360). */
fun bearingBetween(from: LatLng, to: LatLng): Float {
    val dLng = Math.toRadians(to.longitude - from.longitude)
    val lat1 = Math.toRadians(from.latitude)
    val lat2 = Math.toRadians(to.latitude)
    val x    = sin(dLng) * cos(lat2)
    val y    = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLng)
    return ((Math.toDegrees(atan2(x, y)).toFloat() + 360f) % 360f)
}

/**
 * Draws a top-down car icon on an orange circle — the custom map marker.
 * The car "faces north" (up) by default; we rotate it using Marker.rotation.
 */
fun buildRiderMarker(context: Context): BitmapDescriptor {
    val density = context.resources.displayMetrics.density
    val size    = (64 * density).toInt()
    val s       = size.toFloat()

    val bmp    = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bmp)

    // ── drop shadow ──────────────────────────────────────────────────────────
    val shadow = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.argb(60, 0, 0, 0)
    }
    canvas.drawCircle(s / 2f + 2f, s / 2f + 3f, s / 2f - 3f, shadow)

    // ── orange background circle ─────────────────────────────────────────────
    val bg = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#FF8C00")
    }
    canvas.drawCircle(s / 2f, s / 2f, s / 2f - 3f, bg)

    // ── white car body ───────────────────────────────────────────────────────
    val white = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
    }

    // Main body rectangle
    canvas.drawRoundRect(
        android.graphics.RectF(s * 0.30f, s * 0.18f, s * 0.70f, s * 0.82f),
        s * 0.09f, s * 0.09f, white
    )

    // Windscreen (top window)
    val glass = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#AADDFFFF")
    }
    canvas.drawRoundRect(
        android.graphics.RectF(s * 0.34f, s * 0.23f, s * 0.66f, s * 0.42f),
        s * 0.05f, s * 0.05f, glass
    )

    // Rear window
    canvas.drawRoundRect(
        android.graphics.RectF(s * 0.34f, s * 0.56f, s * 0.66f, s * 0.73f),
        s * 0.05f, s * 0.05f, glass
    )

    // ── wheels (dark orange) ─────────────────────────────────────────────────
    val wheel = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.parseColor("#CC6600")
    }
    // Front-left / front-right
    canvas.drawRoundRect(android.graphics.RectF(s*.17f, s*.22f, s*.31f, s*.40f), s*.04f, s*.04f, wheel)
    canvas.drawRoundRect(android.graphics.RectF(s*.69f, s*.22f, s*.83f, s*.40f), s*.04f, s*.04f, wheel)
    // Rear-left / rear-right
    canvas.drawRoundRect(android.graphics.RectF(s*.17f, s*.60f, s*.31f, s*.78f), s*.04f, s*.04f, wheel)
    canvas.drawRoundRect(android.graphics.RectF(s*.69f, s*.60f, s*.83f, s*.78f), s*.04f, s*.04f, wheel)

    return BitmapDescriptorFactory.fromBitmap(bmp)
}

// ─────────────────────────────────────────────────────────────────────────────
// Order Tracking Screen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(onBackClick: () -> Unit) {

    val context  = LocalContext.current
    val isDark   = ThemeManager.isDarkMode
    val bg       = MaterialTheme.colorScheme.background
    val surface  = MaterialTheme.colorScheme.surface
    val onBg     = MaterialTheme.colorScheme.onBackground
    val sub      = if (isDark) Color(0xFFAAAAAA) else Color(0xFF757575)
    val cardBg   = if (isDark) Color(0xFF252525) else Color.White
    val scope    = rememberCoroutineScope()

    // ── STATE ─────────────────────────────────────────────────────────────────
    var userLocation   by remember { mutableStateOf<LatLng?>(null) }
    // targetRiderPos is the *destination* we animate toward every few seconds
    var targetRiderPos by remember { mutableStateOf<LatLng?>(null) }
    // riderLocation is the *displayed* position that smoothly interpolates
    var riderLocation  by remember { mutableStateOf<LatLng?>(null) }
    var riderBearing   by remember { mutableStateOf(0f) }
    var locationLoading by remember { mutableStateOf(true) }

    var hasPermission  by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // ── STEP 1: Ask for location permission ──────────────────────────────────
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    // ── STEP 2: If permission granted but GPS off → show system GPS dialog ───
    val gpsResolutionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { /* user responded to GPS dialog; location updates auto-start if they said yes */ }

    fun requestGpsIfNeeded() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2_000L
        ).build()
        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)          // keep showing even if user denied once
            .build()
        LocationServices.getSettingsClient(context)
            .checkLocationSettings(settingsRequest)
            .addOnFailureListener { exception ->
                // GPS is off — show the system "Turn on location?" dialog
                (exception as? ResolvableApiException)?.let {
                    try {
                        gpsResolutionLauncher.launch(
                            IntentSenderRequest.Builder(it.resolution).build()
                        )
                    } catch (_: Exception) {}
                }
            }
    }

    // On first composition: ask for permission; when granted, enable GPS
    LaunchedEffect(Unit) {
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            requestGpsIfNeeded()
        }
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) requestGpsIfNeeded()
    }

    // ── STEP 3: Start real GPS updates ───────────────────────────────────────
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    DisposableEffect(hasPermission) {
        if (!hasPermission) return@DisposableEffect onDispose {}

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2_000L)
            .setMinUpdateIntervalMillis(1_000L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    val latLng = LatLng(loc.latitude, loc.longitude)
                    userLocation    = latLng
                    locationLoading = false
                    // Seed the rider ~600 m northeast on the very first fix
                    if (riderLocation == null) {
                        val seed = LatLng(loc.latitude + 0.0055, loc.longitude + 0.0038)
                        riderLocation  = seed
                        targetRiderPos = seed
                    }
                }
            }
        }

        try {
            fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        } catch (_: SecurityException) {}

        onDispose { fusedClient.removeLocationUpdates(callback) }
    }

    // ── STEP 4: Move rider TARGET toward user every 6 s ──────────────────────
    LaunchedEffect(userLocation) {
        while (true) {
            delay(6_000)
            val user  = userLocation  ?: continue
            val rider = riderLocation ?: continue
            val dLat  = user.latitude  - rider.latitude
            val dLng  = user.longitude - rider.longitude
            val dist  = sqrt(dLat * dLat + dLng * dLng)
            if (dist < 0.00008) continue          // already at destination
            targetRiderPos = LatLng(
                rider.latitude  + dLat * 0.18,
                rider.longitude + dLng * 0.18
            )
        }
    }

    // ── STEP 5: Smoothly animate riderLocation → targetRiderPos (60 fps) ─────
    LaunchedEffect(targetRiderPos) {
        val target = targetRiderPos ?: return@LaunchedEffect
        val start  = riderLocation  ?: return@LaunchedEffect
        if (start == target) return@LaunchedEffect

        // Rotate car to face its direction of travel
        riderBearing = bearingBetween(start, target)

        val frames = 80                              // ~1.3 s at 60 fps
        repeat(frames) { frame ->
            val t = (frame + 1f) / frames
            // Ease-in-out: smooth start and end
            val eased = t * t * (3f - 2f * t)
            riderLocation = LatLng(
                start.latitude  + (target.latitude  - start.latitude)  * eased,
                start.longitude + (target.longitude - start.longitude) * eased
            )
            delay(16)
        }
        riderLocation = target
    }

    // ── MAP CAMERA — keep both pins visible ───────────────────────────────────
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(6.5244, 3.3792), 14f) // Lagos
    }

    LaunchedEffect(riderLocation, userLocation) {
        val user  = userLocation  ?: return@LaunchedEffect
        val rider = riderLocation ?: return@LaunchedEffect
        try {
            val bounds = LatLngBounds.Builder()
                .include(user)
                .include(rider)
                .build()
            cameraState.animate(
                CameraUpdateFactory.newLatLngBounds(bounds, 220),
                1_000
            )
        } catch (_: Exception) {
            // Fallback if bounds are degenerate
            cameraState.animate(CameraUpdateFactory.newLatLngZoom(user, 15f), 800)
        }
    }

    // ── DERIVED: distance + ETA (updates whenever rider/user move) ───────────
    val distanceText by remember(riderLocation, userLocation) {
        derivedStateOf {
            val r = riderLocation ?: return@derivedStateOf "Locating rider…"
            val u = userLocation  ?: return@derivedStateOf "Getting location…"
            val m = haversineMeters(r, u)
            when {
                m < 80   -> "🎉 Almost at your door!"
                m < 1000 -> "${"%.0f".format(m)} m away"
                else     -> "${"%.1f".format(m / 1000)} km away"
            }
        }
    }

    val etaText by remember(riderLocation, userLocation) {
        derivedStateOf {
            val r = riderLocation ?: return@derivedStateOf "—"
            val u = userLocation  ?: return@derivedStateOf "—"
            val m = haversineMeters(r, u)
            // ~25 km/h in Lagos traffic
            val minutes = (m / (25_000.0 / 60.0)).toInt().coerceAtLeast(1)
            "${minutes} min"
        }
    }

    // ── CUSTOM MARKER (lazy — only build once, only after permission) ─────────
    val riderMarkerIcon: BitmapDescriptor? = remember(hasPermission) {
        if (!hasPermission) return@remember null
        try { buildRiderMarker(context) }
        catch (_: Exception) { null }
    }

    // ── PULSING RING animation ────────────────────────────────────────────────
    val pulse = rememberInfiniteTransition(label = "ring")
    val ringAlpha by pulse.animateFloat(
        0.6f, 0f,
        infiniteRepeatable(tween(1_400, easing = EaseOut), RepeatMode.Restart),
        label = "ringAlpha"
    )
    val ringScale by pulse.animateFloat(
        1f, 2.4f,
        infiniteRepeatable(tween(1_400, easing = EaseOut), RepeatMode.Restart),
        label = "ringScale"
    )

    // ─────────────────────────────────────────────────────────────────────────
    // UI
    // ─────────────────────────────────────────────────────────────────────────
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Live Tracking", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = onBg)
                        Text(distanceText, fontSize = 12.sp, color = Color(0xFFFF8C00),
                            fontWeight = FontWeight.SemiBold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = onBg)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = surface),
                windowInsets = WindowInsets(0)
            )
        },
        containerColor = bg,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ── MAP AREA (top 58 %) ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.58f)
            ) {
                when {
                    // ── No permission ──────────────────────────────────────
                    !hasPermission -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(if (isDark) Color(0xFF1A1A1A) else Color(0xFFF2F2F2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFF3E0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.LocationOff, null,
                                        tint = Color(0xFFFF8C00),
                                        modifier = Modifier.size(40.dp))
                                }
                                Spacer(Modifier.height(20.dp))
                                Text("Location access needed",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = onBg)
                                Spacer(Modifier.height(8.dp))
                                Text("Allow location so we can show your rider moving in real time",
                                    color = sub,
                                    fontSize = 13.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                Spacer(Modifier.height(24.dp))
                                Button(
                                    onClick = {
                                        permissionLauncher.launch(
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF8C00)
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.MyLocation, null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Enable Location",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White)
                                }
                            }
                        }
                    }

                    // ── Waiting for first GPS fix ──────────────────────────
                    locationLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(if (isDark) Color(0xFF1A1A1A) else Color(0xFFF5F5F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    color = Color(0xFFFF8C00),
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(16.dp))
                                Text("Getting your location…",
                                    color = sub,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium)
                                Spacer(Modifier.height(6.dp))
                                Text("Make sure your GPS is on",
                                    color = sub.copy(0.6f),
                                    fontSize = 12.sp)
                            }
                        }
                    }

                    // ── LIVE MAP ───────────────────────────────────────────
                    else -> {
                        GoogleMap(
                            modifier    = Modifier.fillMaxSize(),
                            cameraPositionState = cameraState,
                            uiSettings  = MapUiSettings(
                                zoomControlsEnabled     = false,
                                myLocationButtonEnabled = false,
                                compassEnabled          = false,
                                mapToolbarEnabled       = false
                            ),
                            properties = MapProperties(
                                isMyLocationEnabled = hasPermission,
                                mapType             = MapType.NORMAL
                            )
                        ) {
                            // ── Route polyline ───────────────────────────
                            if (riderLocation != null && userLocation != null) {
                                Polyline(
                                    points  = listOf(riderLocation!!, userLocation!!),
                                    color   = Color(0xFFFF8C00),
                                    width   = 10f,
                                    pattern = listOf(
                                        com.google.android.gms.maps.model.Dash(20f),
                                        com.google.android.gms.maps.model.Gap(12f)
                                    )
                                )
                            }

                            // ── Destination pin (user's location) ────────
                            userLocation?.let { loc ->
                                Marker(
                                    state   = MarkerState(loc),
                                    title   = "Your Location",
                                    icon    = BitmapDescriptorFactory.defaultMarker(
                                                  BitmapDescriptorFactory.HUE_AZURE),
                                    zIndex  = 1f
                                )
                            }

                            // ── Rider pulsing ring ────────────────────────
                            riderLocation?.let { loc ->
                                Circle(
                                    center      = loc,
                                    radius      = 120.0 * ringScale,
                                    fillColor   = Color(0xFFFF8C00).copy(alpha = ringAlpha * 0.25f),
                                    strokeColor = Color(0xFFFF8C00).copy(alpha = ringAlpha * 0.60f),
                                    strokeWidth = 3f
                                )
                            }

                            // ── Rider car marker (custom, rotated) ────────
                            riderLocation?.let { loc ->
                                Marker(
                                    state    = MarkerState(loc),
                                    title    = "Samuel — Your Rider",
                                    snippet  = "On the way!",
                                    icon     = riderMarkerIcon
                                        ?: BitmapDescriptorFactory.defaultMarker(
                                               BitmapDescriptorFactory.HUE_ORANGE),
                                    rotation = riderBearing,
                                    flat     = true,           // marker lies flat on map
                                    anchor   = androidx.compose.ui.geometry.Offset(0.5f, 0.5f),
                                    zIndex   = 2f
                                )
                            }
                        }

                        // ── ETA floating chip ─────────────────────────────
                        Surface(
                            modifier        = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 14.dp),
                            color           = Color(0xFFFF8C00),
                            shape           = RoundedCornerShape(24.dp),
                            shadowElevation = 10.dp
                        ) {
                            Row(
                                modifier         = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.DeliveryDining, null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "ETA  $etaText  ·  $distanceText",
                                    color      = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 14.sp
                                )
                            }
                        }

                        // ── Re-centre button ──────────────────────────────
                        SmallFloatingActionButton(
                            onClick = {
                                scope.launch {
                                    val user  = userLocation  ?: return@launch
                                    val rider = riderLocation ?: return@launch
                                    try {
                                        val bounds = LatLngBounds.Builder()
                                            .include(user).include(rider).build()
                                        cameraState.animate(
                                            CameraUpdateFactory.newLatLngBounds(bounds, 220), 800
                                        )
                                    } catch (_: Exception) {
                                        userLocation?.let {
                                            cameraState.animate(
                                                CameraUpdateFactory.newLatLngZoom(it, 15f), 600
                                            )
                                        }
                                    }
                                }
                            },
                            modifier         = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp),
                            containerColor   = Color.White,
                            contentColor     = Color(0xFFFF8C00),
                            shape            = CircleShape
                        ) {
                            Icon(Icons.Default.MyLocation, "Re-centre",
                                modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            // ── BOTTOM PANEL (42 %) ───────────────────────────────────────────
            Surface(
                modifier        = Modifier
                    .fillMaxWidth()
                    .weight(0.42f),
                color           = surface,
                shadowElevation = 20.dp,
                shape           = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .navigationBarsPadding()
                ) {

                    // Drag handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 10.dp)
                            .size(40.dp, 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(sub.copy(0.3f))
                    )

                    Spacer(Modifier.height(14.dp))

                    // ── Status banner ─────────────────────────────────────
                    Surface(
                        color = if (isDark) Color(0xFF2A2A2A) else Color(0xFFFFF8F0),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment    = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50))
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("On the way",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize   = 14.sp,
                                        color      = onBg)
                                    Text(distanceText,
                                        fontSize   = 12.sp,
                                        color      = sub)
                                }
                            }
                            Surface(
                                color = Color(0xFFFF8C00),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    etaText,
                                    modifier   = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    color      = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize   = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // ── Rider card ────────────────────────────────────────
                    Surface(
                        color           = cardBg,
                        shape           = RoundedCornerShape(16.dp),
                        shadowElevation = if (isDark) 0.dp else 3.dp
                    ) {
                        Row(
                            modifier         = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar
                            Box(
                                modifier         = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF8C00)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("S",
                                    color      = Color.White,
                                    fontSize   = 22.sp,
                                    fontWeight = FontWeight.ExtraBold)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Samuel Adebayo",
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 15.sp,
                                    color      = onBg)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null,
                                        tint     = Color(0xFFFFC107),
                                        modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(3.dp))
                                    Text("4.9  ·  Honda Civic · ABC-123",
                                        fontSize = 12.sp,
                                        color    = sub)
                                }
                            }
                            // Call button
                            Surface(
                                color  = Color(0xFFFF8C00),
                                shape  = CircleShape,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier         = Modifier.fillMaxSize()
                                ) {
                                    Icon(Icons.Default.Phone, null,
                                        tint     = Color.White,
                                        modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // ── Progress steps ────────────────────────────────────
                    TrackStep(Icons.Default.Check,          "Order Confirmed",  "We received your order",             done = true,                    onBg = onBg, sub = sub)
                    TrackStep(Icons.Default.Restaurant,     "Preparing",        "Chefs are cooking your meal",        done = true,                    onBg = onBg, sub = sub)
                    TrackStep(Icons.Default.DeliveryDining, "On the Way",       "Rider heading to you  📍",           done = false, current = true,  onBg = onBg, sub = sub)
                    TrackStep(Icons.Default.ShoppingBag,    "Delivered",        "Enjoy your CraveDash meal!",         done = false, last    = true,  onBg = onBg, sub = sub)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Progress step row
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TrackStep(
    icon: ImageVector,
    title: String,
    subtitle: String,
    done: Boolean,
    current: Boolean = false,
    last: Boolean    = false,
    onBg: Color,
    sub: Color
) {
    val pulse = rememberInfiniteTransition(label = "step$title")
    val dotScale by pulse.animateFloat(
        initialValue  = 1f,
        targetValue   = if (current) 1.25f else 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label         = "dot$title"
    )

    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(32.dp * dotScale)
                    .clip(CircleShape)
                    .background(
                        when {
                            done || current -> Color(0xFFFF8C00)
                            else            -> sub.copy(0.2f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null,
                    tint     = Color.White,
                    modifier = Modifier.size(16.dp))
            }
            if (!last) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(28.dp)
                        .background(if (done) Color(0xFFFF8C00) else sub.copy(0.2f))
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.padding(top = 5.dp)) {
            Text(title,
                fontWeight = if (current) FontWeight.ExtraBold else FontWeight.SemiBold,
                fontSize   = 13.sp,
                color      = if (done || current) onBg else sub)
            Text(subtitle, fontSize = 11.sp, color = sub)
        }
    }
}
