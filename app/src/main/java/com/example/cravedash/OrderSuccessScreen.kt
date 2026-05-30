package com.example.cravedash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.alpha
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
// Order Success Screen — animated celebration
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun OrderSuccessScreen(
    onTrackOrderClick: () -> Unit,
    onContinueShoppingClick: () -> Unit
) {
    val isDark  = ThemeManager.isDarkMode
    val bg      = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onBg    = MaterialTheme.colorScheme.onBackground
    val sub     = if (isDark) Color(0xFFAAAAAA) else Color(0xFF757575)

    // Generate a unique order reference once
    val orderRef = remember { "CD-${(100000..999999).random()}" }

    // ── Animations ────────────────────────────────────────────────────────────

    // Circle scale-in on entry
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(100); started = true }

    val circleScale by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 200f),
        label         = "circleScale"
    )

    // Checkmark progressive draw
    val checkProgress by animateFloatAsState(
        targetValue   = if (started) 1f else 0f,
        animationSpec = tween(700, delayMillis = 300, easing = LinearOutSlowInEasing),
        label         = "check"
    )

    // Outer pulsing ring
    val pulseTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseTransition.animateFloat(
        1f, 1.5f,
        infiniteRepeatable(tween(1_200), RepeatMode.Reverse),
        label = "ps"
    )
    val pulseAlpha by pulseTransition.animateFloat(
        0.5f, 0f,
        infiniteRepeatable(tween(1_200), RepeatMode.Reverse),
        label = "pa"
    )

    // Food emoji particles floating upward
    data class Particle(val emoji: String, val xFraction: Float, val delayMs: Int)
    val particles = remember {
        listOf(
            Particle("🍛", -0.32f, 0),
            Particle("🍔", 0.28f,  200),
            Particle("🥤", -0.08f, 400),
            Particle("🍕", 0.40f,  600),
            Particle("🍟", -0.42f, 800),
            Particle("🍣", 0.12f,  1_000)
        )
    }
    val particleTransition = rememberInfiniteTransition(label = "particles")

    // ── UI ────────────────────────────────────────────────────────────────────
    Surface(
        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding(),
        color    = bg
    ) {
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ── Animated success indicator ────────────────────────────────────
            Box(
                modifier         = Modifier.size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer pulsing ring
                if (started) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50).copy(alpha = pulseAlpha * 0.3f))
                    )
                }

                // Middle ring
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(circleScale)
                        .clip(CircleShape)
                        .background(
                            if (isDark) Color(0xFF1B3A1B)
                            else Color(0xFFD7F5D7)
                        )
                )

                // Inner filled circle
                Box(
                    modifier         = Modifier
                        .size(100.dp)
                        .scale(circleScale)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    // Animated checkmark drawn via Canvas
                    Canvas(modifier = Modifier.size(50.dp)) {
                        if (checkProgress > 0f) {
                            val w = size.width
                            val h = size.height

                            // Checkmark points
                            val p1x = w * 0.18f; val p1y = h * 0.52f
                            val p2x = w * 0.42f; val p2y = h * 0.72f
                            val p3x = w * 0.82f; val p3y = h * 0.28f

                            val path = Path()
                            path.moveTo(p1x, p1y)

                            if (checkProgress < 0.5f) {
                                val t = checkProgress * 2f
                                path.lineTo(
                                    p1x + (p2x - p1x) * t,
                                    p1y + (p2y - p1y) * t
                                )
                            } else {
                                path.lineTo(p2x, p2y)
                                val t = (checkProgress - 0.5f) * 2f
                                path.lineTo(
                                    p2x + (p3x - p2x) * t,
                                    p2y + (p3y - p2y) * t
                                )
                            }

                            drawPath(
                                path  = path,
                                color = Color.White,
                                style = Stroke(
                                    width    = 5.dp.toPx(),
                                    cap      = StrokeCap.Round,
                                    join     = StrokeJoin.Round
                                )
                            )
                        }
                    }
                }

                // Floating food emoji particles
                particles.forEach { particle ->
                    val yAnim by particleTransition.animateFloat(
                        0f, -180f,
                        infiniteRepeatable(
                            tween(2_200, particle.delayMs, LinearEasing),
                            RepeatMode.Restart
                        ),
                        label = "y${particle.emoji}"
                    )
                    val alphaAnim by particleTransition.animateFloat(
                        1f, 0f,
                        infiniteRepeatable(
                            tween(2_200, particle.delayMs, LinearOutSlowInEasing),
                            RepeatMode.Restart
                        ),
                        label = "a${particle.emoji}"
                    )
                    if (started) {
                        Text(
                            particle.emoji,
                            fontSize = 22.sp,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(
                                    x = (80 * particle.xFraction).dp,
                                    y = yAnim.dp
                                )
                                .alpha(alphaAnim)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Heading ───────────────────────────────────────────────────────
            Text(
                "Order Placed! 🎉",
                fontSize   = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = onBg,
                textAlign  = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Your meal is being prepared by our chefs.\nGet ready to feast!",
                fontSize   = 15.sp,
                color      = sub,
                textAlign  = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(28.dp))

            // ── Order info card ───────────────────────────────────────────────
            Surface(
                modifier        = Modifier.fillMaxWidth(),
                color           = surface,
                shape           = RoundedCornerShape(20.dp),
                shadowElevation = if (isDark) 0.dp else 4.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OrderInfoRow(
                        icon    = Icons.Default.Tag,
                        label   = "Order Reference",
                        value   = orderRef,
                        onBg    = onBg,
                        sub     = sub,
                        isDark  = isDark
                    )
                    HorizontalDivider(
                        modifier  = Modifier.padding(vertical = 12.dp),
                        color     = if (isDark) Color(0xFF2E2E2E) else Color(0xFFF0F0F0)
                    )
                    OrderInfoRow(
                        icon    = Icons.Default.Schedule,
                        label   = "Estimated Delivery",
                        value   = "25 – 35 min",
                        onBg    = onBg,
                        sub     = sub,
                        isDark  = isDark
                    )
                    HorizontalDivider(
                        modifier  = Modifier.padding(vertical = 12.dp),
                        color     = if (isDark) Color(0xFF2E2E2E) else Color(0xFFF0F0F0)
                    )
                    OrderInfoRow(
                        icon    = Icons.Default.LocalShipping,
                        label   = "Status",
                        value   = "Being prepared ✓",
                        onBg    = onBg,
                        sub     = sub,
                        isDark  = isDark,
                        valueColor = Color(0xFF4CAF50)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Track Order ───────────────────────────────────────────────────
            Button(
                onClick   = onTrackOrderClick,
                modifier  = Modifier.fillMaxWidth().height(56.dp),
                colors    = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                shape     = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text("Track My Order  📍",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = Color.White)
            }

            Spacer(Modifier.height(12.dp))

            // ── Continue Shopping ─────────────────────────────────────────────
            OutlinedButton(
                onClick  = onContinueShoppingClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.outlinedButtonColors(
                    contentColor = onBg
                )
            ) {
                Text("Continue Shopping",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color      = onBg)
            }
        }
    }
}

@Composable
private fun OrderInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onBg: Color,
    sub: Color,
    isDark: Boolean,
    valueColor: Color = Color(0xFFFF8C00)
) {
    Row(
        modifier         = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isDark) Color(0xFF2A2A2A) else Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null,
                tint     = Color(0xFFFF8C00),
                modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label,
                fontSize = 12.sp,
                color    = sub)
            Text(value,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Bold,
                color      = valueColor)
        }
    }
}
