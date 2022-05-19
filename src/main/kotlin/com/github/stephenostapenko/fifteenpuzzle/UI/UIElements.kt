package com.github.stephenostapenko.fifteenpuzzle.UI

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.github.stephenostapenko.fifteenpuzzle.MainPanel.GameState
import com.github.stephenostapenko.fifteenpuzzle.backend.PuzzleButton
import com.github.stephenostapenko.fifteenpuzzle.backend.Utility
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class UIElements {
    companion object {
        @Composable
        fun composePanel(rowsNumber: Int, columnsNumber: Int,
                         state: GameState,
                         buttonList: List<List<PuzzleButton>>,
                         checkForSuccess: (List<List<PuzzleButton>>) -> Boolean)
        {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        turnsCounter(state)
                        successLabel(state)
                        shuffleButton(rowsNumber, columnsNumber, state, buttonList)
                        puzzleButtons(state, buttonList, checkForSuccess)
                    }
                }
            }
        }

        @Composable
        private fun puzzleButtons(state: GameState,
                                  buttonList: List<List<PuzzleButton>>,
                                  checkForSuccess: (List<List<PuzzleButton>>) -> Boolean)
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
                        puzzleButton(state, it, buttonList, checkForSuccess)
                    }
                }
            }
        }

        @Composable
        private fun puzzleButton(state: GameState,
                         button: PuzzleButton,
                         buttonList: List<List<PuzzleButton>>,
                         checkForSuccess: (List<List<PuzzleButton>>) -> Boolean)
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
                        detectDragGestures(
                            onDrag = onDragAction,
                            onDragEnd = onDragEndAction
                        )
                    }
            ) {
                Text(
                    text = button.getLabel(),
                    fontSize = 24.sp
                )
            }
        }

        @Composable
        private fun turnsCounter(state: GameState) {
            val turnsCount = state.getTurnsCount()
            Text(
                text = "$turnsCount turn" + if (turnsCount != 1) "s" else "",
                modifier = Modifier.scale(2f).padding(top = 20.dp, bottom = 15.dp)
            )
        }

        @Composable
        private fun successLabel(state: GameState) {
            Text(
                text = if (state.getState() == GameState.ProcessState.Finished) "Success!" else "",
                modifier = Modifier.scale(2f).padding(20.dp)
            )
        }

        @Composable
        private fun shuffleButton(rowsNumber: Int, columnsNumber: Int,
                                  state: GameState, buttonList: List<List<PuzzleButton>>)
        {
            Button(
                enabled = (state.getState() in GameState.notProgressStates),
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