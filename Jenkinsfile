node {
  stage("Clone project") {
    git branch: 'master', url: 'https://github.com/KarinAngela/BackEndII.git'
  }

  stage("Build") {
    mvn -B -DskipTests clean package
  }

  stage("Test") {
    mvn test
  }
}
