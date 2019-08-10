pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh './gradlew build -x test -x integrationTest'
      }
    }
    stage('Unit Test') {
      steps {
        sh './gradlew test'
      }
      post {
        always {
          junit 'build/test-results/test'
        }
      }
    }
    stage('Integration Tests') {
      steps {
        sh './gradlew integrationTest'
      }
      post {
        always {
          junit 'build/test-results/integrationTest'
        }
      }
    }
  }
}