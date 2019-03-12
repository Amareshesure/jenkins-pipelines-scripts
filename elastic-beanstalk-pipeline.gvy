wait: false

// Pull build_url into a variable so it can be used
def buildUrl = env.BUILD_URL
number = currentBuild.displayName
currentBuild.displayName = "${number} - ${version} ${app}"

node 
{
    
  currentworking = pwd()

  stage 'Test Deploy'
  build job: 'Jon_test_ci_build', parameters: [[$class: 'ExtendedChoiceParameterValue', name: 'app', value: app], [$class: 'ExtendedChoiceParameterValue', name: 'version', value: version], [$class: 'ExtendedChoiceParameterValue', name: 'source_profile', value: 'dev'], [$class: 'ExtendedChoiceParameterValue', name: 'destination_profile', value: 'test'], [$class: 'ExtendedChoiceParameterValue', name: 'environment', value: environment]]

  stage 'Prep Deploy'

  hipchatSend color: 'PURPLE', credentialId: '', message: 'Testing sign off required for deployment of $version to $app in environment PREP - $BUILD_URL/console', notify: false, room: '', sendAs: '', server: '', textFormat: true, v2enabled: true
  input message: "Testing sign off for deployment of $version to $app prep?", ok: 'Approved', submitter: 'TiaUpdates'
  hipchatSend color: 'PURPLE', credentialId: '', message: 'Platform Services sign off required for deployment of $version to $app in environment PREP - $BUILD_URL/console', notify: true, room: '', sendAs: '', server: '', textFormat: true, v2enabled: true
  input message: "Platform sign off for deployment of $version to $app prep?", ok: 'Deploy', submitter: 'TiaUpdates'
  build job: 'Jon_test_ci_build', parameters: [[$class: 'ExtendedChoiceParameterValue', name: 'app', value: app], [$class: 'ExtendedChoiceParameterValue', name: 'version', value: version], [$class: 'ExtendedChoiceParameterValue', name: 'source_profile', value: 'test'], [$class: 'ExtendedChoiceParameterValue', name: 'destination_profile', value: 'prep'], [$class: 'ExtendedChoiceParameterValue', name: 'environment', value: 'prep']]

  stage 'Prod Deploy'
  hipchatSend color: 'PURPLE', credentialId: '', message: 'Testing sign off required for deployment of $version to $app in environment PROD - $BUILD_URL/console', notify: true, room: '', sendAs: '', server: '', textFormat: true, v2enabled: true
  input message: "Testing sign off for deployment of $version to $app prod?", ok: 'Approved', submitter: 'TiaUpdates'
  hipchatSend color: 'PURPLE', credentialId: '', message: 'Delivery Manager sign off required for deployment of $version to $app in environment PROD - $BUILD_URL/console', notify: true, room: '', sendAs: '', server: '', textFormat: true, v2enabled: true
  input message: "Delivery Manager sign off for deployment of $version to $app prod?", ok: 'Approved', submitter: 'TiaUpdates'
  hipchatSend color: 'PURPLE', credentialId: '', message: 'Platform Services sign off required for deployment of $version to $app in environment PROD - $BUILD_URL/console', notify: true, room: '', sendAs: '', server: '', textFormat: true, v2enabled: true
  input message: "Platform sign off for deployment of $version to $app prod?", ok: 'Deploy', submitter: 'TiaUpdates'
  build job: 'Jon_test_ci_build', parameters: [[$class: 'ExtendedChoiceParameterValue', name: 'app', value: app], [$class: 'ExtendedChoiceParameterValue', name: 'version', value: version], [$class: 'ExtendedChoiceParameterValue', name: 'source_profile', value: 'prep'], [$class: 'ExtendedChoiceParameterValue', name: 'destination_profile', value: 'prod'], [$class: 'ExtendedChoiceParameterValue', name: 'environment', value: 'prod']]

}
