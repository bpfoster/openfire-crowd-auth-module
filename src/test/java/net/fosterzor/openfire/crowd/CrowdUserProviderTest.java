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

package net.fosterzor.openfire.crowd;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.search.query.entity.restriction.*;
import com.atlassian.crowd.service.client.CrowdClient;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static junit.framework.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 12/23/11
 * Time: 9:00 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = CrowdUserProvider.class)
public class CrowdUserProviderTest {
    private static final TermRestriction<Boolean> ACTIVE_TERM_RESTRICTION = new TermRestriction<Boolean>(new PropertyImpl<Boolean>("active", Boolean.class), true);
    private static final Date NOTIME = new Date(0);

    private CrowdClient crowdClient;

    private CrowdUserProvider userProvider;

    @Before
    public void setUp() {
        crowdClient = mock(CrowdClient.class);
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

        when(crowdClient.getUser(username)).thenReturn(null);

        userProvider.loadUser(username);
    }

    @Test
    public void testLoadUser() throws Exception {
        final String username = "mrbean";

        final com.atlassian.crowd.model.user.User crowdUser = mock(com.atlassian.crowd.model.user.User.class);
        final User openfireUser = mock(User.class);

        final String name = "mrbean";
        final String displayName = "Rowan Bean";
        final String email = "mrbean@mrbean.com";

        when(crowdUser.getName()).thenReturn(name);
        when(crowdUser.getDisplayName()).thenReturn(displayName);
        when(crowdUser.getEmailAddress()).thenReturn(email);

        when(crowdClient.getUser(username)).thenReturn(crowdUser);

        whenNew(User.class).withArguments(name, displayName, email, NOTIME, NOTIME).thenReturn(openfireUser);


        User returnedUser = userProvider.loadUser(username);


        assertNotNull(returnedUser);
        assertSame(openfireUser, returnedUser);
    }

    @Test
    public void testGetUsernames() throws Exception {
        List collection = mock(List.class);
        when(crowdClient.searchUserNames(ACTIVE_TERM_RESTRICTION, 0, -1)).thenReturn(collection);

        userProvider.getUsernames();
    }

    @Test
    public void testGetUsernamesFailed() throws Exception {
        when(crowdClient.searchUserNames(ACTIVE_TERM_RESTRICTION, 0, -1)).thenThrow(new OperationFailedException());

        Collection<String> usernames = userProvider.getUsernames();
        assertNull(usernames);
    }

    @Test
    public void testGetUsers() throws Exception {
        final int startIndex = 10;
        final int numResults = 20;

        final String name = "mrbean";
        final String displayName = "Rowan Bean";
        final String email = "mrbean@mrbean.com";

        final com.atlassian.crowd.model.user.User crowdUser = mock(com.atlassian.crowd.model.user.User.class);
        final User openfireUser = mock(User.class);

        when(crowdUser.getName()).thenReturn(name);
        when(crowdUser.getDisplayName()).thenReturn(displayName);
        when(crowdUser.getEmailAddress()).thenReturn(email);

        List<com.atlassian.crowd.model.user.User> crowdUserList = new ArrayList<com.atlassian.crowd.model.user.User>();
        crowdUserList.add(crowdUser);

        when(crowdClient.searchUsers(ACTIVE_TERM_RESTRICTION, startIndex, numResults)).thenReturn(crowdUserList);

        whenNew(User.class).withArguments(name, displayName, email, NOTIME, NOTIME).thenReturn(openfireUser);


        Collection<User> users = userProvider.getUsers(startIndex, numResults);


        assertEquals(1, users.size());
        assertSame(openfireUser, users.iterator().next());
    }

    @Test
    public void testGetUsersFail() throws Exception {
        final int startIndex = 10;
        final int numResults = 20;

        when(crowdClient.searchUsers(ACTIVE_TERM_RESTRICTION, startIndex, numResults)).thenThrow(new OperationFailedException());

        Collection<User> users = userProvider.getUsers(startIndex, numResults);

        assertNull(users);
    }

    @Test
    public void testFindUsers() throws Exception {
        final String name = "mrbean";
        final String displayName = "Rowan Bean";
        final String email = "mrbean@mrbean.com";

        final Set<String> fields = new HashSet<String>(Arrays.asList("Username", "invalid"));

        final String query = "mrbean";
        final int startIndex = 10;
        final int numResults = 20;


        final com.atlassian.crowd.model.user.User crowdUser = mock(com.atlassian.crowd.model.user.User.class);
        final User openfireUser = mock(User.class);

        when(crowdUser.getName()).thenReturn(name);
        when(crowdUser.getDisplayName()).thenReturn(displayName);
        when(crowdUser.getEmailAddress()).thenReturn(email);

        List<com.atlassian.crowd.model.user.User> crowdUserList = new ArrayList<com.atlassian.crowd.model.user.User>();
        crowdUserList.add(crowdUser);


        List<SearchRestriction> termRestrictionList = new ArrayList<SearchRestriction>(1);
        termRestrictionList.add(new TermRestriction<String>(new PropertyImpl<String>("name", String.class), MatchMode.CONTAINS, query));

        SearchRestriction fieldsRestrictions = new BooleanRestrictionImpl(BooleanRestriction.BooleanLogic.OR, termRestrictionList);

        SearchRestriction restriction = new BooleanRestrictionImpl(BooleanRestriction.BooleanLogic.AND, fieldsRestrictions, ACTIVE_TERM_RESTRICTION);


        when(crowdClient.searchUsers(restriction, startIndex, numResults)).thenReturn(crowdUserList);
        whenNew(User.class).withArguments(name, displayName, email, NOTIME, NOTIME).thenReturn(openfireUser);

        Collection<User> users = userProvider.findUsers(fields, query, startIndex, numResults);
        assertEquals(1, users.size());
    }

}
