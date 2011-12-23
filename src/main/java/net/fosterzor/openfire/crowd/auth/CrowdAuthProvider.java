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

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 5/25/11
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class CrowdAuthProvider implements AuthProvider {
    private CrowdClient client;

    public CrowdAuthProvider() {
        client = CrowdClientHolder.getClient();
    }

    // For unit testing
    protected CrowdAuthProvider(CrowdClient client) {
        this.client = client;
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
