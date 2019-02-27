stage 'CI'
node {

    git branch: 'jenkins2-course', 
        url: 'https://github.com/g0t4/solitaire-systemjs-course'

    // pull dependencies from npm
    // on windows use: bat 'npm install'
    sh 'npm install'

    // stash code & dependencies to expedite subsequent testing
    // and ensure same code & dependencies are used throughout the pipeline
    // stash is a temporary archive
    stash name: 'everything', 
          excludes: 'test-results/**', 
          includes: '**'
    
    // test with PhantomJS for "fast" "generic" results
    // on windows use: bat 'npm run test-single-run -- --browsers PhantomJS'
    sh 'npm run test-single-run -- --browsers PhantomJS'
    
    // archive karma test results (karma is configured to export junit xml files)
    step([$class: 'JUnitResultArchiver', 
          testResults: 'test-results/**/test-results.xml'])
          
}

node 
{
    sh 'ls -la'
    sh 'rm -rf *'
    unstash 'everything'
    sh 'ls -la'
    //sh 'npm run test-single-run -- --browsers Chrome'
}

stage 'Browser Testing'
{
    parallel = 
    chrome: 
    {
        runTests("Chrome")
    }, 
    firefox: 
    {
        runTests("Firefox")
    }
    safari: 
    {
        runTests("Safari")
    }
}

def runTests(browser)
{
    node
    {
        sh 'rm -rf *'
        unstash 'everything'
        sh "npm run-test-single-run -- -- browsers ${browser}"
        step([$class: 'JUnitResultsArchiver', testResults: 'test-results/**/test-results.xml'])
    }
}

def notify(status){
    emailext (
      to: "wesmdemos@gmail.com",
      subject: "${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>""",
    )
}
