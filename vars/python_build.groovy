def call(dockerRepoName, imageName) {
    pipeline {
        agent any

        stages {
            stage('Build') {
                steps {
                    sh 'pip install -r requirements.txt'
                }
            }
            stage('Python Lint') {
                steps {
                    sh 'pylint-fail-under --fail_under 5.0 *.py'
                }
            }
            
            stage('Package') {
                when {
                    expression { env.GIT_BRANCH == 'origin/main' }
                }
                steps {
                    withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
                        sh "docker login -u '285400783' -p '$TOKEN' docker.io"
                        sh "docker build -t ${dockerRepoName}:latest --tag 285400783/${dockerRepoName}:${imageName} ."
                        sh "docker push 285400783/${dockerRepoName}:${imageName}"
                    }
                }
            }
            
        }
    }

}
