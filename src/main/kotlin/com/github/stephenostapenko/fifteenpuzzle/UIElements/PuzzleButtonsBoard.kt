package com.github.stephenostapenko.fifteenpuzzle.UIElements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.stephenostapenko.fifteenpuzzle.backend.MainPanel
import com.github.stephenostapenko.fifteenpuzzle.backend.PuzzleGrid

class PuzzleButtonsBoard {
    companion object {
        @Composable
        fun drawPuzzleButtonsBoard(state: MainPanel.GameState, grid: PuzzleGrid) {
            BoxWithConstraints(modifier = Modifier
                .fillMaxSize()
                .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(5.dp))
            ) {
                grid.initGrid(constraints.maxHeight, constraints.maxWidth)
                grid.buttonList.forEach {
                    PuzzleButton.drawPuzzleButton(state, it, grid)
                }
            }
        }
    }
}