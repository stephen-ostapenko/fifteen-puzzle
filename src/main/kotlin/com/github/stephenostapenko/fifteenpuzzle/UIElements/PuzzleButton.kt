package com.github.stephenostapenko.fifteenpuzzle.UIElements

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.stephenostapenko.fifteenpuzzle.MainPanel
import com.github.stephenostapenko.fifteenpuzzle.backend.PuzzleButtonImpl
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class PuzzleButton {
    companion object {
        @Composable
        fun puzzleButton(state: MainPanel.GameState,
                         button: PuzzleButtonImpl,
                         buttonList: List<List<PuzzleButtonImpl>>,
                         checkForSuccess: (List<List<PuzzleButtonImpl>>) -> Boolean)
        {
            button.updatePositionOnBoard()

            val onDragAction = action@{ change: PointerInputChange, dragAmount: Offset ->
                if (!button.active) {
                    return@action
                }

                state.setInProgress()
                button.select()

                var nextButtonXPos = button.getXPos() + dragAmount.x.roundToInt()
                var nextButtonYPos = button.getYPos() + dragAmount.y.roundToInt()
                nextButtonXPos = max(0, nextButtonXPos)
                nextButtonXPos = min(button.boardWidth - button.getWidth(), nextButtonXPos)
                nextButtonYPos = max(0, nextButtonYPos)
                nextButtonYPos = min(button.boardHeight - button.getHeight(), nextButtonYPos)

                button.setXPos(nextButtonXPos)
                button.setYPos(nextButtonYPos)
                change.consumeAllChanges()
            }

            val onDragEndAction = action@{
                button.deselect()

                val swapButton = button.findNearestButtonToCurrent(buttonList)
                if (swapButton.active) {
                    button.updatePositionOnBoard()
                    return@action
                }
                if (button.getManhattanDistOnGrid(swapButton) != 1) {
                    button.updatePositionOnBoard()
                    return@action
                }

                val halfButtonHeight = swapButton.getHeight() / 2
                val halfButtonWidth = swapButton.getWidth() / 2
                if (button.getXPos() !in
                    (swapButton.getXPos() - halfButtonWidth)..(swapButton.getXPos() + halfButtonWidth))
                {
                    button.updatePositionOnBoard()
                    return@action
                }
                if (button.getYPos() !in
                    (swapButton.getYPos() - halfButtonHeight)..(swapButton.getYPos() + halfButtonHeight))
                {
                    button.updatePositionOnBoard()
                    return@action
                }

                button.swapPositions(swapButton)
                button.updatePositionOnBoard()
                swapButton.updatePositionOnBoard()

                state.incTurnsCount()
                if (checkForSuccess(buttonList)) {
                    state.setFinished()
                }
            }

            Button(
                enabled = button.active,
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    if (button.checkSelected())
                        MaterialTheme.colors.secondary
                    else
                        MaterialTheme.colors.primary
                ),
                modifier = Modifier
                    .height(button.getHeight().dp)
                    .width(button.getWidth().dp)
                    .padding(5.dp)
                    .zIndex(button.getOrderIndex())
                    .offset {
                        IntOffset(button.getXPos(), button.getYPos())
                    }
                    .alpha(if (button.active) 1f else 0f)
                    .pointerInput(Unit) {
                        if (button.active) {
                            detectDragGestures(
                                onDrag = onDragAction,
                                onDragEnd = onDragEndAction
                            )
                        }
                    }
            ) {
                Text(
                    text = button.getLabel(),
                    fontSize = 24.sp
                )
            }
        }
    }
}