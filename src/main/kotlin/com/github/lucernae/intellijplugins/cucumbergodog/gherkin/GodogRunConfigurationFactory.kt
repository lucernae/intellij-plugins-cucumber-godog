package com.github.lucernae.intellijplugins.cucumbergodog.gherkin

import com.goide.execution.testing.frameworks.gotest.GotestFramework
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfiguration
import com.intellij.openapi.project.Project

/**
 * Factory for creating Godog run configurations.
 */
class GodogRunConfigurationFactory(type: GodogTestConfigurationType) : ConfigurationFactory(type) {
    override fun getId(): String = "GODOG_TEST"

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return GodogRunConfiguration(project, "Godog Tests", type).apply {
            this.testFramework = GotestFramework.INSTANCE
            this.workingDirectory = project.basePath ?: ""
        }
    }
}