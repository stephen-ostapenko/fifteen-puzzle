package com.github.stephenostapenko.fifteenpuzzle

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Fifteen Puzzle",
        icon = painterResource("/icons/pluginIcon.svg"),
        state = rememberWindowState(width = 480.dp, height = 720.dp)
    ) {
        MainPanel(4, 4).getComposePanel()
    }
}