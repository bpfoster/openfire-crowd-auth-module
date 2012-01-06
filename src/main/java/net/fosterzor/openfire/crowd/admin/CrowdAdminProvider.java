/*
 * Copyright 2011 Ben Foster
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.fosterzor.openfire.crowd.admin;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.admin.AdminProvider;
import org.jivesoftware.openfire.group.Group;
import org.jivesoftware.openfire.group.GroupManager;
import org.jivesoftware.openfire.group.GroupNotFoundException;
import org.jivesoftware.openfire.group.GroupProvider;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 12/22/11
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class CrowdAdminProvider implements AdminProvider {
    private static final Logger logger = LoggerFactory.getLogger(CrowdAdminProvider.class);
    private static final String ADMIN_GROUP_PROP_NAME = "crowdAuth.admin.groups";
    private static final String DEFAULT_ADMIN_GROUP_NAME = "openfire-administrators";
    private static final String CSV_SPLIT_REGEX = "\\s*,\\s*";

    private XMPPServer server = XMPPServer.getInstance();

    @Override
    public List<JID> getAdmins() {
        List<JID> admins = new ArrayList<JID>();
        GroupProvider provider = GroupManager.getInstance().getProvider();

        String groupsString = JiveGlobals.getProperty(ADMIN_GROUP_PROP_NAME, DEFAULT_ADMIN_GROUP_NAME);
        String[] groups = groupsString.split(CSV_SPLIT_REGEX);
        for (String groupName : groups) {
            try {
                Group group = provider.getGroup(groupName);
                admins.addAll(group.getMembers());
            } catch (GroupNotFoundException e) {
                logger.error("Group with name: " + groupName + " was not found");
            }
        }

        if (admins.size() == 0) {
            // Allow "admin" user to still log in just in case
            admins.add(createJID("admin"));
        }

        return admins;
    }

    protected JID createJID(String username) {
        return server.createJID(username, null);
    }

    @Override
    public void setAdmins(List<JID> admins) {
        // Not supported
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }
}
