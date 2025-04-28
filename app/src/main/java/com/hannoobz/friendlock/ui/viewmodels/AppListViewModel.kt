package com.hannoobz.friendlock.ui.viewmodels

import android.app.Application
import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hannoobz.friendlock.data.AppEntity
import com.hannoobz.friendlock.data.DBProvider
import com.hannoobz.friendlock.utils.IconCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AppListViewModel(application: Application) : AndroidViewModel(application) {

    private val iconCache = IconCache()
    private val dao = DBProvider.getDatabase(application).appDao()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _filterMode = MutableStateFlow("All")
    val filterMode = _filterMode.asStateFlow()

    private val _sortMode = MutableStateFlow("Time Used")
    val sortMode = _sortMode.asStateFlow()

    private val _allApps = MutableStateFlow<List<AppEntity>>(emptyList())

    val appList: StateFlow<List<AppEntity>> = combine(
        _allApps,
        _query,
        _filterMode,
        _sortMode
    ) { apps, query, filter, sort ->
        val filtered = when (filter) {
            "Blocked" -> apps.filter { it.isChecked }
            "Not Blocked" -> apps.filter { !it.isChecked }
            else -> apps
        }

        val searched = if (query.isBlank()) {
            filtered
        } else {
            filtered.filter { it.name.contains(query, ignoreCase = true) }
        }

        when (sort) {
            "Alphabetically" -> searched.sortedBy { it.name.lowercase() }
            else -> searched.sortedByDescending { it.timeUsedMs }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _preloadedIcons = mutableMapOf<String, ImageBitmap>()

    fun loadApps(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            updateAppsFromUsageStats(context)
            _allApps.value = dao.getAllApps()
            Log.d("TEST", "THIS IS TEST ${_allApps.value}")
        }
    }

    private suspend fun updateAppsFromUsageStats(context: Context) {
        val usageStats = getUsageStats(context)
        val existingApps = dao.getAllApps().associateBy { it.packageName }
        usageStats.forEach { stats ->
            val existingApp = existingApps[stats.packageName]
            val app = AppEntity(
                packageName = stats.packageName,
                name = stats.name,
                timeUsedMs = stats.timeUsedMs,
                isChecked = existingApp?.isChecked ?: false
            )
            dao.insert(app)
        }
    }

    fun getAppIcon(packageName: String): ImageBitmap? {
        return _preloadedIcons[packageName]
    }

    fun preloadIcons(context: Context, visiblePackageNames: List<String>) {
        viewModelScope.launch(Dispatchers.Default) {
            visiblePackageNames.forEach { packageName ->
                if (!_preloadedIcons.containsKey(packageName)) {
                    try {
                        val drawable = iconCache.getIcon(context, packageName)
                        val bitmap = drawable.toBitmap().asImageBitmap()
                        _preloadedIcons[packageName] = bitmap
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    fun toggleChecked(app: AppEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = app.copy(isChecked = !app.isChecked)
            dao.insert(updated)

            _allApps.update { currentList ->
                currentList.map {
                    if (it.packageName == app.packageName) updated else it
                }
            }
        }
    }

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun updateFilter(filter: String) {
        _filterMode.value = filter
    }

    fun updateSort(sort: String) {
        _sortMode.value = sort
    }

    private fun getUsageStats(context: Context): List<AppStats> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)

        val packageManager = context.packageManager
        val nameCache = mutableMapOf<String, String>()

        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_WEEKLY,
            calendar.timeInMillis,
            System.currentTimeMillis()
        )

        return usageStats.filter { it.totalTimeInForeground > 0 }
            .map { stats ->
                val appName = nameCache.getOrPut(stats.packageName) {
                    try {
                        packageManager.getApplicationLabel(
                            packageManager.getApplicationInfo(stats.packageName, 0)
                        ).toString()
                    } catch (e: Exception) {
                        stats.packageName.split(".").last()
                    }
                }

                AppStats(
                    packageName = stats.packageName,
                    name = appName,
                    timeUsedMs = stats.totalTimeInForeground
                )
            }
    }

    data class AppStats(val packageName: String, val name: String, val timeUsedMs: Long)
}
