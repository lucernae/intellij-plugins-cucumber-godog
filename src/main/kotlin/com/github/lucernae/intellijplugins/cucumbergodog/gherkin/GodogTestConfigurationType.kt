package com.github.lucernae.intellijplugins.cucumbergodog.gherkin

import com.goide.execution.testing.GoTestRunConfigurationType
import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.ServiceManager
import com.intellij.execution.configurations.ConfigurationType

/**
 * Configuration type for Godog tests.
 */
class GodogTestConfigurationType : ConfigurationTypeBase(
    "GodogTestRunConfiguration",
    "Godog Test",
    "Run Godog BDD tests",
    AllIcons.RunConfigurations.TestState.Run
) {
    companion object {
        @JvmStatic
        fun getInstance(): GodogTestConfigurationType {
            return ConfigurationType.CONFIGURATION_TYPE_EP.findExtension(GodogTestConfigurationType::class.java)
                ?: error("Could not find GodogTestConfigurationType")
        }
    }

    init {
        // Register the factory
        addFactory(GodogRunConfigurationFactory(this))
    }
}
