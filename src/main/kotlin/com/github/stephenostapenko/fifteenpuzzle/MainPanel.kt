package com.github.stephenostapenko.fifteenpuzzle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.awt.ComposePanel
import com.github.stephenostapenko.fifteenpuzzle.UI.UIElements.Companion.composePanel
import com.github.stephenostapenko.fifteenpuzzle.backend.PuzzleButton
import javax.swing.JComponent

class MainPanel(private val rowsNumber: Int, private val columnsNumber: Int) {
    private var buttonList: List<PuzzleButton> = (0 until rowsNumber).map { row ->
        (0 until columnsNumber).map { col ->
            PuzzleButton("${row * columnsNumber + col + 1}", row, col, rowsNumber, columnsNumber)
        }
    }.flatten()

    fun getJComponentPanel(): JComponent {
        return ComposePanel().apply {
            setContent {
                composePanel(GameState, buttonList, checkForSuccess)
            }
        }
    }

    @Composable
    fun getComposePanel() {
        composePanel(GameState, buttonList, checkForSuccess)
    }

    private val checkForSuccess = check@{ buttonList: List<PuzzleButton> ->
        return@check buttonList.all { it.row == it.initRow && it.col == it.initCol }
    }

    @Suppress("unused")
    object GameState {
        enum class ProcessState {
            Ready, InProgress, Finished
        }

        val notProgressStates = listOf(ProcessState.Ready, ProcessState.Finished)

        private val state = mutableStateOf(ProcessState.Ready)

        fun getState(): ProcessState {
            return state.value
        }

        fun setReady() {
            state.value = ProcessState.Ready
        }

        fun setInProgress() {
            if (state.value != ProcessState.InProgress) {
                resetTurnsCount()
            }
            state.value = ProcessState.InProgress
        }

        fun setFinished() {
            state.value = ProcessState.Finished
        }

        private val turnsCount = mutableStateOf(0)

        fun getTurnsCount(): Int {
            return turnsCount.value
        }

        fun incTurnsCount() {
            turnsCount.value++
        }

        fun resetTurnsCount() {
            turnsCount.value = 0
        }
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

    private val SHUFFLE_ITERATIONS = 3

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