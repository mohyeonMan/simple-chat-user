pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'mohyeonman/simple-chat-user:latest'
        GITHUB_CREDENTIALS_ID = 'github-credentials'
        DOCKER_CREDENTIALS_ID = 'docker-credentials'
        SWARM_MANAGER = 'simple-chat-user-ssh'
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

         stage('Prepare application.yml') {
            steps {
                echo 'Preparing application.yml...'
                withCredentials([file(credentialsId: 'application-user', variable: 'application')]) {
                    script {
                        // Ensure the directory exists
                        sh '''
                            mkdir -p src/main/resources
                            cp $application src/main/resources/application.yml
                        '''
                    }
                }
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
                withCredentials([usernamePassword(credentialsId: 'docker-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    script {
                        sh '''
                            echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin
                            docker push ${DOCKER_IMAGE}
                            docker logout
                        '''
                    }
                }
            }
        }

        stage('Deploy to Swarm') {
            steps {
                withCredentials([
                    file(credentialsId: 'simple-chat-user-pem', variable: 'PEM_FILE'),
                    usernamePassword(credentialsId: 'simple-chat-user-ssh', usernameVariable: 'SSH_USER', passwordVariable: 'SSH_SERVER')
                ]) {
                    sh """
                    ssh -i ${PEM_FILE} -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_SERVER} <<EOF
                    docker service update --image ${DOCKER_IMAGE} simple-chat-user || \
                    docker service create --name simple-chat-user --replicas 1 -p 8080:8080 ${DOCKER_IMAGE}
                    EOF
                    """
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
