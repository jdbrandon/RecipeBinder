pipeline {
    agent {
        // Run on a build agent where we have the Android SDK installed
        label 'android'
    }
    options {
        // Stop the build early in case of compile or test failures
        skipStagesAfterUnstable()
    }
    stages {
        stage('Checkout'){
            steps {
                checkout scm
            }
        }
        stage('Compile') {
            steps {
                // Compile the app and its dependencies
                bat 'gradlew.bat compileDebugSources'
                bat 'gradlew.bat compileReleaseSources'
            }
        }
        stage('Unit test') {
            steps {
                // Compile and run the unit tests for the app and its dependencies
                bat 'gradlew.bat testDebugUnitTest'
                bat 'gradlew.bat testReleaseUnitTest'

                // Analyse the test results and update the build result as appropriate
                junit '**/TEST-*.xml'
            }
        }
        stage('Build APK') {
            steps {
                // Finish building and packaging the APK
                bat 'gradlew.bat assemble'

                // Archive the APKs so that they can be downloaded from Jenkins
                archiveArtifacts '**/*.apk'
            }
        }
        stage('Static analysis') {
            steps {
                // Run Lint and analyse the results
                bat 'gradlew.bat lintDebug'
                bat 'gradlew.bat lintRelease'
                androidLint pattern: '**/lint-results-*.xml'
            }
        }
    }
}