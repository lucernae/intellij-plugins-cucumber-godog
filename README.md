# Cucumber Godog Test Runner

![Build](https://github.com/lucernae/intellij-plugins-cucumber-godog/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

## Features

- Provide Run Line Marker to A Gherkin feature file
- Run the marker as Go test

<!-- Plugin description -->
This IntelliJ Platform Plugin provides integration between Gherkin feature files and Go tests. It adds a "Run" button next to each Scenario in a Gherkin file. When clicked, the plugin will run a Go test with a test case specifier derived from the Scenario title.

Key features:
- Provide Run Line Marker to A Gherkin feature file
- Run the marker as Go test
- Run Go tests with test case specifiers derived from Scenario titles
- Display test output in the Run window like standard Go test commands
- Clickable Scenario and Feature title from Godog console output back to the feature file

This plugin is useful for BDD (Behavior-Driven Development) workflows where Gherkin is used to define test scenarios and Go is used to implement the tests.

This specific section is a source for the [plugin.xml](/src/main/resources/META-INF/plugin.xml) file which will be extracted by the [Gradle](/build.gradle.kts) during the build process.

To keep everything working, do not remove `<!-- ... -->` sections. 
<!-- Plugin description end -->

## Usage

### Godog integration 

The tools were mainly used to integrate with Godog output. See [Godog](https://github.com/cucumber/godog) for more info.

Test data is included in [testData](./src/test/testData) directory.
Open it using Goland or other intellij IDE with Go plugin enabled.

1. Open a Gherkin feature file in the editor
2. You'll see a "Run" button next to each Scenario
3. Click the "Run" button to execute the corresponding Go test
4. The test output will be displayed in the Run window

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Cucumber Godog Test Runner"</kbd> >
  <kbd>Install</kbd>

- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/lucernae/intellij-plugins-cucumber-godog/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
