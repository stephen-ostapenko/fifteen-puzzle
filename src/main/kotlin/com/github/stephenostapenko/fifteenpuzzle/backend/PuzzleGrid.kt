package com.github.stephenostapenko.fifteenpuzzle.backend

import kotlin.math.pow

class PuzzleGrid(private val rowsNumber: Int, private val columnsNumber: Int) {
    private val buttonList: MutableList<MutableList<PuzzleButtonImpl>> = (0 until rowsNumber).map { row ->
        (0 until columnsNumber).map { col ->
            PuzzleButtonImpl(row, col, rowsNumber, columnsNumber)
        }.toMutableList()
    }.toMutableList()

    fun getButtonList(): List<PuzzleButtonImpl> {
        return buttonList.flatten()
    }

    val checkIfGridIsFinished = check@{
        return@check buttonList.all { rowList -> rowList.all { it.checkButtonFitsInitPosition() } }
    }

    fun initGrid(boardHeight: Int, boardWidth: Int) {
        buttonList.forEach { rowList ->
            rowList.forEach {
                it.initButtonPositionOnBoard(boardHeight, boardWidth)
            }
        }
    }

    fun setButtonPositionOnGrid(initRow: Int, initCol: Int, row: Int, col: Int) {
        val button = buttonList.flatten().find { it.getRow() == initRow && it.getCol() == initCol } ?:
            error("Button for position ($initRow, $initCol) can't be found")
        val actualRow = button.getRow()
        val actualCol = button.getCol()
        buttonList[row][col].moveButtonOnGrid(actualRow, actualCol)
        buttonList[actualRow][actualCol].moveButtonOnGrid(row, col)
        buttonList[row][col] = buttonList[actualRow][actualCol]
            .also { buttonList[actualRow][actualCol] = buttonList[row][col] }
    }

    fun findNearestButtonOnGrid(button: PuzzleButtonImpl): PuzzleButtonImpl {
        return buttonList.flatten().minByOrNull {
            val currentButtonXPos = it.getScaledXPos() * button.boardWidth
            val currentButtonYPos = it.getScaledYPos() * button.boardHeight
            val dist = (button.getXPos() - currentButtonXPos).pow(2) +
                    (button.getYPos() - currentButtonYPos).pow(2)
            dist
        } ?: error("Button list is empty")
    }
}