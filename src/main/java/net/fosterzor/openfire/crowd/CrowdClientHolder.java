package net.fosterzor.openfire.crowd;

import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.service.client.CrowdClient;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 5/25/11
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class CrowdClientHolder {
    private static CrowdClient client;

    public static CrowdClient getClient() {
        if (client == null) {
            synchronized (CrowdClientHolder.class) {
                if (client == null) {
                    client = new RestCrowdClientFactory().newInstance("http://localhost:8095/crowd", "openfire", "password");
                }
            }
        }
        return client;
    }
}
