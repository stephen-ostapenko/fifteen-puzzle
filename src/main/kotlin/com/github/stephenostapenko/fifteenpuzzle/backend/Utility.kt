package com.github.stephenostapenko.fifteenpuzzle.backend

class Utility {
    companion object {
        private const val SHUFFLE_ITERATIONS = 501

        fun shuffleCells(rowsNumber: Int, columnsNumber: Int, buttonList: List<List<PuzzleButton>>) {
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
                    buttonList[rowPos][colPos].moveButtonOnGrid(row, col)
                }
            }
        }
    }
}