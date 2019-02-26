node 
{
   try
   {
       notify('Started')
       stage('Preparation') 
       { // for display purposes
          // Get some code from a GitHub repository
          git 'https://github.com/g0t4/jenkins2-course-spring-petclinic.git'
          
       }
       stage('Build')
       {
            sh 'mvn clean package'
       }
       stage('Generate Archive') 
       {
          archiveArtifacts "target/*.?ar"
       }
       stage('Test Results')
       {
           step([$class: 'JUnitResultArchiver', testResults: 'target/surefire-reports/TEST-*.xml'])
       }
       stage('Results') 
       {
          step([$class: 'JUnitResultArchiver', testResults: 'target/surefire-reports/TEST-*.xml'])
          archiveArtifacts "target/*.?ar"
       }
       stage('Email Build Output')
       {
           emailext body: 'Build status ', recipientProviders: [developers()], subject: 'Jenkins Build Notification', to: 'williamkzhou@hotmail.com'
       }
   }
   catch(err)
   {
       
       currentBuild.result = 'FAILURE'
   }
}

def notify(status)
{
    emailext (
      to: "williamkzhou@hotmail.com",
      subject: "${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>""",
    )
}
