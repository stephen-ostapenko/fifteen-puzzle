package com.github.stephenostapenko.fifteenpuzzle.backend

import kotlin.math.pow

class PuzzleGrid(val rowsNumber: Int, val columnsNumber: Int) {
    val buttonList: List<PuzzleButtonImpl> = (0 until rowsNumber).map { row ->
        (0 until columnsNumber).map { col ->
            PuzzleButtonImpl(row, col, rowsNumber, columnsNumber)
        }
    }.flatten()

    val checkIfGridIsFinished = check@{
        return@check buttonList.all { it.checkButtonFitsInitPosition() }
    }

    fun initGrid(boardHeight: Int, boardWidth: Int) {
        buttonList.forEach {
            it.initButtonPositionOnBoard(boardHeight, boardWidth)
        }
    }

    fun getButtonFromGridByInitPlace(row: Int, col: Int): PuzzleButtonImpl {
        return buttonList.find { it.initRow == row && it.initCol == col } ?:
            error("Button for initial position ($row, $col) can't be found")
    }

    fun getButtonFromGridByActualPlace(row: Int, col: Int): PuzzleButtonImpl {
        return buttonList.find { it.getRow() == row && it.getCol() == col } ?:
        error("Button for actual position ($row, $col) can't be found")
    }

    fun findNearestButtonOnGrid(button: PuzzleButtonImpl): PuzzleButtonImpl {
        return buttonList.minByOrNull {
            val currentButtonXPos = it.getScaledXPos() * button.boardWidth
            val currentButtonYPos = it.getScaledYPos() * button.boardHeight
            val dist = (button.getXPos() - currentButtonXPos).pow(2) +
                    (button.getYPos() - currentButtonYPos).pow(2)
            dist
        } ?: error("Button list is empty")
    }
}