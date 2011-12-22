/*
 * Copyright (C) 2011 Ben Foster
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
    
    private XMPPServer server = XMPPServer.getInstance();

    @Override
    public List<JID> getAdmins() {
        List<JID> admins = new ArrayList<JID>();
        GroupProvider provider = GroupManager.getInstance().getProvider();

        String groupsString = JiveGlobals.getProperty(ADMIN_GROUP_PROP_NAME, "openfire-administrators");
        String[] groups = groupsString.split("\\s*,\\s");
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
            admins.add(server.createJID("admin", null));
        }

        return admins;
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
