package com.github.stephenostapenko.fifteenpuzzle

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.wm.ToolWindowManager

class MainPluginAction : DumbAwareAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val p = e.project ?: return
        ToolWindowManager.getInstance(p).getToolWindow("Fifteen Puzzle")?.show()
    }
}