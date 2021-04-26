pipeline {
    agent {
        // Run on a build agent where we have the Android SDK installed
        label 'android'
    }
    stages {
        stage('Checkout'){
            steps {
                checkout scm
                sh './gradlew clean'
            }
        }
        stage('Compile') {
            steps {
                // Compile the app and its dependencies
                sh './gradlew compileStandardDebugSources'
                sh './gradlew compileStandardReleaseSources'
            }
        }
        stage('Unit test') {
            steps {
                // Clear old reports
                sh 'rm -rf ./RecipeBinderApp/build/test-results/*'
                sh 'rm -rf ./RecipeBinderApp/build/reports/*'
                // Compile and run the unit tests for the app and its dependencies
                sh './gradlew test --continue'

                // Analyse the test results and update the build result as appropriate
                junit '**/TEST-*.xml'
            }
        }
        stage('Build APK') {
            steps {
                // Finish building and packaging the APK
                sh './gradlew assembleStandard'

                // Archive the APKs so that they can be downloaded from Jenkins
                archiveArtifacts '**/*.apk'
            }
        }
        stage('Static analysis') {
            steps {
                // Run Lint and analyse the results
                sh './gradlew lintStandardDebug'
                sh './gradlew lintStandardRelease'
                sh './gradlew detekt'
                recordIssues(
                  enabledForFailure: true,
                  aggregatingResults: true,
                  tools: [
                    androidLintParser(pattern: '**/*lint-results*.xml'),
                    detekt(pattern: '**/detekt.xml'),
                  ]
                )
            }
        }
    }
}
