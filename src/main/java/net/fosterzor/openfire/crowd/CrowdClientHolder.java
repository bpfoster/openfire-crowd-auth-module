package net.fosterzor.openfire.crowd;

import com.atlassian.crowd.integration.service.AuthenticationManager;
import com.atlassian.crowd.integration.service.GroupManager;
import com.atlassian.crowd.integration.service.GroupMembershipManager;
import com.atlassian.crowd.integration.service.UserManager;
import com.atlassian.crowd.integration.service.soap.client.SecurityServerClient;

/**
 * Interface that allows the various client components of the Crowd plugin to
 * access the Crowd client library.
 *
 * @author Justin Edelson
 *
 */
public interface CrowdClientHolder {
    public SecurityServerClient getSecurityServerClient();

    public UserManager getUserManager();

    public GroupManager getGroupManager();

    public GroupMembershipManager getGroupMembershipManager();

    public AuthenticationManager getAuthenticationManager();

    //public NexusRoleManager getNexusRoleManager();


    public boolean isConfigured();
}

