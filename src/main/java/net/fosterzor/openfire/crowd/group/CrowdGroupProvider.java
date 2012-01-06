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

package net.fosterzor.openfire.crowd.group;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.search.query.entity.restriction.*;
import com.atlassian.crowd.service.client.CrowdClient;
import net.fosterzor.openfire.crowd.CrowdClientHolder;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.group.Group;
import org.jivesoftware.openfire.group.GroupAlreadyExistsException;
import org.jivesoftware.openfire.group.GroupNotFoundException;
import org.jivesoftware.openfire.group.GroupProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 5/25/11
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class CrowdGroupProvider implements GroupProvider {
    private static final Logger logger = LoggerFactory.getLogger(CrowdGroupProvider.class);
    private static final TermRestriction<Boolean> ACTIVE_TERM_RESTRICTION = new TermRestriction<Boolean>(new PropertyImpl<Boolean>("active", Boolean.class), true);
    private static final Property<String> NAME_PROPERTY = new PropertyImpl<String>("name", String.class);

    private XMPPServer server = XMPPServer.getInstance();
    private CrowdClient client;


    public CrowdGroupProvider() {
        client = CrowdClientHolder.getClient();
    }

    // For unit testing only
    protected CrowdGroupProvider(CrowdClient client) {
        this.client = client;
    }

    @Override
    public Group createGroup(String s) throws UnsupportedOperationException, GroupAlreadyExistsException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteGroup(String s) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Group getGroup(String name) throws GroupNotFoundException {
        Group group = null;
        try {
            com.atlassian.crowd.model.group.Group crowdGroup = client.getGroup(name);

            if (crowdGroup == null || !crowdGroup.isActive()) {
                throw new GroupNotFoundException("Group " + name + " not found");
            }
            String crowdName = crowdGroup.getName();
            String description = crowdGroup.getDescription();

            int startIndex = 0;
            int fetchSize = -1;

            List<String> namesOfUsersOfGroup = client.getNamesOfUsersOfGroup(crowdName, startIndex, fetchSize);
            List<JID> membersJid = new ArrayList<JID>(namesOfUsersOfGroup.size());
            for (String member : namesOfUsersOfGroup) {
                JID userJID;
                if (member.indexOf('@') == -1) {
                    // Create JID of local user if JID does not match a component's JID
                    userJID = server.createJID(member, null);
                } else {
                    userJID = new JID(member);
                }
                membersJid.add(userJID);
            }

            group = new Group(name, description, membersJid, Collections.<JID>emptyList());

        } catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
            throw new GroupNotFoundException("Group " + name + " not found", e);
        } catch (OperationFailedException e) {
            logger.error("Error getting group", e);
        } catch (InvalidAuthenticationException e) {
            logger.error("Error getting group", e);
        } catch (ApplicationPermissionException e) {
            logger.error("Error getting group", e);
        }

        return group;
    }

    @Override
    public void setName(String s, String s1) throws UnsupportedOperationException, GroupAlreadyExistsException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDescription(String s, String s1) throws GroupNotFoundException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getGroupCount() {
        return getGroupNames().size();
    }

    @Override
    public Collection<String> getGroupNames() {
        return getGroupNames(0, -1);
    }

    @Override
    public Collection<String> getSharedGroupsNames() {
        // TODO: Is this right?
        return getGroupNames();
    }

    @Override
    public Collection<String> getGroupNames(int startIndex, int numResults) {
        Collection<String> groups = null;
        try {
            groups = client.searchGroupNames(ACTIVE_TERM_RESTRICTION, startIndex, numResults);
        } catch (OperationFailedException e) {
            logger.error("Error getting group names", e);
        } catch (InvalidAuthenticationException e) {
            logger.error("Error getting group names", e);
        } catch (ApplicationPermissionException e) {
            logger.error("Error getting group names", e);
        }
        return groups;
    }

    @Override
    public Collection<String> getGroupNames(JID user) {
        Collection<String> groupNames = null;

        try {
            groupNames = client.getNamesOfGroupsForUser(user.getNode(), 0, -1);
        } catch (ApplicationPermissionException e) {
            logger.error("Error getting group names", e);
        } catch (UserNotFoundException e) {
            logger.error("Error getting group names", e);
        } catch (OperationFailedException e) {
            logger.error("Error getting group names", e);
        } catch (InvalidAuthenticationException e) {
            logger.error("Error getting group names", e);
        }

        return groupNames;
    }

    @Override
    public void addMember(String s, JID jid, boolean b) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateMember(String s, JID jid, boolean b) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteMember(String s, JID jid) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public Collection<String> search(String query) {
        return search(query, 0, -1);
    }

    @Override
    public Collection<String> search(String query, int startIndex, int numResults) {
        Collection<String> groupNames = null;
        SearchRestriction nameRestriction = new TermRestriction<String>(NAME_PROPERTY, query);
        SearchRestriction restriction = new BooleanRestrictionImpl(BooleanRestriction.BooleanLogic.AND, nameRestriction, ACTIVE_TERM_RESTRICTION);
        try {
            groupNames = client.searchGroupNames(restriction, startIndex, numResults);
        } catch (OperationFailedException e) {
            logger.error("Error searching groups", e);
        } catch (InvalidAuthenticationException e) {
            logger.error("Error searching groups", e);
        } catch (ApplicationPermissionException e) {
            logger.error("Error searching groups", e);
        }

        return groupNames;
    }

    @Override
    public boolean isSearchSupported() {
        return true;
    }
}
