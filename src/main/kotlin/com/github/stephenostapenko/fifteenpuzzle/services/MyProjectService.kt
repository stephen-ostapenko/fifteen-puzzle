package com.github.stephenostapenko.fifteenpuzzle.services

import com.intellij.openapi.project.Project
import com.github.stephenostapenko.fifteenpuzzle.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
