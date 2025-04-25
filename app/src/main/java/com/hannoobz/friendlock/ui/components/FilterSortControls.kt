package com.hannoobz.friendlock.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterSortControls(
    selectedFilter: String,
    selectedSort: String,
    onFilterChange: (String) -> Unit,
    onSortChange: (String) -> Unit,
) {
    Row(modifier = Modifier.padding(8.dp)) {
        DropdownMenuWrapper(
            options = listOf("All", "Blocked", "Not Blocked"),
            selected = selectedFilter,
            onSelectedChange = onFilterChange,
            modifier = Modifier.width(160.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        DropdownMenuWrapper(
            options = listOf("Time Used", "Alphabetically"),
            selected = selectedSort,
            onSelectedChange = onSortChange,
            modifier = Modifier.width(160.dp),
        )
    }
}
