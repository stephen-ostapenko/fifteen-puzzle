package com.github.stephenostapenko.fifteenpuzzle.UIElements

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.github.stephenostapenko.fifteenpuzzle.MainPanel

class SuccessLabel {
    companion object {
        @Composable
        fun successLabel(state: MainPanel.GameState) {
            Text(
                text = if (state.getState() == MainPanel.GameState.ProcessState.Finished) "Success!" else "",
                modifier = Modifier.scale(2f).padding(20.dp)
            )
        }
    }
}