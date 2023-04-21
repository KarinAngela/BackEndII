node {
  stage("Clone project") {
    git branch: 'master', url: 'https://github.com/KarinAngela/BackEndII.git'
  }

  stage("Build project with test execution") {
    sh "./gradlew build"
  }
}
