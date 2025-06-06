package com.github.lucernae.intellijplugins.cucumbergodog.filters

import com.github.lucernae.intellijplugins.cucumbergodog.gherkin.GodogRunConfiguration
import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.OpenFileHyperlinkInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem

/**
 * Console filter for Godog test output.
 * Implements functionality similar to Go test output filter.
 */
class GodogTestOutputFilter(private val project: Project) : Filter {
    private fun processMatch(
        match: MatchResult,
        line: String,
        entireLength: Int,
        adjustEndPoint: Int = 1
    ): Filter.Result? {
        val filePath = match.groupValues[1]
        val lineNumber = match.groupValues[2].toIntOrNull() ?: return null

        val file = LocalFileSystem.getInstance().findFileByPath(filePath) ?: return null

        val startPoint = entireLength - line.length + match.range.first
        val endPoint = entireLength - line.length + match.range.last + adjustEndPoint

        val hyperlinkInfo = OpenFileHyperlinkInfo(project, file, lineNumber - 1)

        return Filter.Result(startPoint, endPoint, hyperlinkInfo)
    }

    private fun getFeatureMap(): Map<String, String> {
        val runManager = com.intellij.execution.RunManager.getInstance(project)
        val configuration = runManager.selectedConfiguration?.configuration as? GodogRunConfiguration
        return configuration?.featureMap ?: return emptyMap()
    }

    private fun processGodogFeaturesMatch(
        match: MatchResult,
        line: String,
        entireLength: Int,
        adjustEndPoint: Int = 1
    ): Filter.Result? {
        val title = match.groupValues[1]
        val featureName = match.groupValues[2]
        val lineNumber = match.groupValues.getOrNull(3)?.toIntOrNull() ?: 1

        val featureMap = getFeatureMap()
        val filePath = featureMap[featureName] ?: return null
        val file = LocalFileSystem.getInstance().findFileByPath(filePath) ?: return null

        val startPoint = entireLength - line.length + match.range.first
        val endPoint = when {
            title.startsWith("Scenario:") -> startPoint + title.length + adjustEndPoint
            else -> startPoint + match.range.last + adjustEndPoint
        }

        val hyperlinkInfo = OpenFileHyperlinkInfo(project, file, lineNumber - 1)

        return Filter.Result(startPoint, endPoint, hyperlinkInfo)
    }

    private fun processStandardPattern(line: String, entireLength: Int): Filter.Result? {
        val standardPattern = Regex("(.*\\.go):(\\d+):")
        val standardMatch = standardPattern.find(line) ?: return null
        return processMatch(standardMatch, line, entireLength)
    }

    private fun processTestPattern(line: String, entireLength: Int): Filter.Result? {
        val testPattern = Regex("(.*_test\\.go):(\\d+):")
        val testMatch = testPattern.find(line) ?: return null
        return processMatch(testMatch, line, entireLength)
    }

    private fun processPanicPattern(line: String, entireLength: Int): Filter.Result? {
        val panicPattern = Regex("\\s+(.*/.*\\.go):(\\d+)\\s+")
        val panicMatch = panicPattern.find(line) ?: return null
        return processMatch(panicMatch, line, entireLength, 0)
    }

    private fun processGodogFeaturesPattern(line: String, entireLength: Int): Filter.Result? {
        val godogFeaturesPattern = Regex("(Feature:.*?)\\s*([^#\n]+?)$")
        val godogFeaturesMatch = godogFeaturesPattern.find(line) ?: return null
        return processGodogFeaturesMatch(godogFeaturesMatch, line, entireLength)
    }

    private fun processGodogScenarioPattern(line: String, entireLength: Int): Filter.Result? {

        val godogLinePattern = Regex("(Scenario:.*?)\\s*#\\s*([^#\n]+?):(\\d+)\\s+")
        val godogLineMatch = godogLinePattern.find(line) ?: return null
        return processGodogFeaturesMatch(godogLineMatch, line, entireLength, 0)
    }

    override fun applyFilter(line: String, entireLength: Int): Filter.Result? {
        if (line.isEmpty()) return null

        return processGodogFeaturesPattern(line, entireLength)
            ?: processGodogScenarioPattern(line, entireLength)
            ?: processStandardPattern(line, entireLength)
            ?: processTestPattern(line, entireLength)
            ?: processPanicPattern(line, entireLength)
    }
}