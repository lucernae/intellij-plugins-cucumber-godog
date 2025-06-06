package com.github.lucernae.intellijplugins.cucumbergodog.gherkin

import com.intellij.execution.RunManager
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiUtilCore
import org.jetbrains.plugins.cucumber.psi.GherkinElementTypes
import org.jetbrains.plugins.cucumber.psi.GherkinFeature
import org.jetbrains.plugins.cucumber.psi.GherkinFileType
import org.jetbrains.plugins.cucumber.psi.GherkinScenario
import java.util.concurrent.ConcurrentHashMap
import javax.swing.Icon

/**
 * Provides a Run button next to Scenario lines in Gherkin files.
 */
class GherkinRunLineMarkerProvider : RunLineMarkerContributor() {
    companion object {
        private val testResults = ConcurrentHashMap<String, Boolean>()
    }

    private fun getIconForTest(filePath: String, featureName: String, scenarioTitle: String): Icon {
        val testKey = "$filePath:$featureName:$scenarioTitle"
        return when (testResults[testKey]) {
            true -> AllIcons.RunConfigurations.TestState.Green2
            false -> AllIcons.RunConfigurations.TestState.Red2
            else -> AllIcons.RunConfigurations.TestState.Run_run
        }
    }

    override fun getInfo(element: PsiElement): Info? {
        // Only process leaf elements (text)
        if (!element.isValid || element.textLength == 0) {
            return null
        }

        val file = element.containingFile
        when {
            file.fileType != GherkinFileType.INSTANCE -> return null
            GherkinElementTypes.FEATURE == element.node.elementType -> {
                val gherkinFeature = element.node.psi as GherkinFeature
                val featureName = gherkinFeature.featureName.trim()
                return Info(
                    getIconForTest(file.virtualFile.path, featureName, ""),
                    arrayOf(RunScenarioAction(featureName, "", file)),
                    { "Run all scenarios in feature: $featureName" }
                )
            }
            GherkinElementTypes.SCENARIO == element.node.elementType -> {
                val gherkinFeature = element.parent.node.psi as GherkinFeature
                val featureName = gherkinFeature.featureName.trim()
                val gherkinScenario = element.node.psi as GherkinScenario
                val scenarioTitle = gherkinScenario.scenarioName.trim()
                return Info(
                    getIconForTest(file.virtualFile.path, featureName, scenarioTitle),
                    arrayOf(RunScenarioAction(featureName, scenarioTitle, file)),
                    { "Run scenario: $scenarioTitle in feature: $featureName" }
                )
            }
        }

        return null
    }

    /**
     * Action to run a Go test for a specific Scenario.
     */
    private class RunScenarioAction(
        private val featureName: String,
        private val scenarioTitle: String,
        private val file: PsiFile
    ): AnAction("Run scenario: $scenarioTitle in feature: $featureName") {

        fun getSanitizedGoTestName(name: String): String {
            return name
                .replace(Regex("[^a-zA-Z0-9]"), "_")
                .replace(Regex("_+"), "_")
                .trim('_')
        }

        override fun actionPerformed(e: AnActionEvent) {
            val project = e.project ?: return
            val virtualFile = PsiUtilCore.getVirtualFile(file) ?: return

            val configurationType = GodogTestConfigurationType.getInstance()
            val runManager = RunManager.getInstance(project)
            val factory = configurationType.configurationFactories.first() as GodogRunConfigurationFactory

            val testPattern = "/${getSanitizedGoTestName(featureName)}/${getSanitizedGoTestName(scenarioTitle)}"

            val configurationName = "Godog tests:${virtualFile.name}:$featureName:$scenarioTitle"
            val configurationSettings = runManager.createConfiguration(
                configurationName,
                factory
            )
            configurationSettings.isActivateToolWindowBeforeRun = true
            (configurationSettings.configuration as GodogRunConfiguration).apply {
                this.pattern = testPattern
                this.directoryPath = virtualFile.parent.parent.path
                this.featureMap = mutableMapOf(featureName to virtualFile.path)
            }
            runManager.addConfiguration(configurationSettings)
            runManager.selectedConfiguration = configurationSettings
            val executor = com.intellij.execution.executors.DefaultRunExecutor.getRunExecutorInstance()
            val environment =
                com.intellij.execution.runners.ExecutionEnvironmentBuilder.create(executor, configurationSettings)
                    .build()

            val listener = object : com.intellij.execution.process.ProcessListener {
                override fun startNotified(event: com.intellij.execution.process.ProcessEvent) {}

                override fun processTerminated(event: com.intellij.execution.process.ProcessEvent) {
                    val testKey = "${file.virtualFile.path}:$featureName:$scenarioTitle"
                    testResults[testKey] = event.exitCode == 0
                }

                override fun onTextAvailable(
                    event: com.intellij.execution.process.ProcessEvent,
                    outputType: com.intellij.openapi.util.Key<*>
                ) {
                }
            }

            environment.runner.execute(environment) {
                it.processHandler?.addProcessListener(listener)
            }
        }
    }
}
