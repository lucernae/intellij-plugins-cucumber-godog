package com.github.lucernae.intellijplugins.cucumbergodog.gherkin

import com.goide.execution.testing.GoTestRunConfiguration
import com.goide.execution.testing.frameworks.gotest.GotestFramework
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.openapi.project.Project
import org.jdom.Element

/**
 * Run configuration for Godog tests.
 */
class GodogRunConfiguration(
    project: Project,
    name: String,
    type: ConfigurationType
) : GoTestRunConfiguration(project, name, type) {

    class GodogRunConfigurationOptions {
        var featureMap = mutableMapOf<String, String>()
    }

    private val options = GodogRunConfigurationOptions()

    var featureMap: MutableMap<String, String>
        get() = options.featureMap
        set(value) {
            options.featureMap = value
        }

    init {
        // Set default values for Godog tests
        testFramework = GotestFramework.INSTANCE
        workingDirectory = project.basePath ?: ""
    }
    
    override fun writeExternal(element: Element) {
        super.writeExternal(element)
        val mapElement = Element("featureMap")
        element.setAttribute("featureMap", com.google.gson.Gson().toJson(options.featureMap))
        element.addContent(mapElement)
    }

    override fun readExternal(element: Element) {
        super.readExternal(element)
        val mapElement = element.getChild("featureMap") ?: return
        featureMap.clear()
        mapElement.getAttributeValue("featureMap")?.let {
            featureMap = com.google.gson.Gson().fromJson(it, mutableMapOf<String, String>().javaClass)
        }
    }
}