package net.fosterzor.openfire.crowd.auth;

import com.atlassian.crowd.integration.exception.ApplicationAccessDeniedException;
import com.atlassian.crowd.integration.exception.InactiveAccountException;
import com.atlassian.crowd.integration.exception.InvalidAuthenticationException;
import com.atlassian.crowd.integration.exception.InvalidAuthorizationTokenException;
import net.fosterzor.openfire.crowd.CrowdClientHolder;
import org.jivesoftware.openfire.auth.AuthProvider;
import org.jivesoftware.openfire.auth.ConnectionException;
import org.jivesoftware.openfire.auth.InternalUnauthenticatedException;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.jivesoftware.openfire.user.UserNotFoundException;

import java.rmi.RemoteException;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 5/25/11
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class CrowdAuthProvider implements AuthProvider {
    private CrowdClientHolder crowdClientHolder;

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
            crowdClientHolder.getAuthenticationManager().authenticate(username, password);
        } catch (RemoteException e) {
            throw new ConnectionException("Remote exception", e);
        } catch (InvalidAuthorizationTokenException e) {
            throw new UnauthorizedException("Invalid authorization token", e);
        } catch (InvalidAuthenticationException e) {
            throw new UnauthorizedException("Invalid authentication", e);
        } catch (InactiveAccountException e) {
            throw new UnauthorizedException("Inactive account", e);
        } catch (ApplicationAccessDeniedException e) {
            throw new InternalUnauthenticatedException("Application access denied", e);
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
