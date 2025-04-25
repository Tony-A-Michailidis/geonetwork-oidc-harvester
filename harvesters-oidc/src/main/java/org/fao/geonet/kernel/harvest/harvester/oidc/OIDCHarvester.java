package org.fao.geonet.kernel.harvest.harvester.oidc;

import org.fao.geonet.kernel.harvest.harvester.AbstractHarvester;
import org.jdom.Element;
import java.util.logging.Logger;

public class OIDCHarvester extends AbstractHarvester {
    private static final Logger log = Logger.getLogger(OIDCHarvester.class.getName());
    private OIDCClient oidcClient;
    private String metadataEndpoint;

    public OIDCHarvester() {
        super();
        log.info("OIDC Harvester initialized");
    }

    @Override
    protected void doInit(Element node) throws Exception {
        log.info("Initializing OIDC Harvester config");

        String clientId = node.getChildText("clientId");
        String clientSecret = node.getChildText("clientSecret");
        String tokenEndpoint = node.getChildText("tokenEndpoint");
        String user = node.getChildText("username");
        String password = node.getChildText("password");
        this.metadataEndpoint = node.getChildText("metadataEndpoint");

        this.oidcClient = new OIDCClient(tokenEndpoint, clientId, clientSecret, user, password);
    }

    @Override
    protected void doHarvest(String resourceId, Element configNode) throws Exception {
        try {
            String accessToken = oidcClient.getAccessToken();
            log.info("Access token acquired, harvesting metadata...");
            MetadataFetcher.fetchAndIngest(metadataEndpoint, accessToken, context);
        } catch (Exception e) {
            log.severe("Harvesting failed: " + e.getMessage());
            throw e;
        }
    }
}
