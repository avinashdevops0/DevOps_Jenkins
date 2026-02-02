```pipeline
pipeline {
    agent any

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'monocodes',
                    url: 'https://github.com/avinashdevops0/DevOps_Projects_Practice.git'
            }
        }

        stage('SonarQube Analysis') {
    steps {
        withSonarQubeEnv('mysonar') {
            script {
                def scannerHome = tool 'mysonar'
                sh """
                  ${scannerHome}/bin/sonar-scanner \
                  -Dsonar.projectKey=devops-practice \
                  -Dsonar.projectName="DevOps Practice Project" \
                  -Dsonar.sources=. \
                  -Dsonar.exclusions=node_modules/**
                """
            }
        }
    }
}

    }
}
```
