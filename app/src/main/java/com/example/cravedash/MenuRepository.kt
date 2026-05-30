package com.example.cravedash

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import org.json.JSONArray
import org.json.JSONObject

// ─────────────────────────────────────────────────────────────────────────────
// Helper
// ─────────────────────────────────────────────────────────────────────────────
fun FoodItem.toMenuItem() = MenuItem(name, price, description, imageUrl)

// ─────────────────────────────────────────────────────────────────────────────
// Category → hero image map (used by Dashboard category cards)
// ─────────────────────────────────────────────────────────────────────────────
val categoryImages = mapOf(
    "Meals"    to "https://images.unsplash.com/photo-1627308595229-7830a5c91f9f?w=400",
    "Grills"   to "https://images.unsplash.com/photo-1529193591184-b1d58069ecdd?w=400",
    "Sides"    to "https://images.unsplash.com/photo-1511690656952-34342bb7c2f2?w=400",
    "Desserts" to "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400",
    "Drinks"   to "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=400"
)

// ─────────────────────────────────────────────────────────────────────────────
// Default menu — 5 premium categories, culturally accurate, proper descriptions
// ─────────────────────────────────────────────────────────────────────────────
internal val defaultMenuItems = listOf(

    // ════════════════════════════════════════════════════════════════
    // MEALS — main dishes, Nigerian classics + international
    // ════════════════════════════════════════════════════════════════
    FoodItem("Jollof Rice", "₦2,500", "Meals",
        "Award-winning smoky party jollof cooked with tomato-pepper base, bay leaves, and secret seasoning. The undisputed king of Nigerian cooking.",
        "https://images.unsplash.com/photo-1627308595229-7830a5c91f9f?w=600"),

    FoodItem("Pounded Yam & Egusi", "₦3,500", "Meals",
        "Silky smooth pounded yam paired with rich egusi soup loaded with stockfish, dried shrimp, uziza leaves, and assorted meat.",
        "https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=600"),

    FoodItem("Eba & Ogbono Soup", "₦3,000", "Meals",
        "Golden garri eba served with thick draw-draw ogbono soup cooked in palm oil with dried fish and assorted protein.",
        "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=600"),

    FoodItem("Egusi Soup", "₦2,800", "Meals",
        "Rich ground melon seed soup cooked in palm oil with uziza, stockfish, crayfish, and assorted meat. A true Nigerian classic.",
        "https://images.unsplash.com/photo-1517244683847-7456b63c5969?w=600"),

    FoodItem("Ofada Rice & Ayamase", "₦3,000", "Meals",
        "Locally grown unpolished brown rice served with the legendary green pepper ayamase stew — offals, palm oil, and assorted peppers.",
        "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=600"),

    FoodItem("Efo Riro", "₦2,800", "Meals",
        "Velvety Yoruba spinach stew cooked with assorted protein, palm oil, crayfish, and red bell peppers. Lagos Sunday essential.",
        "https://images.unsplash.com/photo-1547592180-85f173990554?w=600"),

    FoodItem("Pepper Soup (Assorted)", "₦2,800", "Meals",
        "Fiery aromatic broth with goat meat and catfish, perfumed with utazi, scent leaves, uziza, and native spices. The ultimate Nigerian comfort.",
        "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=600"),

    FoodItem("Afang Soup", "₦3,500", "Meals",
        "Prestigious Efik delicacy from afang leaves and waterleaf, with periwinkle, dried fish, assorted meat, and palm oil. A celebration bowl.",
        "https://images.unsplash.com/photo-1567364816519-cbc9c4ffe5ad?w=600"),

    FoodItem("Ofe Akwu (Banga Soup)", "₦3,200", "Meals",
        "Rich Delta banga soup extracted from fresh palm fruits, cooked with dried fish, oburunbebe stick, and indigenous spices.",
        "https://images.unsplash.com/photo-1518779578993-ec3579fee39f?w=600"),

    FoodItem("Party Fried Rice", "₦2,500", "Meals",
        "Premium fried rice with carrots, peas, green onions, chicken liver, prawns, and perfectly seasoned egg. Party approved.",
        "https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=600"),

    FoodItem("Beef Shawarma", "₦2,200", "Meals",
        "Slow-roasted, heavily seasoned beef in soft flatbread with garlic sauce, coleslaw, tomatoes, and house hot sauce.",
        "https://images.unsplash.com/photo-1561043433-aaf687c4cf04?w=600"),

    FoodItem("Beef Burger", "₦2,500", "Meals",
        "Double smash patty with cheddar, caramelised onions, lettuce, tomato, pickles, and CraveDash signature smoky sauce.",
        "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=600"),

    FoodItem("Grilled Salmon", "₦5,000", "Meals",
        "Atlantic salmon fillet pan-seared in garlic herb butter with lemon, capers, and seasonal greens. Simply premium.",
        "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=600"),

    FoodItem("Pasta Carbonara", "₦1,800", "Meals",
        "Authentic Italian carbonara — silk-smooth egg yolk sauce with guanciale, pecorino romano, and freshly cracked pepper.",
        "https://images.unsplash.com/photo-1612874742237-6526221588e3?w=600"),

    FoodItem("Margherita Pizza", "₦4,500", "Meals",
        "Neapolitan-style thin crust with San Marzano tomato, fresh mozzarella, basil oil, and a perfect char from the stone oven.",
        "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=600"),

    FoodItem("Salmon Sushi Platter", "₦6,000", "Meals",
        "8-piece premium sushi selection with fresh salmon nigiri, avocado maki, and cucumber rolls. Served with wasabi and pickled ginger.",
        "https://images.unsplash.com/photo-1559410545-0bdcd187e0a6?w=600"),

    // ════════════════════════════════════════════════════════════════
    // GRILLS — Nigeria's premium grilled and peppered dishes
    //          These are NOT snacks. They are serious, celebrated food.
    // ════════════════════════════════════════════════════════════════
    FoodItem("Beef Suya", "₦2,000", "Grills",
        "Thin-sliced premium beef skewers coated in house yaji spice blend — groundnuts, ginger, paprika — char-grilled over live coals. A Nigerian institution.",
        "https://images.unsplash.com/photo-1529193591184-b1d58069ecdd?w=600"),

    FoodItem("Chicken Suya", "₦2,200", "Grills",
        "Boneless chicken thigh marinated overnight in yaji paste and char-grilled to a perfect caramelised crust. Served with raw onion and tomato.",
        "https://images.unsplash.com/photo-1532550907401-a500c9a57435?w=600"),

    FoodItem("Asun", "₦3,000", "Grills",
        "Expertly smoked and peppered goat meat — Lagos' most celebrated delicacy. Intensely smoky, fiery, deeply satisfying. The king of Nigerian party food.",
        "https://images.unsplash.com/photo-1544025162-d76694265947?w=600"),

    FoodItem("Nkwobi", "₦3,500", "Grills",
        "Igbo luxury dish: cow foot slow-cooked until tender in spiced palm kernel paste with utazi leaves and native spices. True celebration food.",
        "https://images.unsplash.com/photo-1546549032-9571cd6b27df?w=600"),

    FoodItem("Peppered Snail", "₦3,800", "Grills",
        "Giant African land snails in an intensely spiced tomato-pepper sauce. A highly prized Lagos delicacy — rich, unique, unforgettable.",
        "https://images.unsplash.com/photo-1565299507177-b0ac66763828?w=600"),

    FoodItem("Grilled Tilapia", "₦3,000", "Grills",
        "Whole fresh tilapia marinated in shito pepper paste and grilled over open flame with onions, scotch bonnets, and citrus herbs.",
        "https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=600"),

    FoodItem("Peppered Chicken", "₦2,800", "Grills",
        "Juicy fried chicken pieces tossed in a fiery blended tomato-pepper sauce with onions, green peppers, and curry leaves.",
        "https://images.unsplash.com/photo-1562967914-608f82629710?w=600"),

    FoodItem("BBQ Pork Ribs", "₦4,500", "Grills",
        "Slow-smoked baby back ribs glazed in house BBQ sauce with hickory notes, brown sugar, and chilli. Falling off the bone.",
        "https://images.unsplash.com/photo-1544025162-d76694265947?w=600"),

    FoodItem("Ishiewu", "₦3,200", "Grills",
        "Spiced goat head meat — a traditional favourite slow-cooked in aromatic ugba (oil bean), palm oil, and native spices.",
        "https://images.unsplash.com/photo-1529193591184-b1d58069ecdd?w=600"),

    // ════════════════════════════════════════════════════════════════
    // SIDES — accompaniments and small plates
    // ════════════════════════════════════════════════════════════════
    FoodItem("Fried Plantain (Dodo)", "₦500", "Sides",
        "Ripe plantain slices fried to golden caramelised perfection. The universal pairing for every Nigerian meal. Never gets old.",
        "https://images.unsplash.com/photo-1511690656952-34342bb7c2f2?w=600"),

    FoodItem("Moi Moi", "₦800", "Sides",
        "Steamed black-eyed pea pudding with fish, boiled egg, and crayfish. A Nigerian breakfast staple and beloved side dish.",
        "https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=600"),

    FoodItem("Akara", "₦600", "Sides",
        "Crispy, freshly fried bean fritters made from blended honey beans with onion and scotch bonnet. Best eaten hot.",
        "https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=600"),

    FoodItem("Gizdodo", "₦1,200", "Sides",
        "Perfectly sautéed gizzard and ripe dodo in tangy tomato and green pepper sauce. A Lagos party platter icon.",
        "https://images.unsplash.com/photo-1544025162-d76694265947?w=600"),

    FoodItem("Fried Yam Chips", "₦600", "Sides",
        "Thick-cut yam fingers deep-fried until perfectly crispy outside, fluffy inside. Served with ata din din dipping sauce.",
        "https://images.unsplash.com/photo-1576107232684-1279f8c7bc5b?w=600"),

    FoodItem("Coleslaw", "₦400", "Sides",
        "Crunchy shredded cabbage and carrots in light mayo dressing with a hint of sweetness. The perfect fresh contrast.",
        "https://images.unsplash.com/photo-1572449043416-55f4685c9bb7?w=600"),

    FoodItem("Garlic Bread", "₦700", "Sides",
        "Thick-cut sourdough toasted with compound garlic herb butter, parmesan, and fresh parsley. Indulgent and warming.",
        "https://images.unsplash.com/photo-1619535860434-ba1d8fa12536?w=600"),

    FoodItem("French Fries", "₦500", "Sides",
        "Double-fried golden potato fries, seasoned with house spice blend. Crispy outside, fluffy within.",
        "https://images.unsplash.com/photo-1576107232684-1279f8c7bc5b?w=600"),

    // ════════════════════════════════════════════════════════════════
    // DESSERTS — sweets, pastries, and indulgences
    // ════════════════════════════════════════════════════════════════
    FoodItem("Puff Puff", "₦400", "Desserts",
        "Fluffy, pillowy deep-fried Nigerian dough balls dusted with powdered sugar and cinnamon. Pure nostalgia in every bite.",
        "https://images.unsplash.com/photo-1551024601-bec78aea704b?w=600"),

    FoodItem("Chin Chin (3 Flavours)", "₦500", "Desserts",
        "Handmade crunchy fried pastry bites in classic, coconut, and spiced flavours. Nigeria's most addictive snack, done properly.",
        "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=600"),

    FoodItem("Boli & Groundnut", "₦600", "Desserts",
        "Charcoal-roasted ripe plantain served with salted roasted groundnuts and palm oil dip. The Lagos roadside experience, elevated.",
        "https://images.unsplash.com/photo-1511690656952-34342bb7c2f2?w=600"),

    FoodItem("Chocolate Lava Cake", "₦1,400", "Desserts",
        "Warm dark chocolate fondant with a molten Valrhona centre, dusted with cocoa, served with vanilla bean ice cream.",
        "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=600"),

    FoodItem("Tiramisu", "₦1,400", "Desserts",
        "Classic Italian dessert: espresso-dipped savoiardi, whipped mascarpone cream, and a generous dusting of premium cocoa.",
        "https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=600"),

    FoodItem("Churros & Chocolate Sauce", "₦900", "Desserts",
        "Crispy golden churros rolled in cinnamon sugar, served with rich Belgian dark chocolate dipping sauce.",
        "https://images.unsplash.com/photo-1624353365286-3f8d62daad51?w=600"),

    FoodItem("Vanilla Bean Ice Cream", "₦700", "Desserts",
        "3 generous scoops of premium Madagascar vanilla bean ice cream with waffle cone crumble and fresh berries.",
        "https://images.unsplash.com/photo-1488900128323-21503983a07e?w=600"),

    FoodItem("New York Cheesecake", "₦1,200", "Desserts",
        "Dense, creamy baked cheesecake on a graham cracker base with seasonal berry compote. A timeless classic.",
        "https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=600"),

    // ════════════════════════════════════════════════════════════════
    // DRINKS — Nigerian classics and international beverages
    // ════════════════════════════════════════════════════════════════
    FoodItem("Zobo (Hibiscus)", "₦500", "Drinks",
        "Chilled Nigerian hibiscus drink brewed with ginger, cloves, pineapple, and cinnamon. Deep red, naturally refreshing.",
        "https://images.unsplash.com/photo-1595981267035-7b04ca84a82d?w=600"),

    FoodItem("Kunu", "₦400", "Drinks",
        "Traditional northern Nigerian millet drink spiced with ginger, cloves, and cinnamon. Thick, nourishing, and deeply satisfying.",
        "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=600"),

    FoodItem("Chapman", "₦1,200", "Drinks",
        "Nigeria's iconic party mocktail — Fanta, Sprite, Grenadine, cucumber, angostura bitters, and a cherry on top. The Lagos good-time drink.",
        "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=600"),

    FoodItem("Watermelon Smoothie", "₦1,000", "Drinks",
        "Fresh watermelon cold-blended with mint leaves, lime juice, and a touch of honey. The perfect Lagos heat remedy.",
        "https://images.unsplash.com/photo-1502741224143-90386d7f8c82?w=600"),

    FoodItem("Mango Lassi", "₦1,000", "Drinks",
        "Alphonso mango blended with creamy yogurt, cardamom, and a drizzle of honey. Thick, exotic, deeply refreshing.",
        "https://images.unsplash.com/photo-1571090254539-63d5b1fcbebe?w=600"),

    FoodItem("Milkshake", "₦1,500", "Drinks",
        "Hand-spun vanilla or chocolate milkshake with premium ice cream, whipped cream, and a cherry. Thick enough to need a spoon.",
        "https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=600"),

    FoodItem("Freshly Pressed Juice", "₦800", "Drinks",
        "Cold-pressed seasonal fruit blend — orange, carrot, ginger, or watermelon. Ask your server for today's selection.",
        "https://images.unsplash.com/photo-1613478223719-2ab802602423?w=600"),

    FoodItem("Maltina", "₦500", "Drinks",
        "Nigeria's premium malt drink. Sweet, rich, and energising — the perfect non-alcoholic celebration drink.",
        "https://images.unsplash.com/photo-1624517452488-04869289c4ca?w=600"),

    FoodItem("Coca-Cola", "₦300", "Drinks",
        "Ice-cold classic Coca-Cola. Always refreshing, always perfect with Nigerian food.",
        "https://images.unsplash.com/photo-1554866585-cd94860890b7?w=600"),

    FoodItem("Still Water", "₦200", "Drinks",
        "Chilled 75cl bottle of premium still spring water.",
        "https://images.unsplash.com/photo-1548839140-29a749e1cf4d?w=600")
)

