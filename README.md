Openfire Crowd Auth Module
=============

This is a module that allows Openfire to authenticate and retrieve user and group information from Atlassian Crowd.



INSTALLATION
------------
To engage the module, follow these steps:

1. Download the openfire-crowd-auth-module jar and place it in your Openfire installation under the /lib folder
2. Copy the crowd-integration-client-*.jar to the lib folder as well
3. Copy crowd.properties and crowd-ehcache.xml from crowd/client/conf to openfire/conf and configure it
4. Boot up Openfire and configure the following System Properties within Openfire:

		provider.admin.className = net.fosterzor.openfire.crowd.admin.CrowdAdminProvider
		provider.auth.className = net.fosterzor.openfire.crowd.auth.CrowdAuthProvider
		provider.group.className = net.fosterzor.openfire.crowd.group.CrowdGroupProvider
		provider.user.className = net.fosterzor.openfire.crowd.CrowdUserProvider

5. Additionally, set the property `crowdAuth.admin.groups` to a comma-separated value of groups in Crowd that should be given administrator rights within Openfire.  The default value is `openfire-administrators`
