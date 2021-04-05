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
                sh './gradlew compileDebugSources'
                sh './gradlew compileReleaseSources'
            }
        }
        stage('Unit test') {
            steps {
                // Clear old reports
                sh 'rm -rf ./RecipeBinderApp/build/test-results/*'
                sh 'rm -rf ./RecipeBinderApp/build/reports/*'
                // Compile and run the unit tests for the app and its dependencies
                sh './gradlew testDebugUnitTest'
                sh './gradlew testReleaseUnitTest'

                // Analyse the test results and update the build result as appropriate
                junit '**/TEST-*.xml'
            }
        }
        stage('Build APK') {
            steps {
                // Finish building and packaging the APK
                sh './gradlew assemble'

                // Archive the APKs so that they can be downloaded from Jenkins
                archiveArtifacts '**/*.apk'
            }
        }
        stage('Static analysis') {
            steps {
                // Run Lint and analyse the results
                sh './gradlew lintDebug'
                sh './gradlew lintRelease'
                archiveArtifacts '**/*lint-results-release.html'
            }
        }
    }
}
