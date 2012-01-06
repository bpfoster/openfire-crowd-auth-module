package net.fosterzor.openfire.crowd.auth;

import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.service.client.CrowdClient;
import org.jivesoftware.openfire.auth.InternalUnauthenticatedException;
import org.jivesoftware.openfire.auth.UnauthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 1/6/12
 * Time: 11:55 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(PowerMockRunner.class)
public class CrowdAuthProviderTest {
    private CrowdAuthProvider authProvider;
    private CrowdClient crowdClient;

    @Before
    public void setUp() throws Exception {
        crowdClient = mock(CrowdClient.class);
        authProvider = new CrowdAuthProvider(crowdClient);
    }

    @Test
    public void testProperties() {
        assertTrue(authProvider.isPlainSupported());
        assertFalse(authProvider.isDigestSupported());
        assertFalse(authProvider.supportsPasswordRetrieval());
    }

    @Test(expected = InternalUnauthenticatedException.class)
    public void testDigestAuthenticate() throws Exception {
        authProvider.authenticate("abc", "def", "ghi");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetPassword() throws Exception {
        authProvider.getPassword("anything");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetPassword() throws Exception {
        authProvider.setPassword("anything", "anythingElse");
    }

    @Test
    public void testPlaintextAuthentication() throws Exception {
        String password = "password";
        String username = "username";
        User user = mock(User.class);

        when(crowdClient.authenticateUser(username, password)).thenReturn(user);
        authProvider.authenticate(username, password);
    }

    @Test(expected = UnauthorizedException.class)
    public void testFailedAuthenticationWithNull() throws Exception {
        String password = "password";
        String username = "username";

        when(crowdClient.authenticateUser(username, password)).thenReturn(null);
        authProvider.authenticate(username, password);
    }

    @Test(expected = UnauthorizedException.class)
    public void testFailedAuthenticationWithException() throws Exception {
        String password = "password";
        String username = "username";

        when(crowdClient.authenticateUser(username, password)).thenThrow(new com.atlassian.crowd.exception.UserNotFoundException(username));
        authProvider.authenticate(username, password);
    }
}
