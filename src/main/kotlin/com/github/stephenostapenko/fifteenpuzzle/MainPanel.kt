package com.github.stephenostapenko.fifteenpuzzle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
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
import com.github.stephenostapenko.fifteenpuzzle.backend.PuzzleButton
import javax.swing.JComponent
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

const val SHUFFLE_ITERATIONS = 3

class MainPanel(private val rowsNumber: Int, private val columnsNumber: Int) {
    fun getJComponentPanel(): JComponent {
        return ComposePanel().apply {
            setContent {
                composePanel()
            }
        }
    }

    @Composable
    fun composePanel() {
        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    turnsCounter()
                    successLabel()
                    shuffleButton()
                    puzzleButtons()
                }
            }
        }
    }

    private var buttonList: List<PuzzleButton> = (0 until rowsNumber).map { row ->
        (0 until columnsNumber).map { col ->
            PuzzleButton("${row * columnsNumber + col + 1}", row, col, rowsNumber, columnsNumber)
        }
    }.flatten()

    @Composable
    private fun puzzleButtons() {
        BoxWithConstraints(modifier = Modifier
            .fillMaxSize()
            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(5.dp))
        ) {
            buttonList.forEach {
                it.boardHeight = constraints.maxHeight
                it.boardWidth = constraints.maxWidth
            }

            buttonList.forEach {
                puzzleButton(it)
            }
        }
    }

    @Composable
    private fun puzzleButton(button: PuzzleButton) {
        button.updatePos()

        val onDragAction = action@{ change: PointerInputChange, dragAmount: Offset ->
            if (!button.active) {
                return@action
            }

            gameFinishedState.value = false
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
                button.updatePos()
                return@action
            }
            if (button.getManhattanDistOnGrid(swapButton) != 1) {
                button.updatePos()
                return@action
            }

            val halfButtonHeight = swapButton.getHeight() / 2
            val halfButtonWidth = swapButton.getWidth() / 2
            if (button.getXPos() !in
                (swapButton.getXPos() - halfButtonWidth)..(swapButton.getXPos() + halfButtonWidth))
            {
                button.updatePos()
                return@action
            }
            if (button.getYPos() !in
                (swapButton.getYPos() - halfButtonHeight)..(swapButton.getYPos() + halfButtonHeight))
            {
                button.updatePos()
                return@action
            }

            button.swapPositions(swapButton)
            button.updatePos()
            swapButton.updatePos()

            turnsCount.value++
            if (checkForSuccess()) {
                gameFinishedState.value = true
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
                .zIndex(if (button.checkSelected()) 1f else 0f)
                .offset {
                    IntOffset(button.getXPos(), button.getYPos())
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = onDragAction,
                        onDragEnd = onDragEndAction
                    )
                }
        ) {
            Text(
                text = button.label,
                fontSize = 24.sp
            )
        }
    }

    @Composable
    fun turnsCounter() {
        Text(
            text = "${turnsCount.value} turn" + if (turnsCount.value != 1) "s" else "",
            modifier = Modifier.scale(2f).padding(top = 20.dp, bottom = 15.dp)
        )
    }

    @Composable
    fun successLabel() {
        Text(
            text = if (gameFinishedState.value) "Success!" else "",
            modifier = Modifier.scale(2f).padding(20.dp)
        )
    }

    @Composable
    fun shuffleButton() {
        Button(
            enabled = gameFinishedState.value || gameInitState.value,
            onClick = {
                //shuffleCells()
            },
            modifier = Modifier.scale(1.25f).padding(15.dp)
        ) {
            Text(text = "Shuffle cells")
        }
    }

    private var gameFinishedState = mutableStateOf(false)
    private var gameInitState = mutableStateOf(true)
    private var turnsCount = mutableStateOf(0)

    private fun checkForSuccess(): Boolean {
        return buttonList.all { it.row == it.initRow && it.col == it.initCol }
    }

    /*private fun getOnClickActionForPuzzleButton(row: Int, col: Int): (() -> Unit) {
        return {
            var turnCompleted = false
            for ((rowDelta, colDelta) in listOf(
                Pair(-1, 0),
                Pair(0, 1),
                Pair(1, 0),
                Pair(0, -1)
            )) {
                if (row + rowDelta !in (0 until rowsNumber) ||
                    col + colDelta !in (0 until columnsNumber)) {
                    continue
                }
                if (!buttonContexts[row + rowDelta][col + colDelta].enabled.value) {
                    swapContexts(row, col, row + rowDelta, col + colDelta)
                    turnCompleted = true
                    break
                }
            }

            if (turnCompleted) {
                turnsCount.value = if (gameInitState.value) 1 else turnsCount.value + 1
                gameInitState.value = false
            }

            updateGameStates()
        }
    }*/

    /*private fun updateGameStates() {
        gameFinishedState.value = checkForSuccess()
        if (gameFinishedState.value) {
            gameInitState.value = true
        }
    }*/

    /*private fun shuffleCells() {
        val cells = (0 until rowsNumber).map { row ->
            (0 until columnsNumber).map { col ->
                row * columnsNumber + col + 1
            }.toMutableList()
        }

        var curRow = rowsNumber - 1
        var curCol = columnsNumber - 1
        val deltas = listOf(
            Pair(-1, 0),
            Pair(0, 1),
            Pair(1, 0),
            Pair(0, -1)
        )

        for (it in 0 until SHUFFLE_ITERATIONS) {
            var (rowDelta, colDelta) = deltas.random()
            while (curRow + rowDelta !in (0 until rowsNumber) ||
                curCol + colDelta !in (0 until columnsNumber)) {
                val (nextRowDelta, nextColDelta) = deltas.random()
                rowDelta = nextRowDelta
                colDelta = nextColDelta
            }

            cells[curRow][curCol] = cells[curRow + rowDelta][curCol + colDelta].also {
                cells[curRow + rowDelta][curCol + colDelta] = cells[curRow][curCol]
            }
            curRow += rowDelta
            curCol += colDelta
        }

        gameFinishedState.value = false
        turnsCount.value = 0
        for (row in 0 until rowsNumber) {
            for (col in 0 until columnsNumber) {
                buttonContexts[row][col].label.value = cells[row][col].toString()
                buttonContexts[row][col].enabled.value = !(row == curRow && col == curCol)
            }
        }
    }*/
}