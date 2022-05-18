package com.github.stephenostapenko.fifteenpuzzle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import javax.swing.JComponent
import kotlin.math.pow
import kotlin.math.roundToInt

const val SHUFFLE_ITERATIONS = 3

class PuzzleButton(val label: String,
                   initRow: Int, initCol: Int,
                   private val rowsNumber: Int, private val columnsNumber: Int)
{
    private var row = initRow
    private var col = initCol
    val xPos = mutableStateOf(0)
    val yPos = mutableStateOf(0)
    val active = !(row + 1 == rowsNumber && col + 1 == columnsNumber)

    fun resetPos(height: Int, width: Int) {
        xPos.value = (getScaledXPos() * width).roundToInt()
        yPos.value = (getScaledYPos() * height).roundToInt()
    }

    fun getRow(): Int {
        return row
    }

    fun getCol(): Int {
        return col
    }

    fun getScaledHeight(): Double {
        return 1.0 / columnsNumber
    }

    fun getScaledWidth(): Double {
        return 1.0 / rowsNumber
    }

    fun getScaledXPos(): Double {
        return getScaledHeight() * row
    }

    fun getScaledYPos(): Double {
        return getScaledWidth() * col
    }

    fun swapPositions(button: PuzzleButton) {
        row = button.row.also { button.row = row }
        col = button.col.also { button.col = col }
    }

    fun findNearestButtonToCurrent(boardHeight: Int, boardWidth: Int, buttonList: List<PuzzleButton>): PuzzleButton {
        return buttonList.minByOrNull { button ->
            val buttonXPos = button.getScaledXPos() * boardWidth
            val buttonYPos = button.getScaledYPos() * boardHeight
            val dist = (xPos.value - buttonXPos).pow(2) + (yPos.value - buttonYPos).pow(2)
            dist
        } ?: error("Button list is empty")
    }
}

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

    private val buttons = (0 until rowsNumber).map { row ->
        (0 until columnsNumber).map { col ->
            PuzzleButton("($row, $col)", row, col, rowsNumber, columnsNumber)
        }
    }.flatten()

    @Composable
    private fun puzzleButtons() {
        BoxWithConstraints(modifier = Modifier
            .fillMaxSize()
            .border(BorderStroke(1.dp, Color.LightGray), RoundedCornerShape(5.dp))
        ) {
            for (button in buttons) {
                puzzleButton(
                    button = button,
                    boardHeight = constraints.maxHeight,
                    boardWidth = constraints.maxWidth
                )
            }
        }
    }

    @Composable
    private fun puzzleButton(button: PuzzleButton, boardHeight: Int, boardWidth: Int) {
        /*println("Button for $currentBoardHeight x $currentBoardWidth")
        val row by remember { mutableStateOf(button.getRow()) }
        val col by remember { mutableStateOf(button.getCol()) }
        var boardHeight by remember { mutableStateOf(0) }
        boardHeight = currentBoardHeight
        var boardWidth by remember { mutableStateOf(0) }
        boardWidth = currentBoardWidth
        val buttonHeight by remember { mutableStateOf((button.getScaledHeight() * boardHeight).roundToInt()) }
        val buttonWidth by remember { mutableStateOf((button.getScaledWidth() * boardWidth).roundToInt()) }
        println("Button: $buttonHeight x $buttonWidth")
        val buttonInitXPos by remember { mutableStateOf((button.getScaledXPos() * boardWidth).roundToInt()) }
        val buttonInitYPos by remember { mutableStateOf((button.getScaledYPos() * boardHeight).roundToInt()) }
        var buttonXPos by remember { mutableStateOf(buttonInitXPos) }
        var buttonYPos by remember { mutableStateOf(buttonInitYPos) }*/
        //println("Button for $boardHeight x $boardWidth")
        val row = button.getRow()
        val col = button.getCol()
        val buttonHeight = (button.getScaledHeight() * boardHeight).roundToInt()
        val buttonWidth = (button.getScaledWidth() * boardWidth).roundToInt()
        //println("Button: $buttonHeight x $buttonWidth")
        //var buttonInitXPos by remember { mutableStateOf() }
        //var buttonInitYPos by remember { mutableStateOf() }
        button.resetPos(boardHeight, boardWidth)

        Button(
            enabled = button.active,
            onClick = {},
            modifier = Modifier
                .height(buttonHeight.dp)
                .width(buttonWidth.dp)
                .padding(5.dp)
                .offset {
                    IntOffset(button.xPos.value, button.yPos.value)
                }
                .pointerInput(Unit) {
                    detectDragGestures(onDragEnd = {
                        val swapButton = button.findNearestButtonToCurrent(boardHeight, boardWidth, buttons)
                        println("${swapButton.getRow()} ${swapButton.getCol()}")
                        if (!swapButton.active && abs(row - swapButton.getRow()) + abs(col - swapButton.getCol()) == 1) {
                            if (button.xPos.value !in
                                (swapButton.xPos.value - buttonWidth / 2)..(swapButton.xPos.value + buttonWidth / 2))
                            {
                                button.resetPos(boardHeight, boardWidth)
                                return@detectDragGestures
                            }
                            if (button.yPos.value !in
                                (swapButton.yPos.value - buttonHeight / 2)..(swapButton.yPos.value + buttonHeight / 2)) {
                                button.resetPos(boardHeight, boardWidth)
                                return@detectDragGestures
                            }

                            button.swapPositions(swapButton)
                            button.resetPos(boardHeight, boardWidth)
                            swapButton.resetPos(boardHeight, boardWidth)
                        } else {
                            button.resetPos(boardHeight, boardWidth)
                        }
                    }) { change, dragAmount ->
                        if (!button.active) {
                            return@detectDragGestures
                        }

                        val nextButtonXPos = button.xPos.value + dragAmount.x.roundToInt()
                        val nextButtonYPos = button.yPos.value + dragAmount.y.roundToInt()

                        if (nextButtonXPos !in 0..(boardWidth - buttonWidth)) {
                            return@detectDragGestures
                        }
                        if (nextButtonYPos !in 0..(boardHeight - buttonHeight)) {
                            return@detectDragGestures
                        }

                        button.xPos.value = nextButtonXPos
                        button.yPos.value = nextButtonYPos
                        change.consumeAllChanges()
                    }
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

    /*private fun checkForSuccess(): Boolean {
        for (row in 0 until rowsNumber) {
            for (col in 0 until columnsNumber) {
                if (buttonContexts[row][col].label.value != (row * columnsNumber + col + 1).toString()) {
                    return false
                }
            }
        }
        return true
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