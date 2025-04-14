#!/usr/bin/env groovy

def COLOR_MAP = [
    'SUCCESS': 'good', 
    'FAILURE': 'danger',
    'UNSTABLE': 'warning'
]

pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'your-registry.com'
        APP_NAME = 'fraud-detection'
        SONAR_HOST = credentials('sonar-host')
        DOCKER_CREDS = credentials('docker-credentials')
        KUBE_CONFIG = credentials('kubeconfig')
    }

    options {
        timeout(time: 1, unit: 'HOURS')
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    env.GIT_BRANCH = env.BRANCH_NAME
                    env.ENVIRONMENT = branch2env(env.GIT_BRANCH)
                    env.VERSION = getVersion()
                }
                sh 'chmod +x jenkins/scripts/*.sh'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
                sh './jenkins/scripts/build.sh'
            }
        }

        stage('Test') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        sh './gradlew test'
                    }
                }
                stage('Integration Tests') {
                    steps {
                        sh './gradlew integrationTest'
                    }
                }
            }
        }

        stage('Security Scan') {
            parallel {
                stage('SonarQube Analysis') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            sh './gradlew sonarqube'
                        }
                    }
                }
                stage('OWASP Dependency Check') {
                    steps {
                        sh './gradlew dependencyCheckAnalyze'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                script {
                    dir('jenkins/docker') {
                        sh "./build-images.sh ${env.VERSION}"
                    }
                }
            }
        }

        stage('Push Docker Images') {
            steps {
                script {
                    docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-credentials') {
                        sh "./jenkins/scripts/push-images.sh ${env.VERSION}"
                    }
                }
            }
        }

        stage('Deploy to Development') {
            when { branch 'develop' }
            steps {
                sh "./jenkins/scripts/deploy.sh development ${env.VERSION}"
            }
        }

        stage('Deploy to Staging') {
            when { branch 'staging' }
            environment {
                APPROVAL_REQUIRED = 'true'
            }
            steps {
                milestone(1)
                input message: 'Deploy to Staging?'
                sh "./jenkins/scripts/deploy.sh staging ${env.VERSION}"
            }
        }

        stage('Deploy to Production') {
            when { branch 'main' }
            environment {
                APPROVAL_REQUIRED = 'true'
            }
            steps {
                milestone(1)
                input message: 'Deploy to Production?'
                sh "./jenkins/scripts/deploy.sh production ${env.VERSION}"
            }
        }

        stage('Validation') {
            steps {
                sh "./jenkins/scripts/validate-deployment.sh ${env.ENVIRONMENT}"
            }
        }
    }

    post {
        always {
            junit '**/build/test-results/test/*.xml'
            cleanWs()
        }
        success {
            notifySlack("${env.JOB_NAME} - #${env.BUILD_NUMBER} Success", 'good')
        }
        failure {
            notifySlack("${env.JOB_NAME} - #${env.BUILD_NUMBER} Failure", 'danger')
        }
    }
}

def branch2env(String branch) {
    switch(branch) {
        case 'main': return 'production'
        case 'staging': return 'staging'
        case 'develop': return 'development'
        default: return 'development'
    }
}

def getVersion() {
    def version = sh(script: './gradlew -q printVersion', returnStdout: true).trim()
    return "${version}-${env.BUILD_NUMBER}"
}

def notifySlack(String message, String color) {
    slackSend(
        channel: '#deployments',
        color: color,
        message: "${message}\nEnvironment: ${env.ENVIRONMENT}\nBranch: ${env.GIT_BRANCH}\nBuild: ${env.BUILD_URL}"
    )
}