package com.hannoobz.friendlock.data

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apps")
data class AppEntity(
    @PrimaryKey val packageName: String,
    val name: String,
    val timeUsedMs: Long,
    val isChecked: Boolean,
)
