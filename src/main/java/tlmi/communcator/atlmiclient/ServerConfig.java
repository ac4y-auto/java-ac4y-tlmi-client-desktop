package tlmi.communcator.atlmiclient;

/**
 * Server URL configuration.
 *
 * Provides two profiles:
 * - PRODUCTION: original ac4y cloud servers
 * - LOCAL: localhost development (ac4y-gate on port 3000 + WebSocket on 2222)
 */
public class ServerConfig {

    private final String gateServiceUrl;
    private final String userServiceUrl;
    private final String translationServiceUrl;
    private final String websocketUrl;

    private ServerConfig(String gateServiceUrl, String userServiceUrl,
                         String translationServiceUrl, String websocketUrl) {
        this.gateServiceUrl = gateServiceUrl;
        this.userServiceUrl = userServiceUrl;
        this.translationServiceUrl = translationServiceUrl;
        this.websocketUrl = websocketUrl;
    }

    /** Production: ac4y cloud servers */
    public static ServerConfig production() {
        return new ServerConfig(
                "https://gate.ac4y.com",
                "https://client.ac4y.com",
                "https://api.ac4y.com",
                "wss://www.ac4y.com:2222"
        );
    }

    /** Local development: ac4y-gate on localhost */
    public static ServerConfig local() {
        return new ServerConfig(
                "http://localhost:3000",
                "http://localhost:3002",
                "http://localhost:3000",
                "ws://localhost:2222"
        );
    }

    public String getGateServiceUrl() { return gateServiceUrl; }
    public String getUserServiceUrl() { return userServiceUrl; }
    public String getTranslationServiceUrl() { return translationServiceUrl; }
    public String getWebsocketUrl() { return websocketUrl; }

    /** WebSocket URI for a specific user */
    public String getWebsocketUri(String userId) {
        return websocketUrl + "/" + userId;
    }

    /** Internet check URL (gate REST endpoint) */
    public String getHealthCheckUrl() {
        return gateServiceUrl + "/gate/user";
    }

    @Override
    public String toString() {
        return "ServerConfig{gate=" + gateServiceUrl
                + ", user=" + userServiceUrl
                + ", translation=" + translationServiceUrl
                + ", ws=" + websocketUrl + "}";
    }

}
