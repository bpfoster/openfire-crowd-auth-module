package net.fosterzor.openfire.crowd.group;

import com.atlassian.crowd.integration.exception.InvalidAuthorizationTokenException;
import com.atlassian.crowd.integration.exception.ObjectNotFoundException;
import com.atlassian.crowd.integration.service.GroupManager;
import com.atlassian.crowd.integration.service.GroupMembershipManager;
import com.atlassian.crowd.integration.soap.SOAPGroup;
import org.jivesoftware.openfire.group.Group;
import org.jivesoftware.openfire.group.GroupAlreadyExistsException;
import org.jivesoftware.openfire.group.GroupNotFoundException;
import org.jivesoftware.openfire.group.GroupProvider;
import org.xmpp.packet.JID;

import java.rmi.RemoteException;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 5/25/11
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class CrowdGroupProvider implements GroupProvider {
    private GroupManager groupManager;
    private GroupMembershipManager groupMembershipManager;

    @Override
    public Group createGroup(String s) throws UnsupportedOperationException, GroupAlreadyExistsException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteGroup(String s) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Group getGroup(String s) throws GroupNotFoundException {
        try {
            SOAPGroup soapGroup = groupManager.getGroup(s);
        } catch (RemoteException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidAuthorizationTokenException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ObjectNotFoundException e) {
            throw new GroupNotFoundException("Group not found", e);
        }

        Group group = new Group();
        group.
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
        // TODO: Auto-generated method
        return 0;
    }

    @Override
    public Collection<String> getGroupNames() {
        // TODO: Auto-generated method
        return null;
    }

    @Override
    public Collection<String> getSharedGroupsNames() {
        // TODO: Auto-generated method
        return null;
    }

    @Override
    public Collection<String> getGroupNames(int i, int i1) {
        // TODO: Auto-generated method
        return null;
    }

    @Override
    public Collection<String> getGroupNames(JID jid) {
        // TODO: Auto-generated method
        return null;
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
    public Collection<String> search(String s) {
        // TODO: Auto-generated method
        return null;
    }

    @Override
    public Collection<String> search(String s, int i, int i1) {
        // TODO: Auto-generated method
        return null;
    }

    @Override
    public boolean isSearchSupported() {
        // TODO: Auto-generated method
        return false;
    }
}
