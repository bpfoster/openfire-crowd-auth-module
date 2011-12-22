package net.fosterzor.openfire.crowd.auth;

import com.atlassian.crowd.exception.ApplicationPermissionException;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.service.client.CrowdClient;
import net.fosterzor.openfire.crowd.CrowdClientHolder;
import org.jivesoftware.openfire.auth.AuthProvider;
import org.jivesoftware.openfire.auth.ConnectionException;
import org.jivesoftware.openfire.auth.InternalUnauthenticatedException;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 5/25/11
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class CrowdAuthProvider implements AuthProvider {
    private static final Logger logger = LoggerFactory.getLogger(CrowdAuthProvider.class);
    private CrowdClient client;

    public CrowdAuthProvider() {
        client = CrowdClientHolder.getClient();
    }
    
    @Override
    public boolean isPlainSupported() {
        return true;
    }

    @Override
    public boolean isDigestSupported() {
        return false;
    }

    @Override
    public void authenticate(String username, String password) throws UnauthorizedException, ConnectionException, InternalUnauthenticatedException {
        try {
            User user = client.authenticateUser(username, password);
            if (user == null) {
                throw new UnauthorizedException();
            }
        } catch (com.atlassian.crowd.exception.UserNotFoundException e) {
            throw new UnauthorizedException("User not found", e);
        } catch (com.atlassian.crowd.exception.InactiveAccountException e) {
            throw new UnauthorizedException("User inactive", e);
        } catch (ExpiredCredentialException e) {
            throw new UnauthorizedException("User credentials expired", e);
        } catch (ApplicationPermissionException e) {
            throw new InternalUnauthenticatedException("Application permission", e);
        } catch (com.atlassian.crowd.exception.InvalidAuthenticationException e) {
            throw new InternalUnauthenticatedException("Invalid authentication", e);
        } catch (OperationFailedException e) {
            throw new InternalUnauthenticatedException("Operation failed", e);
        }
    }

    @Override
    public void authenticate(String username, String token, String digest) throws UnauthorizedException, ConnectionException, InternalUnauthenticatedException {
        throw new InternalUnauthenticatedException("Crowd does not support digest passwords");
    }

    @Override
    public String getPassword(String s) throws UserNotFoundException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Unable to retrieve Crowd passwords from external apps");
    }

    @Override
    public void setPassword(String s, String s1) throws UserNotFoundException, UnsupportedOperationException {
        throw new UnsupportedOperationException("Unable to set Crowd passwords from external apps");
    }

    @Override
    public boolean supportsPasswordRetrieval() {
        return false;
    }
}
