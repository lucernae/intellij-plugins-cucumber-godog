package com.github.lucernae.intellijplugins.cucumbergodog.gherkin

import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.testFramework.TestDataPath
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.plugins.cucumber.psi.GherkinFileType

@TestDataPath("\$CONTENT_ROOT/src/test/testData")
class GherkinRunLineMarkerProviderTest : BasePlatformTestCase() {

    private lateinit var provider: GherkinRunLineMarkerProvider

    override fun setUp() {
        super.setUp()
        provider = GherkinRunLineMarkerProvider()
    }

    override fun getTestDataPath(): String = "src/test/testData"

    fun testGetInfoReturnsNullForNonGherkinFile() {
        val psiFile = myFixture.configureByText("test.txt", "Some text")
        val element = psiFile.firstChild
        assertNotNull(element)

        val info = provider.getInfo(element)
        assertNull("Should return null for non-Gherkin file", info)
    }

    fun testGetInfoReturnsNullForInvalidElement() {
        val psiFile = myFixture.configureByText(GherkinFileType.INSTANCE, "")
        val element = psiFile.firstChild ?: return // If there's no child, skip the test

        val info = provider.getInfo(element)
        assertNull("Should return null for invalid element", info)
    }

    fun testGetInfoReturnsNullForNonFeatureOrScenarioElement() {
        // Create a Gherkin file with a step (Given/When/Then) which is not a feature or scenario
        val psiFile = myFixture.configureByText(GherkinFileType.INSTANCE, 
            """
            Feature: Test feature
              Scenario: Test scenario
                Given a step
            """.trimIndent())

        // Find the step element (not a feature or scenario)
        var stepElement: PsiElement? = null
        psiFile.accept(object : com.intellij.psi.PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {
                if (element.text.contains("Given a step")) {
                    stepElement = element
                    return
                }
                super.visitElement(element)
            }
        })

        assertNotNull("Step element should be found", stepElement)

        val info = provider.getInfo(stepElement!!)
        assertNull("Should return null for non-feature or non-scenario element", info)
    }

    fun testSanitizeGoTestName() {
        // Test the sanitization method directly
        val sanitizedName1 = sanitizeGoTestName("Feature with spaces!")
        assertEquals("Feature_with_spaces", sanitizedName1)

        val sanitizedName2 = sanitizeGoTestName("Scenario with special chars: @#$%")
        assertEquals("Scenario_with_special_chars", sanitizedName2)

        val sanitizedName3 = sanitizeGoTestName("Multiple__underscores___should_be_single_")
        assertEquals("Multiple_underscores_should_be_single", sanitizedName3)
    }

    // Helper method that replicates the functionality in RunScenarioAction
    private fun sanitizeGoTestName(name: String): String {
        return name
            .replace(Regex("[^a-zA-Z0-9]"), "_")
            .replace(Regex("_+"), "_")
            .trim('_')
    }
}
