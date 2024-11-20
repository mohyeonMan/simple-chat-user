pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'mohyeonman/simple-chat-user:latest'
        DOCKER_CREDENTIALS_ID = 'docker_id_mohyeonman'
    }

    stages {
        stage('Clone Repository') {
            steps {
                echo 'Cloning the repository...'
                checkout scm
            }
        }
        stage('Build') {
            steps {
                echo 'Building the project with Gradle...'
                sh './gradlew build'
            }
        }
        stage('Test') {
            steps {
                echo 'Running tests with Gradle...'
                sh './gradlew test'
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
