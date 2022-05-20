package com.github.stephenostapenko.fifteenpuzzle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.awt.ComposePanel
import com.github.stephenostapenko.fifteenpuzzle.UIElements.MainInterfaceComposePanel
import com.github.stephenostapenko.fifteenpuzzle.backend.PuzzleButtonImpl
import javax.swing.JComponent

class MainPanel(private val rowsNumber: Int, private val columnsNumber: Int) {
    private var buttonList: List<List<PuzzleButtonImpl>> = (0 until rowsNumber).map { row ->
        (0 until columnsNumber).map { col ->
            PuzzleButtonImpl(row, col, rowsNumber, columnsNumber)
        }
    }

    fun getJComponentPanel(): JComponent {
        return ComposePanel().apply {
            setContent {
                MainInterfaceComposePanel.mainInterfaceComposePanel(
                    rowsNumber, columnsNumber,
                    GameState, buttonList, checkForSuccess
                )
            }
        }
    }

    @Composable
    fun getComposePanel() {
        MainInterfaceComposePanel.mainInterfaceComposePanel(
            rowsNumber, columnsNumber,
            GameState, buttonList, checkForSuccess
        )
    }

    private val checkForSuccess = check@{ buttonList: List<List<PuzzleButtonImpl>> ->
        return@check buttonList.all { rowList -> rowList.all { it.checkButtonPosition() } }
    }

    @Suppress("MemberVisibilityCanBePrivate", "unused")
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
            resetTurnsCount()
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
}