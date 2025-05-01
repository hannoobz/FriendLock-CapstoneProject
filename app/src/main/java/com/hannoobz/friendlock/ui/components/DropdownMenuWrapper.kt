package com.hannoobz.friendlock.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun DropdownMenuWrapper(
    options: List<String>,
    selected: String,
    onSelectedChange: (String) -> Unit,
    modifier: Modifier,
) {
    val expanded = remember { mutableStateOf(false) }
    Box {
        Button(
            modifier = modifier,
            onClick = { expanded.value = true }) {
            Text("$selected")
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onSelectedChange(option)
                        expanded.value = false
                    },
                    text = { Text(option) }
                )
            }
        }
    }
}
