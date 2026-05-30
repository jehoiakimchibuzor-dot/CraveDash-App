package com.example.cravedash

import androidx.compose.runtime.mutableStateListOf

/**
 * Global favorites state — survives navigation changes.
 * MenuScreen writes here, FavoritesScreen reads here.
 * Using SnapshotStateList so every composable that reads it
 * recomposes automatically when items change.
 */
object FavoritesManager {

    val items = mutableStateListOf<MenuItem>()

    fun isFavorite(name: String): Boolean =
        items.any { it.name == name }

    /** Add if not present, remove if already present. */
    fun toggle(item: MenuItem) {
        val idx = items.indexOfFirst { it.name == item.name }
        if (idx != -1) items.removeAt(idx) else items.add(item)
    }

    fun remove(name: String) {
        items.removeAll { it.name == name }
    }

    fun clear() = items.clear()
}
