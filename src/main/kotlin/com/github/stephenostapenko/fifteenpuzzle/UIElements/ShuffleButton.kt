package com.github.stephenostapenko.fifteenpuzzle.UIElements

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.github.stephenostapenko.fifteenpuzzle.MainPanel
import com.github.stephenostapenko.fifteenpuzzle.backend.PuzzleButtonImpl
import com.github.stephenostapenko.fifteenpuzzle.backend.Utility

class ShuffleButton {
    companion object {
        @Composable
        fun shuffleButton(rowsNumber: Int, columnsNumber: Int,
                                  state: MainPanel.GameState, buttonList: List<List<PuzzleButtonImpl>>)
        {
            Button(
                enabled = (state.getState() in MainPanel.GameState.notProgressStates),
                onClick = {
                    state.setReady()
                    Utility.shuffleCells(rowsNumber, columnsNumber, buttonList)
                },
                modifier = Modifier.scale(1.25f).padding(15.dp)
            ) {
                Text(text = "Shuffle cells")
            }
        }
    }
}