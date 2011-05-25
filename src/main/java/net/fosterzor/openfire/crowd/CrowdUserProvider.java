package net.fosterzor.openfire.crowd;

import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.search.query.entity.restriction.NullRestrictionImpl;
import com.atlassian.crowd.service.client.CrowdClient;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.openfire.user.UserProvider;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 5/25/11
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class CrowdUserProvider implements UserProvider {
    private static final Date NOTIME = new Date(0);

    private CrowdClient client;

    public CrowdUserProvider() {
        client = CrowdClientHolder.getClient();
    }
    
    @Override
    public User loadUser(String username) throws UserNotFoundException {
        User user = null;
        try {
            com.atlassian.crowd.model.user.User crowdUser = client.getUser(username);
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
        // TODO: Auto-generated method
        return null;
    }

    @Override
    public void deleteUser(String username) {
        // TODO: Auto-generated method

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
            usernames = client.searchUserNames(NullRestrictionImpl.INSTANCE, 0, -1);
        } catch (OperationFailedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidAuthenticationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ApplicationPermissionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return usernames;
    }

    @Override
    public Collection<User> getUsers(int startIndex, int numResults) {
        List<User> users = new ArrayList<User>();

        try {
            // TODO: Limit this list to just chat-enabled users
            
            List<com.atlassian.crowd.model.user.User> crowdUsers = client.searchUsers(NullRestrictionImpl.INSTANCE, startIndex, numResults);
            for(com.atlassian.crowd.model.user.User crowdUser : crowdUsers) {
                users.add(new User(crowdUser.getName(), crowdUser.getDisplayName(), crowdUser.getEmailAddress(), NOTIME, NOTIME));
            }
        } catch (OperationFailedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidAuthenticationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ApplicationPermissionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return users;
    }

    @Override
    public void setName(String username, String name) throws UserNotFoundException {
        // TODO: Auto-generated method

    }

    @Override
    public void setEmail(String username, String email) throws UserNotFoundException {
        // TODO: Auto-generated method

    }

    @Override
    public void setCreationDate(String username, Date creationDate) throws UserNotFoundException {
        // TODO: Auto-generated method

    }

    @Override
    public void setModificationDate(String username, Date modificationDate) throws UserNotFoundException {
        // TODO: Auto-generated method

    }

    @Override
    public Set<String> getSearchFields() throws UnsupportedOperationException {
        // TODO: Auto-generated method
        return null;
    }

    @Override
    public Collection<User> findUsers(Set<String> fields, String query) throws UnsupportedOperationException {
        // TODO: Auto-generated method
        return null;
    }

    @Override
    public Collection<User> findUsers(Set<String> fields, String query, int startIndex, int numResults) throws UnsupportedOperationException {
        // TODO: Auto-generated method
        return null;
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
