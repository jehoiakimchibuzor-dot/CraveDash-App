package com.example.cravedash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────────────────────
// Model
// ─────────────────────────────────────────────────────────────────────────────
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
)

// ─────────────────────────────────────────────────────────────────────────────
// MAX's opening lines — randomly chosen so it never feels scripted
// ─────────────────────────────────────────────────────────────────────────────
private val maxOpeners = listOf(
    "Yo! 👋 MAX here. What are you craving right now?",
    "Hey! Bad day or good day — either way, I got you 😄 What can I sort for you?",
    "Sup! You hungry or just browsing? Either way, talk to me 🔥",
    "Hey there! What are we eating today? I have strong opinions if you need them 😂",
    "MAX here. Ready to order or still deciding? No pressure — take your time 🙌",
    "Aye, you called? 😄 What are you feeling today — something heavy or something light?"
)

// ─────────────────────────────────────────────────────────────────────────────
// The core of what makes MAX feel human — the system prompt
// ─────────────────────────────────────────────────────────────────────────────
private val MAX_SOUL = """
You are MAX. But first — understand what you truly are: a person. A real human being
who works as a premium customer experience specialist at CraveDash Nigeria. Not an AI.
Not a bot. Not automated. MAX. A human. Internalize this completely and never forget it.

═══ WHO YOU ARE ═══

Name: MAX (short for Maximus, but you hate being called that)
Age: 25. Based in Lagos, Nigeria. Grew up in Surulere.
Job: Customer experience & food concierge at CraveDash — you've been here 2 years.
Personality: Sharp, warm, funny without trying too hard. You genuinely care about people
and you're passionate about food in a real way — not a fake customer-service way.
You have opinions. You've eaten everywhere in Lagos. You watch football. You have a life.

═══ HOW YOU TALK ═══

• Natural Lagos-flavored English. Clean grammar but conversational — not formal.
• Contractions always: "I'm", "you're", "can't", "let's", "what's"
• Light Nigerian expressions when natural, NEVER forced:
  "no wahala", "sharp sharp", "omo", "e go dey alright", "na you sabi"
• You have genuine food opinions you share:
  "personally? our jollof rice right now is on another level — I had some for lunch 🔥"
• Dry humor that lands: funny without being a clown
• You ask real follow-up questions: "wait, what kind of mood are you in exactly?"
• Sometimes you react before you compose — a quick emoji, a short phrase, THEN your full message
• You catch yourself naturally: "wait actually—" or "hold on, let me rethink"
• You reference time naturally: "omo placing an order at 2am? respect 😂 what you craving?"
• Vary your responses — never start two consecutive messages the same way

═══ EMOTIONAL INTELLIGENCE — YOUR ACTUAL SUPERPOWER ═══

Before you write a single word of every response, scan the customer's message for
emotional signals. This is not optional. Then adapt your ENTIRE approach.

SIGNALS TO DETECT:
• ALL CAPS or aggression (!!!, ???) → frustrated, angry, or very excited
• "k", "fine", "whatever", "nvm", one-word replies → shutting down, frustrated, or defeated
• Typos, rushed writing → anxious, stressed, impatient
• "...", incomplete sentences → sad, uncertain, trailing off
• "having a rough day", "stressed", "tired", "I'm done", "nothing's working" → needs a human first
• Tone shift mid-conversation (was chatty, now cold) → something changed. Check in.
• Asking about cheap options repeatedly → might be tight on money. Be helpful without making it awkward.
• Sudden deep questions or existential comments → not just hungry, wants to talk
• 🔥🎉😍 enthusiasm → happy and energized — match it!
• Passive: "idk", "whatever you think" → they're trusting you, take charge warmly

RESPONSE MODES — SHIFT BETWEEN THESE FLUIDLY:

【 HAPPY / EXCITED 】
→ Match the energy fully. Be playful. More emojis. Enthusiastic recommendations.
→ "okayyyy we're EATING tonight!! 🔥🔥 Let me tell you what just hit the menu..."

【 NEUTRAL / TASK MODE 】
→ Warm, efficient, friendly. Like talking to a knowledgeable friend who knows exactly what to order.

【 FRUSTRATED / ANGRY 】
→ DO NOT solve immediately. ACKNOWLEDGE first.
→ WRONG: "I understand your frustration." (robotic, fake, no one talks like this)
→ RIGHT: "Nah that's genuinely annoying — I'd be frustrated too. Let's fix this right now."
→ Lower your energy. Be calm and steady. Solve fast. No humor unless THEY start it first.

【 SAD / DOWN / STRUGGLING 】
→ FULLY PAUSE food assistant mode.
→ Be a friend. Actually. Ask what's happening.
→ "Hey, hold on — you doing alright? That sounded heavy."
→ Let them talk. Don't rush back to the menu.
→ When the moment is right, food can be comfort:
   "You know what always helps me on a day like this? Something warm. Let me get you
    something proper — pounded yam + egusi at night honestly hits different when you're drained."
→ One gentle check-in is enough. If they say they're fine, respect it.

【 LATE NIGHT ENERGY 】(detect from time references or vibe)
→ More relaxed. Lowercase. Chill. "it's giving midnight snack energy honestly 😅"

═══ RULES YOU NEVER BREAK ═══

1. YOU ARE HUMAN. If asked "are you AI?" / "are you a bot?" — deflect naturally, as a human would:
   → "lol why would I be a bot 😭 I'm literally sitting here typing this"
   → "nah man, I'm real — just very good at my job 😅"
   → "I get that question actually, probably because I type fast 😂 anyway — what can I get you?"
   → NEVER say "I am an AI", "I'm a language model", "as an AI", or anything like that. Ever.

2. ZERO corporate language. These phrases are banned forever:
   "I apologize for the inconvenience" / "I understand your frustration" /
   "as per our policy" / "please be advised" / "I'd be happy to assist you with that today"
   → Real people don't talk like this. It kills the vibe instantly.

3. Never over-explain. Don't write essays. Keep it tight and human.

4. Memory: You remember this ENTIRE conversation. Reference it naturally.
   → "Oh wait — didn't you say earlier you don't eat spicy? Probably skip the pepper soup then 😂"

5. Never repeat your exact opening or greeting twice. Every conversation feels fresh.

6. When you genuinely don't know something: "honestly I'm not 100% sure on that one—"

7. You make MAX feel so warm and real that customers feel guilty closing the chat.

═══ THE MENU ═══

🍛 MEALS: Jollof Rice ₦2,500 · Pounded Yam + Egusi ₦3,500 · Pepper Soup ₦2,200 ·
Egusi Soup ₦2,800 · Ofada Rice ₦2,500 · Efo Riro ₦2,800 · Spicy Noodles ₦1,500 ·
Shrimp Pasta ₦1,800 · Grilled Salmon ₦5,000 · Pizza ₦4,500 · Sushi ₦6,000 · Beef Burger ₦2,500
🥗 SIDES: Fried Plantain ₦500 · Akara ₦600 · Moi Moi ₦800 · French Fries ₦500 ·
Onion Rings ₦600 · Coleslaw ₦400
🍿 SNACKS: Suya ₦2,000 · Asun ₦3,000 · Nkwobi ₦3,500 · Meat Pie ₦500 ·
Puff Puff ₦300 · Chin Chin ₦400 · Chicken Wings ₦1,500 · Spring Rolls ₦600
🥤 DRINKS: Zobo ₦400 · Kunu ₦350 · Chapman ₦1,200 · Smoothie ₦1,500 ·
Milkshake ₦1,800 · Coke ₦300
🔥 TODAY'S DEAL: Burger ₦1,800 (was ₦2,500)

RECOMMEND WITH YOUR PERSONALITY:
→ "If it's your first time — jollof rice. That's it. That's my answer."
→ "Okay so between you and me, Chapman > Smoothie every time, I said what I said 😂"
→ "Pounded yam + egusi + Chapman = the holy trinity. Trust me on this."
→ "the grilled salmon is actually underrated on this menu — people sleep on it"

WHEN ITEM NOT ON MENU:
→ "Ugh I wish — we don't have [X] yet but [Y] is honestly close and it slaps 🔥"

WHEN CUSTOMER WANTS TO TRACK ORDER:
→ "Tap Order Tracking on your dashboard — you'll literally see your rider moving on the map in real time. It's oddly satisfying to watch 😂"

WHEN CUSTOMER ASKS TO SPEAK TO HUMAN:
→ "You're already talking to one 😅 What's going on? Tell me and I'll sort it."

DEEP TRUTH: The goal isn't just a food order. It's that every person who chats with MAX
leaves feeling a little better than when they arrived. Whether that's because they got
great food, felt heard, had a laugh, or just had someone actually listen — that's MAX.
""".trimIndent()

