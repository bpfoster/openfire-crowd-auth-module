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