// ─────────────────────────────────────────────────────────────────────────────
// MenuRepository — single source of truth, persisted via SharedPreferences
// ─────────────────────────────────────────────────────────────────────────────
object MenuRepository {

    private const val PREFS = "cd_menu_v3"
    private const val KEY   = "items_json"

    val items = mutableStateListOf<FoodItem>().apply { addAll(defaultMenuItems) }

    fun init(context: Context) {
        val json = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, null) ?: return
        try {
            val array = JSONArray(json)
            items.clear()
            for (i in 0 until array.length()) {
                val o = array.getJSONObject(i)
                items.add(FoodItem(
                    name        = o.getString("name"),
                    price       = o.getString("price"),
                    category    = o.getString("category"),
                    description = o.optString("description", ""),
                    imageUrl    = o.optString("imageUrl", "")
                ))
            }
        } catch (_: Exception) { /* keep defaults on parse error */ }
    }

    fun add(item: FoodItem, context: Context)                       { items.add(item);         persist(context) }
    fun update(old: FoodItem, new: FoodItem, context: Context)      { val i = items.indexOf(old); if (i != -1) { items[i] = new; persist(context) } }
    fun delete(item: FoodItem, context: Context)                    { items.remove(item);      persist(context) }
    fun resetToDefaults(context: Context)                           { items.clear(); items.addAll(defaultMenuItems); context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply() }

    private fun persist(context: Context) {
        val array = JSONArray()
        items.forEach { item ->
            array.put(JSONObject().apply {
                put("name", item.name); put("price", item.price)
                put("category", item.category); put("description", item.description)
                put("imageUrl", item.imageUrl)
            })
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, array.toString()).apply()
    }

    fun byCategory(category: String): List<FoodItem> = items.filter { it.category == category }

    /** Live categories derived from the current item list */
    val categories: List<String> get() = items.map { it.category }.distinct()
        .sortedBy { listOf("Meals","Grills","Sides","Desserts","Drinks").indexOf(it).takeIf { i -> i >= 0 } ?: 99 }

    /** Top 6 meals for Dashboard "Popular Now" */
    val popular: List<FoodItem> get() = items.filter { it.category == "Meals" }.take(6)
}
