package com.github.stephenostapenko.fifteenpuzzle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.unit.dp
import javax.swing.JComponent

class MainPanel {
    fun createPanel(): JComponent {
        return ComposePanel().apply {
            setContent {
                MaterialTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            getButtons()
                        }
                    }
                }
            }
        }
    }

    private data class ButtonContext(val label: MutableState<String>, val enabled: MutableState<Boolean>)

    private val contexts = (0..3).map { i ->
        (0..3).map { j ->
            ButtonContext(
                label = mutableStateOf((i * 4 + j + 1).toString()),
                enabled = mutableStateOf(i != 3 || j != 3)
            )
        }
    }

    private fun swapContexts(row1: Int, col1: Int, row2: Int, col2: Int) {
        contexts[row1][col1].label.value = contexts[row2][col2].label.value.also {
            contexts[row2][col2].label.value = contexts[row1][col1].label.value
        }
        contexts[row1][col1].enabled.value = contexts[row2][col2].enabled.value.also {
            contexts[row2][col2].enabled.value = contexts[row1][col1].enabled.value
        }
    }

    private fun getOnClickAction(row: Int, col: Int): (() -> Unit) {
        return {
            for ((rowDelta, colDelta) in listOf(
                Pair(-1, 0),
                Pair(0, 1),
                Pair(1, 0),
                Pair(0, -1)
            )) {
                if (row + rowDelta !in (0..3) || col + colDelta !in (0..3)) {
                    continue
                }
                if (!contexts[row + rowDelta][col + colDelta].enabled.value) {
                    swapContexts(row, col, row + rowDelta, col + colDelta)
                    break
                }
            }
        }
    }

    @Composable
    private fun getButtons() {
        for (row in 0..3) {
            Row {
                for (col in 0..3) {
                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = contexts[row][col].enabled.value,
                        onClick = getOnClickAction(row, col)
                    ) {
                        Text(text = contexts[row][col].label.value)
                    }
                }
            }
        }
    }
}