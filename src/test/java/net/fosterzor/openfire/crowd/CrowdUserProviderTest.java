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

package net.fosterzor.openfire.crowd;

import com.atlassian.crowd.service.client.CrowdClient;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 12/23/11
 * Time: 9:00 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(JMock.class)
public class CrowdUserProviderTest {
    private Mockery context = new JUnit4Mockery();
    private CrowdClient crowdClient;

    private CrowdUserProvider userProvider;

    @Before
    public void setUp() {
        crowdClient = context.mock(CrowdClient.class);
        userProvider = new CrowdUserProvider(crowdClient);
    }

    @Test
    public void testProperties() {
        assertTrue(userProvider.isReadOnly());
        assertTrue(userProvider.isNameRequired());
        assertTrue(userProvider.isEmailRequired());
    }


    @Test(expected = UserNotFoundException.class)
    public void testLoadNotFoundUser() throws Exception {
        final String username = "nobody";
        context.checking(new Expectations() {{
            one(crowdClient).getUser(username);
            will(returnValue(null));
        }});
        userProvider.loadUser(username);
    }

}
