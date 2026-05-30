package com.example.cravedash

import androidx.compose.runtime.mutableStateListOf

/**
 * Global cart state — survives navigation changes.
 * FoodDetails writes here, CartScreen reads and mutates here.
 * Using SnapshotStateList so every composable recomposes automatically.
 */
object CartManager {

    val items = mutableStateListOf<CartItem>()

    // ── Derived values ────────────────────────────────────────────────────────
    val subtotal: Int get() = items.sumOf { it.price * it.quantity }
    val deliveryFee: Int = 500
    val total: Int get() = subtotal + deliveryFee
    val count: Int get() = items.sumOf { it.quantity }

    // ── Mutations ─────────────────────────────────────────────────────────────

    /** Add one unit of an item; increments quantity if already in cart. */
    fun add(name: String, price: Int, imageUrl: String = "") {
        val idx = items.indexOfFirst { it.name == name }
        if (idx != -1) {
            items[idx] = items[idx].copy(quantity = items[idx].quantity + 1)
        } else {
            items.add(CartItem(name, price, 1, imageUrl))
        }
    }

    fun increase(item: CartItem) {
        val idx = items.indexOf(item)
        if (idx != -1) items[idx] = item.copy(quantity = item.quantity + 1)
    }

    /** Decrease quantity; removes item if quantity reaches 0. */
    fun decrease(item: CartItem) {
        val idx = items.indexOf(item)
        if (idx != -1) {
            if (item.quantity > 1) items[idx] = item.copy(quantity = item.quantity - 1)
            else items.removeAt(idx)
        }
    }

    fun remove(item: CartItem) { items.remove(item) }

    fun clear() = items.clear()
}
