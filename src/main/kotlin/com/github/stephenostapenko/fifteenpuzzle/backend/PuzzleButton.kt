package com.github.stephenostapenko.fifteenpuzzle.backend

import androidx.compose.runtime.mutableStateOf
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

@Suppress("MemberVisibilityCanBePrivate")
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

    var boardHeight = 0
    var boardWidth = 0

    fun initButton(boardHeight: Int, boardWidth: Int) {
        setBoardSize(boardHeight, boardWidth)
        updatePos()
    }

    fun setBoardSize(height: Int, width: Int) {
        boardHeight = height
        boardWidth = width
    }

    fun updatePos() {
        setXPos((getScaledXPos() * boardWidth).roundToInt())
        setYPos((getScaledYPos() * boardHeight).roundToInt())
    }

    fun getHeight(): Int {
        return (getScaledHeight() * boardHeight).roundToInt()
    }

    fun getWidth(): Int {
        return (getScaledWidth() * boardWidth).roundToInt()
    }

    val active = !(row + 1 == rowsNumber && col + 1 == columnsNumber)

    private val selected = mutableStateOf(false)

    fun checkSelected(): Boolean {
        return selected.value
    }

    fun select() {
        selected.value = true
    }

    fun deselect() {
        selected.value = false
    }

    fun getScaledHeight(): Double {
        return 1.0 / rowsNumber
    }

    fun getScaledWidth(): Double {
        return 1.0 / columnsNumber
    }

    fun getScaledXPos(): Double {
        return getScaledWidth() * col
    }

    fun getScaledYPos(): Double {
        return getScaledHeight() * row
    }

    fun swapPositions(button: PuzzleButton) {
        row = button.row.also { button.row = row }
        col = button.col.also { button.col = col }
    }

    fun getManhattanDistOnGrid(button: PuzzleButton): Int {
        return abs(row - button.row) + abs(col - button.col)
    }

    fun findNearestButtonToCurrent(buttonList: List<PuzzleButton>): PuzzleButton {
        return buttonList.minByOrNull { button ->
            val buttonXPos = button.getScaledXPos() * boardWidth
            val buttonYPos = button.getScaledYPos() * boardHeight
            val dist = (getXPos() - buttonXPos).pow(2) + (getYPos() - buttonYPos).pow(2)
            dist
        } ?: error("Button list is empty")
    }
}