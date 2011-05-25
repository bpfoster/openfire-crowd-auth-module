package net.fosterzor.openfire.crowd;

import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.InvalidAuthenticationException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.service.client.CrowdClient;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.openfire.user.UserProvider;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 5/25/11
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class CrowdUserProvider implements UserProvider {

    private CrowdClient client;

    public CrowdUserProvider() {
        client = CrowdClientHolder.getClient();
    }
    
    @Override
    public User loadUser(String username) throws UserNotFoundException {
        User user = null;
        try {
            com.atlassian.crowd.model.user.User crowdUser = client.getUser(username);
            user = new User(crowdUser.getName(), crowdUser.getDisplayName(), crowdUser.getEmailAddress(), null, null);
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
        // TODO: Auto-generated method
        return 0;
    }

    @Override
    public Collection<User> getUsers() {
        // TODO: Auto-generated method
        return null;
    }

    @Override
    public Collection<String> getUsernames() {
        // TODO: Auto-generated method
        return null;
    }

    @Override
    public Collection<User> getUsers(int startIndex, int numResults) {
        // TODO: Auto-generated method
        return null;
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
        // TODO: Auto-generated method
        return false;
    }

    @Override
    public boolean isEmailRequired() {
        // TODO: Auto-generated method
        return false;
    }
}
