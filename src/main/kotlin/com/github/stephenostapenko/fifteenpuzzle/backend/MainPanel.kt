package com.github.stephenostapenko.fifteenpuzzle.backend

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.awt.ComposePanel
import com.github.stephenostapenko.fifteenpuzzle.UIElements.MainInterfaceComposePanel
import javax.swing.JComponent

class MainPanel(private val rowsNumber: Int, private val columnsNumber: Int) {
    val grid = PuzzleGrid(rowsNumber, columnsNumber)

    fun getSwingPanel(): JComponent {
        return ComposePanel().apply {
            setContent {
                MainInterfaceComposePanel.mainInterfaceComposePanel(
                    rowsNumber, columnsNumber,
                    GameState, grid
                )
            }
        }
    }

    @Composable
    fun getComposePanel() {
        MainInterfaceComposePanel.mainInterfaceComposePanel(
            rowsNumber, columnsNumber,
            GameState, grid
        )
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