package net.fosterzor.openfire.crowd.group;

import com.atlassian.crowd.service.client.CrowdClient;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.group.Group;
import org.jivesoftware.openfire.group.GroupNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmpp.packet.JID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 1/6/12
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = {CrowdGroupProvider.class, XMPPServer.class, Group.class})
public class CrowdGroupProviderTest {
    // TODO

    private CrowdGroupProvider groupProvider;
    private CrowdClient crowdClient;
    private XMPPServer xmppServer;

    @Before
    public void setUp() throws Exception {
        crowdClient = mock(CrowdClient.class);
        xmppServer = mock(XMPPServer.class);
        mockStatic(XMPPServer.class);
        when(XMPPServer.getInstance()).thenReturn(xmppServer);
        
        groupProvider = new CrowdGroupProvider(crowdClient);
    }

    @Test
    public void testProperties() throws Exception {
        assertTrue(groupProvider.isReadOnly());
        assertTrue(groupProvider.isSearchSupported());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCreateGroup() throws Exception {
        groupProvider.createGroup("name");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDeleteGroup() throws Exception {
        groupProvider.deleteGroup("name");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetName() throws Exception {
        groupProvider.setName("in1", "in2");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetDescription() throws Exception {
        groupProvider.setDescription("in1", "in2");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddMember() throws Exception {
        groupProvider.addMember("in1", new JID("in2"), true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUpdateMember() throws Exception {
        groupProvider.updateMember("in1", new JID("in2"), true);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDeleteMember() throws Exception {
        groupProvider.deleteMember("in1", new JID("in2"));
    }

    @Test(expected = GroupNotFoundException.class)
    public void testGetGroupNull() throws Exception {
        final String groupName = "group1";

        when(crowdClient.getGroup(groupName)).thenReturn(null);

        groupProvider.getGroup(groupName);
    }

    @Test(expected = GroupNotFoundException.class)
    public void testGetGroupInactive() throws Exception {
        final String groupName = "group1";
        final String groupDescription = "description";

        final com.atlassian.crowd.model.group.Group crowdGroup = mock(com.atlassian.crowd.model.group.Group.class);

        when(crowdClient.getGroup(groupName)).thenReturn(crowdGroup);

        when(crowdGroup.getName()).thenReturn(groupName);
        when(crowdGroup.getDescription()).thenReturn(groupDescription);
        when(crowdGroup.isActive()).thenReturn(false);

        groupProvider.getGroup(groupName);
    }

    @Test
    public void testGetGroup() throws Exception {
        final String groupName = "group1";
        final String groupDescription = "description";

        final List<String> crowdGroupUsers = Arrays.asList("user1", "user2@server");

        final com.atlassian.crowd.model.group.Group crowdGroup = mock(com.atlassian.crowd.model.group.Group.class);
        final Group openfireGroup = mock(Group.class);

        when(crowdClient.getGroup(groupName)).thenReturn(crowdGroup);

        when(crowdGroup.getName()).thenReturn(groupName);
        when(crowdGroup.getDescription()).thenReturn(groupDescription);
        when(crowdGroup.isActive()).thenReturn(true);

        when(crowdClient.getNamesOfUsersOfGroup(groupName, 0, -1)).thenReturn(crowdGroupUsers);

        when(xmppServer.createJID("user1", null)).thenReturn(new JID("user1", "server", null));

        List<JID> memberList = Arrays.asList(new JID("user1", "server", null), new JID("user2@server"));

        whenNew(Group.class).withArguments(eq(groupName), eq(groupDescription), eq(memberList), eq(Collections.<JID>emptyList())).thenReturn(openfireGroup);

        Group group = groupProvider.getGroup(groupName);

        assertNotNull(group);
        assertSame(openfireGroup, group);
    }

    @Test(expected = GroupNotFoundException.class)
    public void testGetGroupNotFound() throws Exception {
        final String groupName = "group";

        when(crowdClient.getGroup(groupName)).thenThrow(new com.atlassian.crowd.exception.GroupNotFoundException(groupName));
        groupProvider.getGroup(groupName);
    }
}
