node {
  stage("Clone project") {
    git branch: 'master', url: 'https://github.com/KarinAngela/BackEndII.git'
  }

  stage("Build") {
    sh "./mvnw -B -DskipTests clean package"
  }

  stage("Test") {
    sh "./mvnw clean test"
  }
}
