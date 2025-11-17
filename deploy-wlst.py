# WLST deploy script
# Usage:
#   java weblogic.WLST deploy-wlst.py

adminUrl = 't3://localhost:7001'
adminUser = 'weblogic'
adminPass = 'welcome1'
appName = 'weblogic-openai'
appPath = 'target/weblogic-openai.war'

connect(adminUser, adminPass, adminUrl)
edit()
startEdit()
deploy(appName, appPath, targets='AdminServer', stageMode='nostage', upload='true')
save()
activate()
disconnect()
exit()
