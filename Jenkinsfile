pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'mohyeonman/simple-chat-user:latest'
        GITHUB_CREDENTIALS_ID = 'github-credentials'
        DOCKER_CREDENTIALS_ID = 'docker-credentials' 
    }

    stages {
        stage('Clone Repository') {
            steps {
                echo 'Cloning the repository...'
                checkout([$class: 'GitSCM',
                          branches: [[name: '*/main']],
                          userRemoteConfigs: [[
                              url: 'https://github.com/mohyeonMan/simple-chat-user.git',
                              credentialsId: "${GITHUB_CREDENTIALS_ID}" 
                          ]]
                ])
            }
        }
        stage('Build and Test') {
            steps {

                sh 'chmod +x ./gradlew'
                echo 'Building the project with Gradle...'
                sh './gradlew build'
                echo 'Running tests with Gradle...'
                sh './gradlew test'
            }
        }
        stage('Verify Build Output') {
            steps {
                echo 'Verifying JAR file exists...'
                sh 'ls -l build/libs/'
            }
        }
        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }
        stage('Push Docker Image') {
            steps {
                echo 'Pushing Docker image to Docker Hub...'
                withDockerRegistry([credentialsId: "${DOCKER_CREDENTIALS_ID}"]) {
                    sh "docker push ${DOCKER_IMAGE}"
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Please check the logs.'
        }
    }
}
