package com.example.cravedash

import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// Slide data
// ─────────────────────────────────────────────────────────────────────────────
private data class OnboardSlide(
    val imageUrl: String,
    @DrawableRes val badgeIcon: Int,
    val badgeText: String,
    val title: String,
    val subtitle: String
)

private val slides = listOf(
    OnboardSlide(
        imageUrl  = "https://images.unsplash.com/photo-1517244683847-7456b63c5969?w=900",
        badgeIcon = R.drawable.ic_badge_food,
        badgeText = "Nigerian Cuisine",
        title     = "Crave it.\nOrder it.\nEnjoy it.",
        subtitle  = "Premium meals from the best chefs in Lagos, delivered fast to your door."
    ),
    OnboardSlide(
        imageUrl  = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=900",
        badgeIcon = R.drawable.ic_badge_tracking,
        badgeText = "Live GPS Tracking",
        title     = "Watch your\nfood move\nto you live.",
        subtitle  = "Real-time maps show your rider heading straight to your door every second."
    ),
    OnboardSlide(
        imageUrl  = "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=900",
        badgeIcon = R.drawable.ic_badge_ai,
        badgeText = "AI Concierge",
        title     = "Meet MAX.\nYour personal\nfood expert.",
        subtitle  = "MAX knows every dish, reads your mood, and always has the perfect recommendation."
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// Onboarding — bike intro → 3-slide premium experience
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onGetStartedClick: () -> Unit) {

    // Bike intro lasts 2.6 s, then crossfades into the 3-slide design
    var splashDone by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(2_600)
        splashDone = true
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ── The 3 slides always live underneath ───────────────────────────────
        SlidesContent(onGetStartedClick = onGetStartedClick)

        // ── Bike intro overlays everything and fades out ───────────────────────
        AnimatedVisibility(
            visible = !splashDone,
            exit    = fadeOut(animationSpec = tween(600))
        ) {
            BikeIntroOverlay()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bike intro screen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun BikeIntroOverlay() {

    // Branding slides in after 350 ms
    var brandIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(350); brandIn = true }

    // Bike: zooms left → right on an infinite loop
    val bikeTransition = rememberInfiniteTransition(label = "bikeX")
    val bikeX by bikeTransition.animateFloat(
        initialValue  = -160f,
        targetValue   = 1380f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1_400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bikePos"
    )

    // Bike bounce: simulates wheels on road
    val bikeY by bikeTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = -10f,
        animationSpec = infiniteRepeatable(
            animation  = tween(170, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bikeBounce"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // ── CraveDash branding (fades + slides in) ────────────────────────────
        AnimatedVisibility(
            visible = brandIn,
            enter   = fadeIn(tween(700)) + slideInVertically(
                animationSpec    = tween(700, easing = FastOutSlowInEasing),
                initialOffsetY   = { 40 }
            ),
            modifier = Modifier.align(Alignment.Center).offset(y = (-90).dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Orange CD badge
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFFF8C00)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "CD",
                        color      = Color.White,
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(Modifier.height(18.dp))
                Text(
                    "CRAVE DASH",
                    color         = Color.White,
                    fontSize      = 34.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    letterSpacing = 6.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Food. Fast. Delivered.",
                    color         = Color(0xFFFF8C00),
                    fontSize      = 14.sp,
                    fontWeight    = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )
            }
        }

        // ── Orange road/track glow ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.Center)
                .offset(y = 56.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color(0xFFFF8C00).copy(0.5f),
                            Color(0xFFFF8C00),
                            Color(0xFFFF8C00),
                            Color(0xFFFF8C00).copy(0.5f),
                            Color.Transparent
                        )
                    )
                )
        )

        // ── Speed-streak trail behind the bike ────────────────────────────────
        Box(
            modifier = Modifier
                .width(90.dp)
                .height(2.dp)
                .align(Alignment.Center)
                .offset(x = (bikeX - 80).dp, y = 48.dp)
                .alpha(0.5f)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, Color(0xFFFF8C00).copy(0.6f))
                    )
                )
        )
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(1.dp)
                .align(Alignment.Center)
                .offset(x = (bikeX - 110).dp, y = 44.dp)
                .alpha(0.3f)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, Color(0xFFFF8C00).copy(0.4f))
                    )
                )
        )

        // ── The delivery bike 🛵 ──────────────────────────────────────────────
        Text(
            text     = "🛵",
            fontSize = 68.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = bikeX.dp, y = (38 + bikeY).dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 3-slide swipe content (unchanged from before)
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SlidesContent(onGetStartedClick: () -> Unit) {

    val pagerState  = rememberPagerState { slides.size }
    val scope       = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    // Auto-advance every 4 s (pauses on last slide)
    LaunchedEffect(currentPage) {
        if (currentPage < slides.size - 1) {
            delay(4_000)
            pagerState.animateScrollToPage(currentPage + 1)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            SlideView(slides[page])
        }

        // ── CraveDash logo top-left ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 14.dp, start = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color(0xFFFF8C00)),
                contentAlignment = Alignment.Center
            ) {
                Text("CD", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.width(8.dp))
            Text("CraveDash", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }

        // ── Skip button ───────────────────────────────────────────────────────
        if (currentPage < slides.size - 1) {
            Text(
                "Skip",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(top = 14.dp, end = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(0.18f))
                    .clickable { onGetStartedClick() }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color      = Color.White,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // ── Bottom controls (dots + buttons) ─────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(slides.size) { index ->
                    val isActive = index == currentPage
                    val dotWidth by animateDpAsState(
                        targetValue   = if (isActive) 28.dp else 8.dp,
                        animationSpec = tween(300),
                        label         = "dot$index"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(dotWidth)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isActive) Color(0xFFFF8C00)
                                else Color.White.copy(0.35f)
                            )
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            if (currentPage == slides.size - 1) {
                Button(
                    onClick   = onGetStartedClick,
                    modifier  = Modifier.fillMaxWidth().height(58.dp),
                    colors    = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                    shape     = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Get Started  🚀",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White)
                }
            } else {
                Button(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(currentPage + 1) }
                    },
                    modifier  = Modifier.fillMaxWidth().height(58.dp),
                    colors    = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(0.15f)
                    ),
                    shape     = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text("Next  →",
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color.White)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Individual slide composable
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SlideView(slide: OnboardSlide) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model              = slide.imageUrl,
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0.00f to Color.Black.copy(0.25f),
                    0.45f to Color.Black.copy(0.50f),
                    1.00f to Color.Black.copy(0.92f)
                )
            )
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 28.dp)
                .padding(bottom = 220.dp)
        ) {
            // Premium icon badge — custom vector icon instead of emoji
            Surface(
                color  = Color(0xFFFF8C00).copy(0.22f),
                shape  = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier             = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment    = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Image(
                        painter          = painterResource(slide.badgeIcon),
                        contentDescription = null,
                        modifier         = Modifier.size(16.dp),
                        colorFilter      = ColorFilter.tint(Color(0xFFFF8C00))
                    )
                    Text(
                        slide.badgeText,
                        color      = Color(0xFFFF8C00),
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                slide.title,
                fontSize      = 40.sp,
                fontWeight    = FontWeight.ExtraBold,
                color         = Color.White,
                lineHeight    = 46.sp,
                letterSpacing = (-1).sp
            )
            Spacer(Modifier.height(14.dp))
            Text(
                slide.subtitle,
                fontSize   = 15.sp,
                color      = Color.White.copy(0.72f),
                lineHeight = 24.sp
            )
        }
    }
}
