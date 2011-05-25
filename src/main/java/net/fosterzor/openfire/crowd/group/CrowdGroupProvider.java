package net.fosterzor.openfire.crowd.group;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.exception.UserNotFoundException;
import com.atlassian.crowd.search.query.entity.restriction.TermRestriction;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.service.client.CrowdClient;
import net.fosterzor.openfire.crowd.CrowdClientHolder;
import org.jivesoftware.openfire.group.Group;
import org.jivesoftware.openfire.group.GroupAlreadyExistsException;
import org.jivesoftware.openfire.group.GroupNotFoundException;
import org.jivesoftware.openfire.group.GroupProvider;
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
    private CrowdClient client;

    public CrowdGroupProvider() {
        client = CrowdClientHolder.getClient();
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
        //try {
        try {
            com.atlassian.crowd.model.group.Group crowdGroup = client.getGroup(name);

            if (crowdGroup == null) {
                throw new GroupNotFoundException("Group " + name + " not found");
            }
            String name1 = crowdGroup.getName();
            String description = crowdGroup.getDescription();

            int startIndex = 0;
            int fetchSize = -1;

            List<String> namesOfUsersOfGroup = client.getNamesOfUsersOfGroup(name1, startIndex, fetchSize);
            List<JID> membersJid = new ArrayList<JID>(namesOfUsersOfGroup.size());
            for (String member : namesOfUsersOfGroup) {
                membersJid.add(new JID(member));
            }

            group = new Group(name, description, membersJid, Collections.<JID>emptyList());

        } catch (com.atlassian.crowd.exception.GroupNotFoundException e) {
            throw new GroupNotFoundException("Group " + name + " not found", e);
        } catch (OperationFailedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidAuthenticationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ApplicationPermissionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
        // TOOD: Is this right?
        return getGroupNames();
    }

    @Override
    public Collection<String> getGroupNames(int startIndex, int numResults) {
        Collection<String> groups = null;
        try {
            groups = client.searchGroupNames(null, startIndex, numResults);
        } catch (OperationFailedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidAuthenticationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ApplicationPermissionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return groups;
    }

    @Override
    public Collection<String> getGroupNames(JID user) {
        // TODO user.getNode()?
        Collection<String> groupNames = null;

        try {
            groupNames = client.getNamesOfGroupsForUser(user.getNode(), 0, -1);
        } catch (ApplicationPermissionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UserNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (OperationFailedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidAuthenticationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
        // TODO: I really dunno about this SearchRestriction
        Collection<String> groupNames = null;
        SearchRestriction restriction = new TermRestriction(GroupTermKeys.NAME, query);
        try {
            groupNames = client.searchGroupNames(restriction, startIndex, numResults);
        } catch (OperationFailedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidAuthenticationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ApplicationPermissionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        
        return groupNames;
    }

    @Override
    public boolean isSearchSupported() {
        return true;
    }
}
