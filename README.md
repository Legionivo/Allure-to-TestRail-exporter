## A plugin that exports test cases from Intellij Idea to TestRail using Allure @Step annotations.</h1>
Plugin is published here - https://plugins.jetbrains.com/plugin/13322-allure-to-test-rail-exporter/
## Requirements:
JDK 8+

Gradle 5(optional, wrapper is included in project) 

## How to update:
   * Clone repo
   * Import project into Intellij Idea as a Gradle project - everything will be done automatically
   * Do code changes 
   * Change plugin version in build.gradle
   * Run `buildPlugin` gradle task
   * Install new plugin from `build/distributions/AllureToTestRailExporter-x.x.zip`
   * Have fun

**Plugin does update existing test case in TestRail - no need to delete it manually and export again!**  
   
#### Plugin was build to work only with the following libraries::
    Junit 5
    Allure
##### How to use:

    Set up valid credentials in plugin settings under Tools - TestRail exporter plugin

    Check that connection is successful

    Open file with tests

    Select desired test by putting a cursor on a test name - this is IMPORTANT

    Push Alt - Insert combination

    Select "Export to TestRail" menu item

    TmsLink with testCase ID should be added
 
 
 
**If you do not need test steps - enable "Export only test name" checkbox in plugin configuration settings**



##### Code requirements:
    @DisplayName annotation is mandatory - test case title is created from it
    @Feature annotation on a test or class level is mandatory - TestRail section is linked to it:
     - If @Feature is present on a class and methods levels, method annotation is taken
     - If @Feature is not present on a class level, method annotation is taken
     - If conditions above are false - class annotation is taken 
    @Link is not mandatory, but strongly advised
    If you want to see nice test case - write nice @Step annotations  
    
##### Demo    
![](https://github.com/Legionivo/Allure-to-TestRail-exporter/raw/master/plugin.gif)
