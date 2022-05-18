package com.github.stephenostapenko.fifteenpuzzle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import kotlin.math.abs
import javax.swing.JComponent
import kotlin.math.pow
import kotlin.math.roundToInt

const val SHUFFLE_ITERATIONS = 3

class PuzzleButton(val label: String,
                   val initRow: Int, val initCol: Int,
                   private val rowsNumber: Int, private val columnsNumber: Int)
{
    var row = initRow
    var col = initCol

    private val xPos = mutableStateOf(0)
    private val yPos = mutableStateOf(0)

    fun getXPos(): Int {
        return xPos.value
    }

    fun getYPos(): Int {
        return yPos.value
    }

    fun setXPos(pos: Int) {
        xPos.value = pos
    }

    fun setYPos(pos: Int) {
        yPos.value = pos
    }

    fun shiftXPos(shift: Int) {
        xPos.value += shift
    }

    fun shiftYPos(shift: Int) {
        yPos.value += shift
    }

    fun resetPos(height: Int, width: Int) {
        setXPos((getScaledXPos() * width).roundToInt())
        setYPos((getScaledYPos() * height).roundToInt())
    }

    val active = !(row + 1 == rowsNumber && col + 1 == columnsNumber)

    private val orderingIndex = mutableStateOf(0f)

    fun getOrderingIndex(): Float {
        return orderingIndex.value
    }

    fun moveToTop() {
        orderingIndex.value = 1f
    }

    fun moveToBottom() {
        orderingIndex.value = 0f
    }

    val onDragAction: MutableState<(PointerInputChange, Offset) -> Unit> = mutableStateOf( { _, _ -> } )
    val onDragEndAction: MutableState<() -> Unit> = mutableStateOf( {} )

    fun createOnDragAction(boardHeight: Int, boardWidth: Int, buttonHeight: Int, buttonWidth: Int):
                (PointerInputChange, Offset) -> Unit
    {
        return action@{ change, dragAmount ->
            println("$buttonHeight x $buttonWidth")
            if (!active) {
                return@action
            }

            moveToTop()

            val nextButtonXPos = getXPos() + dragAmount.x.roundToInt()
            val nextButtonYPos = getYPos() + dragAmount.y.roundToInt()

            if (nextButtonXPos !in 0..(boardWidth - buttonWidth)) {
                //println("Hit side")
                //println("Checking with $boardHeight x $boardWidth and $buttonHeight x $buttonWidth")
                return@action
            }
            if (nextButtonYPos !in 0..(boardHeight - buttonHeight)) {
                return@action
            }

            setXPos(nextButtonXPos)
            setYPos(nextButtonYPos)
            change.consumeAllChanges()
        }
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

    fun getManhattanDistOnGrid(button: PuzzleButton): Int {
        return abs(row - button.row) + abs(col - button.col)
    }

    fun findNearestButtonToCurrent(boardHeight: Int, boardWidth: Int, buttonList: List<PuzzleButton>): PuzzleButton {
        return buttonList.minByOrNull { button ->
            val buttonXPos = button.getScaledXPos() * boardWidth
            val buttonYPos = button.getScaledYPos() * boardHeight
            val dist = (getXPos() - buttonXPos).pow(2) + (getYPos() - buttonYPos).pow(2)
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
        val buttonHeight = (button.getScaledHeight() * boardHeight).roundToInt()
        val buttonWidth = (button.getScaledWidth() * boardWidth).roundToInt()
        button.resetPos(boardHeight, boardWidth)

        //println("Checking with $boardHeight x $boardWidth and $buttonHeight x $buttonWidth")
        val onDragActionSaver = Saver<Pair<(PointerInputChange, Offset) -> Unit, Int>, Int> (
            save = { it.second }, restore = { button.createOnDragAction(it, boardWidth, buttonHeight, buttonWidth) to it }
        )
        val onDragAction: Pair<(PointerInputChange, Offset) -> Unit, Int> by rememberSaveable(saver = onDragActionSaver) {
            mutableStateOf(
                action@{ change: PointerInputChange, dragAmount: Offset ->
                    if (!button.active) {
                        return@action
                    }

                    gameFinishedState.value = false
                    button.moveToTop()

                    val nextButtonXPos = button.getXPos() + dragAmount.x.roundToInt()
                    val nextButtonYPos = button.getYPos() + dragAmount.y.roundToInt()

                    if (nextButtonXPos !in 0..(boardWidth - buttonWidth)) {
                        //println("Hit side")
                        //println("Checking with $boardHeight x $boardWidth and $buttonHeight x $buttonWidth")
                        return@action
                    }
                    if (nextButtonYPos !in 0..(boardHeight - buttonHeight)) {
                        return@action
                    }

                    button.setXPos(nextButtonXPos)
                    button.setYPos(nextButtonYPos)
                    change.consumeAllChanges()
                } to boardHeight
            )
        }

        button.onDragEndAction.value = action@{
            button.moveToBottom()

            val swapButton = button.findNearestButtonToCurrent(boardHeight, boardWidth, buttons)

            if (swapButton.active) {
                button.resetPos(boardHeight, boardWidth)
                return@action
            }
            if (button.getManhattanDistOnGrid(swapButton) != 1) {
                button.resetPos(boardHeight, boardWidth)
                return@action
            }
            if (button.getXPos() !in
                (swapButton.getXPos() - buttonWidth / 2)..(swapButton.getXPos() + buttonWidth / 2))
            {
                button.resetPos(boardHeight, boardWidth)
                return@action
            }
            if (button.getYPos() !in
                (swapButton.getYPos() - buttonHeight / 2)..(swapButton.getYPos() + buttonHeight / 2))
            {
                button.resetPos(boardHeight, boardWidth)
                return@action
            }

            button.swapPositions(swapButton)
            button.resetPos(boardHeight, boardWidth)
            swapButton.resetPos(boardHeight, boardWidth)

            turnsCount.value++
            if (checkForSuccess()) {
                gameFinishedState.value = true
            }
        }

        Button(
            enabled = button.active,
            onClick = {},
            modifier = Modifier
                .height(buttonHeight.dp)
                .width(buttonWidth.dp)
                .padding(5.dp)
                .offset {
                    IntOffset(button.getXPos(), button.getYPos())
                }
                .zIndex(if (button.active) button.getOrderingIndex() else -1f)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = onDragAction.first,
                        onDragEnd = button.onDragEndAction.value
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
        return buttons.all { it.row == it.initRow && it.col == it.initCol }
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