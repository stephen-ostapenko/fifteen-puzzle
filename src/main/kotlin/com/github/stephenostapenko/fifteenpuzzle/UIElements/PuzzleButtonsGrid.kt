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
import com.github.stephenostapenko.fifteenpuzzle.MainPanel
import com.github.stephenostapenko.fifteenpuzzle.backend.PuzzleButtonImpl

class PuzzleButtonsGrid {
    companion object {
        @Composable
        fun puzzleButtonsGrid(state: MainPanel.GameState,
                                      buttonList: List<List<PuzzleButtonImpl>>,
                                      checkForSuccess: (List<List<PuzzleButtonImpl>>) -> Boolean)
        {
            BoxWithConstraints(modifier = Modifier
                .fillMaxSize()
                .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(5.dp))
            ) {
                buttonList.forEach { rowList ->
                    rowList.forEach {
                        it.initButtonPositionOnBoard(constraints.maxHeight, constraints.maxWidth)
                    }
                }

                buttonList.forEach { rowList ->
                    rowList.forEach {
                        PuzzleButton.puzzleButton(state, it, buttonList, checkForSuccess)
                    }
                }
            }
        }
    }
}