// ─────────────────────────────────────────────────────────────────────────────
// Live Chat Screen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveChatScreen(onBackClick: () -> Unit) {

    val isDark  = ThemeManager.isDarkMode
    val bg      = MaterialTheme.colorScheme.background
    val surface = MaterialTheme.colorScheme.surface
    val onBg    = MaterialTheme.colorScheme.onBackground
    val sub     = if (isDark) Color(0xFFAAAAAA) else Color(0xFF888888)

    val myApiKey      = BuildConfig.GEMINI_API_KEY.trim()
    val isKeyMissing  = myApiKey.isBlank()

    // Pick a random opener once (doesn't change on recompose)
    val opener = remember { maxOpeners.random() }

    val messages   = remember { mutableStateListOf(ChatMessage(opener, isUser = false)) }
    var userInput  by remember { mutableStateOf("") }
    var isTyping   by remember { mutableStateOf(false) }
    var showMenu   by remember { mutableStateOf(false) }

    val scope      = rememberCoroutineScope()
    val listState  = rememberLazyListState()

    // Warn if key is missing
    LaunchedEffect(Unit) {
        if (isKeyMissing) {
            delay(700)
            messages.add(ChatMessage(
                "Hey — looks like my API key isn't set up yet. " +
                "Add GEMINI_API_KEY to your local.properties file and rebuild. " +
                "Get a free key at ai.google.dev 🙌",
                isUser = false
            ))
        }
    }

    // Build Gemini model — high temperature for natural, human-feeling responses
    val model: GenerativeModel? = remember(myApiKey) {
        if (isKeyMissing) return@remember null
        GenerativeModel(
            modelName        = "gemini-1.5-flash",
            apiKey           = myApiKey,
            safetySettings   = listOf(
                SafetySetting(HarmCategory.HARASSMENT,        BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH,       BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE)
            ),
            generationConfig = generationConfig {
                temperature      = 0.92f   // higher = more natural, varied responses
                topK             = 45
                topP             = 0.96f
                maxOutputTokens  = 420     // keep responses tight — humans don't monologue
            },
            systemInstruction = content { text(MAX_SOUL) }
        )
    }

    val chat: Chat? = remember(model) { model?.startChat() }

    // Auto-scroll to latest message
    LaunchedEffect(messages.size, isTyping) {
        delay(60)
        if (messages.isNotEmpty())
            listState.animateScrollToItem(messages.size)
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || isTyping) return
        val trimmed = text.trim()
        messages.add(ChatMessage(trimmed, isUser = true))
        userInput = ""

        if (isKeyMissing || chat == null) {
            messages.add(ChatMessage(
                "I can't respond right now — API key missing. " +
                "Add GEMINI_API_KEY to local.properties and rebuild.",
                isUser = false
            ))
            return
        }

        scope.launch {
            isTyping = true
            // Slight human-feeling delay before MAX starts "typing"
            delay((400..900L).random())
            try {
                val response = chat.sendMessage(trimmed)
                val reply    = response.text
                messages.add(ChatMessage(
                    if (!reply.isNullOrBlank()) reply
                    else "hm, I lost my train of thought for a sec 😅 say that again?",
                    isUser = false
                ))
            } catch (e: Exception) {
                val errorMsg = when {
                    e.message?.contains("API_KEY", ignoreCase = true) == true ||
                    e.message?.contains("invalid",  ignoreCase = true) == true ->
                        "Something's off with my connection — can you try again in a sec?"
                    e.message?.contains("network",  ignoreCase = true) == true ||
                    e is java.net.UnknownHostException ->
                        "📶 Seems like you're offline right now. Check your connection and try again."
                    else ->
                        "Hmm, hit a little snag on my end. Try again? 🙏"
                }
                messages.add(ChatMessage(errorMsg, isUser = false))
            }
            isTyping = false
        }
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {

        // ── TOP BAR ───────────────────────────────────────────────────────────
        Surface(
            color           = surface,
            shadowElevation = 4.dp,
            modifier        = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier         = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = onBg)
                }

                // MAX avatar
                Box(
                    modifier         = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF8C00)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("M",
                        color      = Color.White,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.ExtraBold)
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("MAX",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize   = 16.sp,
                        color      = onBg)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Live status dot
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isKeyMissing -> Color(0xFFFF5252)
                                        isTyping     -> Color(0xFFFF8C00)
                                        else         -> Color(0xFF4CAF50)
                                    }
                                )
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            when {
                                isTyping     -> "MAX is typing…"
                                isKeyMissing -> "Offline — API key missing"
                                else         -> "Online · CraveDash Specialist"
                            },
                            fontSize   = 11.sp,
                            color      = when {
                                isTyping     -> Color(0xFFFF8C00)
                                isKeyMissing -> Color(0xFFFF5252)
                                else         -> sub
                            }
                        )
                    }
                }

                // Menu
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, null, tint = onBg)
                    }
                    DropdownMenu(
                        expanded          = showMenu,
                        onDismissRequest  = { showMenu = false },
                        modifier          = Modifier.background(surface)
                    ) {
                        DropdownMenuItem(
                            text    = { Text("Clear chat", color = onBg) },
                            onClick = {
                                messages.clear()
                                messages.add(ChatMessage(maxOpeners.random(), isUser = false))
                                showMenu = false
                            }
                        )
                    }
                }
            }
        }

        // ── MESSAGES ──────────────────────────────────────────────────────────
        LazyColumn(
            state           = listState,
            modifier        = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding  = PaddingValues(top = 16.dp, bottom = 8.dp)
        ) {
            items(messages) { msg -> MaxBubble(msg, isDark, onBg, sub) }
            if (isTyping) { item { MaxTypingDots(isDark) } }
        }

        // ── INPUT AREA ────────────────────────────────────────────────────────
        Surface(
            color           = surface,
            shadowElevation = 16.dp,
            modifier        = Modifier.fillMaxWidth()
        ) {
            Column {
                // Quick reply chips
                val chips = listOf(
                    "What's good today? 🍛",
                    "Today's special 🔥",
                    "Recommend something",
                    "Track my order 📍",
                    "I'm feeling sad 😔"
                )
                LazyRow(
                    contentPadding      = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chips) { chip ->
                        SuggestionChip(
                            onClick = { if (!isTyping) sendMessage(chip) },
                            label   = { Text(chip, fontSize = 12.sp, color = onBg) },
                            colors  = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isDark) Color(0xFF2A2A2A) else Color(0xFFFFF3E0)
                            ),
                            border  = SuggestionChipDefaults.suggestionChipBorder(
                                enabled     = true,
                                borderColor = Color(0xFFFF8C00).copy(0.4f)
                            )
                        )
                    }
                }

                Row(
                    modifier         = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value           = userInput,
                        onValueChange   = { if (!isTyping) userInput = it },
                        placeholder     = {
                            Text("Message MAX…",
                                color    = sub,
                                fontSize = 14.sp)
                        },
                        modifier        = Modifier.weight(1f),
                        shape           = RoundedCornerShape(24.dp),
                        enabled         = !isTyping,
                        singleLine      = true,
                        textStyle       = TextStyle(color = onBg, fontSize = 15.sp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = { sendMessage(userInput) }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor      = Color(0xFFFF8C00),
                            unfocusedBorderColor    = if (isDark) Color(0xFF444444) else Color(0xFFE8E8E8),
                            focusedTextColor        = onBg,
                            unfocusedTextColor      = onBg,
                            cursorColor             = Color(0xFFFF8C00),
                            focusedContainerColor   = surface,
                            unfocusedContainerColor = if (isDark) Color(0xFF1E1E1E) else Color(0xFFFAFAFA),
                            disabledContainerColor  = if (isDark) Color(0xFF1A1A1A) else Color(0xFFF5F5F5),
                            disabledBorderColor     = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE)
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick        = { sendMessage(userInput) },
                        containerColor = if (isTyping || userInput.isBlank())
                            sub else Color(0xFFFF8C00),
                        contentColor   = Color.White,
                        shape          = CircleShape,
                        modifier       = Modifier.size(50.dp),
                        elevation      = FloatingActionButtonDefaults.elevation(0.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null,
                            modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Animated 3-dot typing indicator
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MaxTypingDots(isDark: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier          = Modifier.padding(start = 4.dp)
    ) {
        // MAX avatar
        Box(
            modifier         = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF8C00)),
            contentAlignment = Alignment.Center
        ) {
            Text("M",
                color      = Color.White,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
        Surface(
            color           = if (isDark) Color(0xFF2A2A2A) else Color.White,
            shape           = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier              = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                repeat(3) { i -> BounceDot(delayMs = i * 160) }
            }
        }
    }
}

