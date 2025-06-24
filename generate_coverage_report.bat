@echo off
echo ====================================
echo Generating Test Coverage Report
echo ====================================

echo.
echo Running unit tests with coverage...
call gradlew.bat clean testDebugUnitTest

echo.
echo Generating Jacoco coverage report...
call gradlew.bat jacocoTestReport

echo.
echo Coverage report generated!
echo HTML report: app/build/reports/jacoco/jacocoTestReport/html/index.html
echo XML report: app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml

echo.
echo Opening coverage report in browser...
start "" "app/build/reports/jacoco/jacocoTestReport/html/index.html"

echo.
echo ====================================
echo Coverage Report Generation Complete!
echo ====================================
pause 