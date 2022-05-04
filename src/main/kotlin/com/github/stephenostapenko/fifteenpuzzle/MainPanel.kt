package com.github.stephenostapenko.fifteenpuzzle

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import javax.swing.JComponent

const val SHUFFLE_ITERATIONS = 501

class MainPanel(private val rowsNumber: Int, private val columnsNumber: Int) {
    fun createPanel(): JComponent {
        return ComposePanel().apply {
            setContent {
                MaterialTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            buttonsForPuzzle()
                            turnsCounter()
                            successLabel()
                            shuffleButton()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun buttonsForPuzzle() {
        for (row in 0 until rowsNumber) {
            Row {
                for (col in 0 until columnsNumber) {
                    Button(
                        enabled = buttonContexts[row][col].enabled.value,
                        onClick = getOnClickActionForPuzzleButton(row, col),
                        modifier = Modifier.scale(1f, 1.5f).padding(5.dp).weight(1f)
                    ) {
                        Text(
                            text = buttonContexts[row][col].label.value,
                            modifier = Modifier.scale(1.2f, 0.8f)
                        )
                    }
                }
            }
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
                shuffleCells()
            },
            modifier = Modifier.scale(1.25f).padding(15.dp)
        ) {
            Text(text = "Shuffle cells")
        }
    }

    private data class ButtonContext(val label: MutableState<String>, val enabled: MutableState<Boolean>)

    private val buttonContexts = (0 until rowsNumber).map { row ->
        (0 until columnsNumber).map { col ->
            ButtonContext(
                label = mutableStateOf((row * columnsNumber + col + 1).toString()),
                enabled = mutableStateOf(row != rowsNumber - 1 || col != columnsNumber - 1)
            )
        }
    }
    private var gameFinishedState = mutableStateOf(false)
    private var gameInitState = mutableStateOf(true)
    private var turnsCount = mutableStateOf(0)

    private fun swapContexts(row1: Int, col1: Int, row2: Int, col2: Int) {
        buttonContexts[row1][col1].label.value = buttonContexts[row2][col2].label.value.also {
            buttonContexts[row2][col2].label.value = buttonContexts[row1][col1].label.value
        }
        buttonContexts[row1][col1].enabled.value = buttonContexts[row2][col2].enabled.value.also {
            buttonContexts[row2][col2].enabled.value = buttonContexts[row1][col1].enabled.value
        }
    }

    private fun getOnClickActionForPuzzleButton(row: Int, col: Int): (() -> Unit) {
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
    }

    private fun checkForSuccess(): Boolean {
        for (row in 0 until rowsNumber) {
            for (col in 0 until columnsNumber) {
                if (buttonContexts[row][col].label.value != (row * columnsNumber + col + 1).toString()) {
                    return false
                }
            }
        }
        return true
    }

    private fun updateGameStates() {
        gameFinishedState.value = checkForSuccess()
        if (gameFinishedState.value) {
            gameInitState.value = true
        }
    }

    private fun shuffleCells() {
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
    }
}