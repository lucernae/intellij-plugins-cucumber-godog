package com.github.lucernae.intellijplugins.cucumbergodog.filters

import com.intellij.execution.filters.ConsoleFilterProvider
import com.intellij.execution.filters.Filter
import com.intellij.openapi.project.Project

class GodogTestConsoleFilterProvider : ConsoleFilterProvider {
    override fun getDefaultFilters(project: Project): Array<Filter> =
        arrayOf(GodogTestOutputFilter(project))
}