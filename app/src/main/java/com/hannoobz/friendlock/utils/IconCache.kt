package com.hannoobz.friendlock.utils


import android.content.Context
import android.graphics.drawable.Drawable
import android.util.LruCache
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toDrawable

class IconCache {
    private val iconCache = LruCache<String, Drawable>(100)

    fun getIcon(context: Context, packageName: String): Drawable {
        val cachedIcon = iconCache.get(packageName)
        if (cachedIcon != null) {
            return cachedIcon
        }

        val icon = try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            AppCompatResources.getDrawable(context, android.R.drawable.sym_def_app_icon)
                ?: context.getDrawable(android.R.drawable.sym_def_app_icon)
                ?: Color.Gray.toArgb().toDrawable()
        }

        iconCache.put(packageName, icon)
        return icon
    }

    fun clearCache() {
        iconCache.evictAll()
    }
}