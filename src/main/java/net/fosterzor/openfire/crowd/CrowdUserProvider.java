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
import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.search.query.entity.restriction.*;
import com.atlassian.crowd.service.client.CrowdClient;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.openfire.user.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 5/25/11
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class CrowdUserProvider implements UserProvider {
    private static final Logger logger = LoggerFactory.getLogger(CrowdUserProvider.class);
    private static final TermRestriction ACTIVE_TERM_RESTRICTION = new TermRestriction(new PropertyImpl("active", Boolean.class), true);
    private static final Date NOTIME = new Date(0);

    private CrowdClient client;

    public CrowdUserProvider() {
        client = CrowdClientHolder.getClient();
    }

    // For unit testing
    protected CrowdUserProvider(CrowdClient client) {
        this.client = client;
    }

    @Override
    public User loadUser(String username) throws UserNotFoundException {
        User user = null;
        try {
            com.atlassian.crowd.model.user.User crowdUser = client.getUser(username);
            if (crowdUser == null) {
                throw new UserNotFoundException();
            }
            user = new User(crowdUser.getName(), crowdUser.getDisplayName(), crowdUser.getEmailAddress(), NOTIME, NOTIME);
        } catch (com.atlassian.crowd.exception.UserNotFoundException e) {
            throw new UserNotFoundException("User not found", e);
        } catch (OperationFailedException e) {
            throw new UserNotFoundException("OperationFailedException", e);
        } catch (ApplicationPermissionException e) {
            throw new UserNotFoundException("ApplicationPermissionException", e);
        } catch (InvalidAuthenticationException e) {
            throw new UserNotFoundException("InvalidAuthenticationException", e);
        }

        return user;
    }

    @Override
    public User createUser(String username, String password, String name, String email) throws UserAlreadyExistsException {
        // Unsupported
        return null;
    }

    @Override
    public void deleteUser(String username) {
        // Unsupported
    }

    @Override
    public int getUserCount() {
        // TODO: More efficient way?
        return getUsers().size();
    }

    @Override
    public Collection<User> getUsers() {
        return getUsers(0, -1);
    }

    @Override
    public Collection<String> getUsernames() {
        Collection<String> usernames = null;
        try {
            usernames = client.searchUserNames(ACTIVE_TERM_RESTRICTION, 0, -1);
        } catch (OperationFailedException e) {
            logger.error("Error fetching usernames", e);
        } catch (InvalidAuthenticationException e) {
            logger.error("Error fetching usernames", e);
        } catch (ApplicationPermissionException e) {
            logger.error("Error fetching usernames", e);
        }

        return usernames;
    }

    @Override
    public Collection<User> getUsers(int startIndex, int numResults) {
        Collection<User> users = null;

        try {
            List<com.atlassian.crowd.model.user.User> crowdUsers = client.searchUsers(ACTIVE_TERM_RESTRICTION, startIndex, numResults);
            users = transformUsers(crowdUsers);
        } catch (OperationFailedException e) {
            logger.error("Error fetching users", e);
        } catch (InvalidAuthenticationException e) {
            logger.error("Error fetching users", e);
        } catch (ApplicationPermissionException e) {
            logger.error("Error fetching users", e);
        }

        return users;
    }

    @Override
    public void setName(String username, String name) throws UserNotFoundException {
        // Unsupported
    }

    @Override
    public void setEmail(String username, String email) throws UserNotFoundException {
        // Unsupported
    }

    @Override
    public void setCreationDate(String username, Date creationDate) throws UserNotFoundException {
        // Unsupported
    }

    @Override
    public void setModificationDate(String username, Date modificationDate) throws UserNotFoundException {
        // Unsupported
    }

    @Override
    public Set<String> getSearchFields() throws UnsupportedOperationException {
        Set<String> fields = new HashSet<String>(Arrays.asList("name", "email", "firstName", "lastName", "displayName"));

        return fields;
    }

    @Override
    public Collection<User> findUsers(Set<String> fields, String query) throws UnsupportedOperationException {
        return findUsers(fields, query, 0, -1);
    }

    @Override
    public Collection<User> findUsers(Set<String> fields, String query, int startIndex, int numResults) throws UnsupportedOperationException {
        List<SearchRestriction> termRestrictionList = new ArrayList<SearchRestriction>(fields.size());
        for (String field : fields) {
            termRestrictionList.add(new TermRestriction(new PropertyImpl(field, String.class), MatchMode.CONTAINS, query));
        }

        SearchRestriction fieldsRestrictions = new BooleanRestrictionImpl(BooleanRestriction.BooleanLogic.OR, termRestrictionList);

        SearchRestriction restriction = new BooleanRestrictionImpl(BooleanRestriction.BooleanLogic.AND, fieldsRestrictions, ACTIVE_TERM_RESTRICTION);

        Collection<User> users = null;
        try {
            List<com.atlassian.crowd.model.user.User> crowdUsers = client.searchUsers(restriction, startIndex, numResults);
            users = transformUsers(crowdUsers);
        } catch (OperationFailedException e) {
            logger.error("Error searching users", e);
        } catch (InvalidAuthenticationException e) {
            logger.error("Error searching users", e);
        } catch (ApplicationPermissionException e) {
            logger.error("Error searching users", e);
        }

        return users;
    }

    private Collection<User> transformUsers(List<com.atlassian.crowd.model.user.User> crowdUsers) {
        Collection<User> users = new ArrayList<User>();
        for (com.atlassian.crowd.model.user.User crowdUser : crowdUsers) {
            users.add(new User(crowdUser.getName(), crowdUser.getDisplayName(), crowdUser.getEmailAddress(), NOTIME, NOTIME));
        }

        return users;
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public boolean isNameRequired() {
        return true;
    }

    @Override
    public boolean isEmailRequired() {
        return true;
    }
}
