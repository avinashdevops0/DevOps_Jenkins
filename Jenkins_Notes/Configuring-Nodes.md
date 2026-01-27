```jenkinsfile 
pipeline {
    agent { label 'node || web ' }
    stages {
        stage ("Clean Ws") {
            steps {
                cleanWs()
            }
        }
    }
}
```