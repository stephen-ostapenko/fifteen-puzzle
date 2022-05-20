package com.github.stephenostapenko.fifteenpuzzle.UIElements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.stephenostapenko.fifteenpuzzle.backend.MainPanel
import com.github.stephenostapenko.fifteenpuzzle.backend.PuzzleButtonImpl
import com.github.stephenostapenko.fifteenpuzzle.backend.PuzzleGrid

class MainInterfaceComposePanel {
    companion object {
        @Composable
        fun mainInterfaceComposePanel(rowsNumber: Int, columnsNumber: Int,
                                      state: MainPanel.GameState, grid: PuzzleGrid)
        {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        TurnsCounter.turnsCounter(state)
                        SuccessLabel.successLabel(state)
                        ShuffleButton.shuffleButton(rowsNumber, columnsNumber, state, grid)
                        PuzzleButtonsBoard.puzzleButtonsBoard(state, grid)
                    }
                }
            }
        }
    }
}