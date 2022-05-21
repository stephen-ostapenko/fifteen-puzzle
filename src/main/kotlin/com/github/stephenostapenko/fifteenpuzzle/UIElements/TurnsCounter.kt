package com.github.stephenostapenko.fifteenpuzzle.UIElements

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.github.stephenostapenko.fifteenpuzzle.backend.MainPanel

class TurnsCounter {
    companion object {
        @Composable
        fun drawTurnsCounter(state: MainPanel.GameState) {
            val turnsCount = state.getTurnsCount()
            Text(
                text = "$turnsCount turn" + if (turnsCount != 1) "s" else "",
                modifier = Modifier.scale(2f).padding(top = 20.dp, bottom = 15.dp)
            )
        }
    }
}