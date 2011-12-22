package net.fosterzor.openfire.crowd;

import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.service.client.CrowdClient;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 5/25/11
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class CrowdClientHolder {
    private static final Logger logger = LoggerFactory.getLogger(CrowdClientHolder.class);
    private static CrowdClient client = initClient();
    private static final String CROWD_PROPERTIES = "crowd.properties";

    private CrowdClientHolder() {
    }

    private static CrowdClient initClient() {
        CrowdClient result = null;
        Properties crowdProperties = new Properties();
        try {
            File file = new File(JiveGlobals.getHomeDirectory() + File.separator + "conf" + File.separator + CROWD_PROPERTIES);
            if (!file.exists() || !file.canRead()) {
                throw new FileNotFoundException("Unable to open crowd.properties");
            }
            
            crowdProperties.load(new FileInputStream(file));

            String appName = crowdProperties.getProperty("application.name");
            String appPass = crowdProperties.getProperty("application.password");
            String url = crowdProperties.getProperty("crowd.server.url");

            logger.info("Creating client to connect to:" + url);

            result = new RestCrowdClientFactory().newInstance(url, appName, appPass);
        } catch (IOException e) {
            logger.error("ERROR creating Crowd client!!!!", e);
        }

        return result;
    }

    public static CrowdClient getClient() {
        if (client == null) {
            throw new IllegalStateException("Crowd client was unable to be instantiated!  Check earlier logs for errors.");
        }
        return client;
    }
}
