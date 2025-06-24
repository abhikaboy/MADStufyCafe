@echo off
echo =====================================
echo    Running Jetpack Compose UI Tests
echo =====================================
echo.

echo Cleaning project...
call gradlew clean

echo.
echo Running all UI tests...
call gradlew connectedAndroidTest

echo.
echo =====================================
echo Running individual test suites...
echo =====================================

echo.
echo Running UserProfileScreen tests...
call gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.composeapp.ui.screens.UserProfileScreenTest

echo.
echo Running HomeScreen tests...
call gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.composeapp.ui.screens.HomeScreenTest

echo.
echo Running CafeCard tests...
call gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.composeapp.ui.components.CafeCardTest

echo.
echo Running SearchBar tests...
call gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.composeapp.ui.components.SearchBarTest

echo.
echo Running ReviewCard tests...
call gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.composeapp.ui.components.ReviewCardTest

echo.
echo Running Integration tests...
call gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.composeapp.integration.CafeAppIntegrationTest

echo.
echo =====================================
echo Test execution completed!
echo Check the console output above for results.
echo =====================================
pause 