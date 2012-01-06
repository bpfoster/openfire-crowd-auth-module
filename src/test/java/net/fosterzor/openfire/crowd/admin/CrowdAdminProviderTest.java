package net.fosterzor.openfire.crowd.admin;

import org.jivesoftware.openfire.group.Group;
import org.jivesoftware.openfire.group.GroupManager;
import org.jivesoftware.openfire.group.GroupNotFoundException;
import org.jivesoftware.openfire.group.GroupProvider;
import org.jivesoftware.util.JiveGlobals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmpp.packet.JID;

import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
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
@PrepareForTest(value = {CrowdAdminProvider.class, GroupManager.class, JiveGlobals.class})
public class CrowdAdminProviderTest {

    private CrowdAdminProvider adminProvider;

    @Before
    public void setUp() {
        adminProvider = new TestableCrowdAdminProvider();
    }

    @Test
    public void testProperties() throws Exception {
        assertTrue(adminProvider.isReadOnly());
    }

    @Test
    public void testGetAdmins() throws Exception {
        GroupManager groupManager = mock(GroupManager.class);
        GroupProvider groupProvider = mock(GroupProvider.class);
        Group group = mock(Group.class);

        mockStatic(GroupManager.class);
        when(GroupManager.getInstance()).thenReturn(groupManager);

        mockStatic(JiveGlobals.class);
        when(JiveGlobals.getProperty(eq("crowdAuth.admin.groups"), anyString())).thenReturn("group1, group2 ,group3,group4");

        when(groupManager.getProvider()).thenReturn(groupProvider);

        when(groupProvider.getGroup(Mockito.matches("(group1)|(group2)|(group3)"))).thenReturn(group);
        when(groupProvider.getGroup("group4")).thenThrow(new GroupNotFoundException());

        when(group.getMembers()).thenReturn(Collections.singleton(new JID("somebody")));


        List<JID> admins = adminProvider.getAdmins();

        assertEquals(3, admins.size());

        assertEquals(new JID("somebody"), admins.get(0));
    }

    @Test
    public void testEmptyAdminResults() throws Exception {
        GroupManager groupManager = mock(GroupManager.class);
        GroupProvider groupProvider = mock(GroupProvider.class);

        mockStatic(GroupManager.class);
        when(GroupManager.getInstance()).thenReturn(groupManager);

        mockStatic(JiveGlobals.class);
        when(JiveGlobals.getProperty(eq("crowdAuth.admin.groups"), anyString())).thenReturn("group1");

        when(groupManager.getProvider()).thenReturn(groupProvider);

        when(groupProvider.getGroup("group1")).thenThrow(new GroupNotFoundException());


        List<JID> admins = adminProvider.getAdmins();

        assertEquals(1, admins.size());

        assertEquals(new JID("admin"), admins.get(0));
    }

    private class TestableCrowdAdminProvider extends CrowdAdminProvider {
        @Override
        protected JID createJID(String username) {
            return new JID(username);
        }
    }
}

