package com.github.stephenostapenko.fifteenpuzzle.backend

import androidx.compose.runtime.Composable

class Utility {
    companion object {
        fun getOnClickActionForPuzzleButton(state: MainPanel.GameState, button: PuzzleButtonImpl,
                                            grid: PuzzleGrid): (() -> Unit)
        {
            return {
                val row = button.getRow()
                val col = button.getCol()
                var turnCompleted = false

                for ((rowDelta, colDelta) in listOf(
                    Pair(-1, 0),
                    Pair(0, 1),
                    Pair(1, 0),
                    Pair(0, -1)
                )) {
                    if (row + rowDelta !in (0 until grid.rowsNumber) ||
                        col + colDelta !in (0 until grid.columnsNumber)) {
                        continue
                    }

                    val swapButton = grid.getButtonFromGridByActualPlace(row + rowDelta, col + colDelta)
                    if (!swapButton.active) {
                        button.swapPositions(swapButton)
                        button.updatePositionOnBoard()
                        swapButton.updatePositionOnBoard()

                        turnCompleted = true
                        break
                    }
                }

                if (turnCompleted) {
                    state.setInProgress()
                    state.incTurnsCount()

                    if (grid.checkIfGridIsFinished()) {
                        state.setFinished()
                    }
                }
            }
        }

        private const val SHUFFLE_ITERATIONS = 501

        @Composable
        fun shuffleCells(rowsNumber: Int, columnsNumber: Int, grid: PuzzleGrid) {
            val cells = (0 until rowsNumber).map { row ->
                (0 until columnsNumber).map { col ->
                    row to col
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

            for (row in 0 until rowsNumber) {
                for (col in 0 until columnsNumber) {
                    val (rowPos, colPos) = cells[row][col]
                    grid.getButtonFromGridByInitPlace(rowPos, colPos).moveButtonOnGridAnimated(row, col)
                }
            }
        }
    }
}