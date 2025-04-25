package com.hannoobz.friendlock.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.hannoobz.friendlock.data.AppEntity
import com.hannoobz.friendlock.ui.components.AppItem
import com.hannoobz.friendlock.ui.components.AppSearchBar
import com.hannoobz.friendlock.ui.components.FilterSortControls
import com.hannoobz.friendlock.ui.viewmodels.AppListViewModel


@Composable
fun AppList(
    navController: NavController,
    viewModel: AppListViewModel,
    apps: State<List<AppEntity>>,
    listState: LazyListState
) {
    val query = viewModel.query.collectAsState()
    val filterMode = viewModel.filterMode.collectAsState()
    val sortMode = viewModel.sortMode.collectAsState()
    val sortChanged = remember { mutableStateOf(false) }

    if(sortChanged.value){
        LaunchedEffect(true) {
            listState.scrollToItem(0)
            sortChanged.value = false
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AppSearchBar(query.value) {
            viewModel.updateQuery(it)
        }
        FilterSortControls(
            selectedFilter = filterMode.value,
            selectedSort = sortMode.value,
            onFilterChange = { viewModel.updateFilter(it) },
            onSortChange = {
                viewModel.updateSort(it)
                sortChanged.value = true },
        )
        LazyColumn(state = listState) {
            items(apps.value.size, key = { index -> apps.value[index].packageName }) { index ->
                val app = apps.value[index]
                AppItem(
                    app = app,
                    icon = viewModel.getAppIcon(app.packageName),
                    onCheckedChange = { viewModel.toggleChecked(app) }
                )
            }
        }
    }
}




fun formatMillis(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = (ms / (1000 * 60)) % 60
    val hours = (ms / (1000 * 60 * 60))
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