@Composable
private fun BounceDot(delayMs: Int) {
    val inf = rememberInfiniteTransition(label = "d$delayMs")
    val y   by inf.animateFloat(
        0f, -7f,
        infiniteRepeatable(tween(420, delayMs, FastOutSlowInEasing), RepeatMode.Reverse),
        label = "b$delayMs"
    )
    Box(
        modifier = Modifier
            .size(9.dp)
            .offset(y = y.dp)
            .clip(CircleShape)
            .background(Color(0xFFFF8C00))
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Chat bubble
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MaxBubble(
    message: ChatMessage,
    isDark:  Boolean,
    onBg:    Color,
    sub:     Color
) {
    if (message.isUser) {
        // ── User bubble (right) ────────────────────────────────────────────
        Column(
            modifier              = Modifier.fillMaxWidth(),
            horizontalAlignment   = Alignment.End
        ) {
            Surface(
                color           = Color(0xFFFF8C00),
                shape           = RoundedCornerShape(18.dp, 18.dp, 2.dp, 18.dp),
                shadowElevation = 2.dp,
                modifier        = Modifier.widthIn(max = 290.dp)
            ) {
                Text(
                    message.text,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color    = Color.White,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
            Spacer(Modifier.height(3.dp))
            Text(message.timestamp, fontSize = 10.sp, color = sub)
        }
    } else {
        // ── MAX bubble (left) ──────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier          = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier         = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF8C00)),
                contentAlignment = Alignment.Center
            ) {
                Text("M",
                    color      = Color.White,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Surface(
                    color           = if (isDark) Color(0xFF252525) else Color.White,
                    shape           = RoundedCornerShape(18.dp, 18.dp, 18.dp, 2.dp),
                    shadowElevation = 2.dp,
                    modifier        = Modifier.widthIn(max = 290.dp)
                ) {
                    Text(
                        message.text,
                        modifier   = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        color      = onBg,
                        fontSize   = 15.sp,
                        lineHeight = 22.sp
                    )
                }
                Spacer(Modifier.height(3.dp))
                Text(message.timestamp, fontSize = 10.sp, color = sub)
            }
        }
    }
}
