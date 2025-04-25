package com.hannoobz.friendlock.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appEntity: AppEntity)

    @Query("SELECT * FROM apps")
    suspend fun getAllApps(): List<AppEntity>

    @Query("SELECT * FROM apps WHERE isChecked = 1")
    suspend fun getCheckedApps(): List<AppEntity>

    @Query("SELECT * FROM apps WHERE isChecked = 0")
    suspend fun getUncheckedApps(): List<AppEntity>

    @Query("SELECT COUNT(*) FROM apps")
    suspend fun getCount(): Int
}
