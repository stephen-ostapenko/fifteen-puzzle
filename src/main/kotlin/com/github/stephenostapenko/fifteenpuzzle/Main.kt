package com.github.stephenostapenko.fifteenpuzzle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Fifteen Puzzle",
        state = rememberWindowState(width = 480.dp, height = 720.dp)
    ) {
        MainPanel(4, 4).composePanel()
    }
}