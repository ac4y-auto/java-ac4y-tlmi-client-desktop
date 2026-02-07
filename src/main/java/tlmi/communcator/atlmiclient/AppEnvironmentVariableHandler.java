package tlmi.communcator.atlmiclient;

import java.util.HashMap;
import java.util.Map;

public class AppEnvironmentVariableHandler {

    private Map<String, String> store = new HashMap<>();

    public AppEnvironmentVariableHandler() {
        // Initialize default values
        store.put("textToSpeechDisabler", "0");
    }

    public Map<String, String> getStore() {
        return store;
    }

    // --- Simple Property inner class ---

    public static class Property {
        private final Map<String, String> store;
        private final String key;

        public Property(Map<String, String> store, String key) {
            this.store = store;
            this.key = key;
        }

        public String get() {
            String val = store.get(key);
            return val != null ? val : "";
        }

        public void set(String value) {
            store.put(key, value);
        }

        public String getSet(String defaultValue) {
            String val = store.get(key);
            if (val == null || val.isEmpty()) {
                store.put(key, defaultValue);
                return defaultValue;
            }
            return val;
        }
    }

    // --- Properties ---

    private Property installationId = new Property(store, "installationId");
    private Property userId = new Property(store, "userId");
    private Property userAvatar = new Property(store, "userAvatar");
    private Property partnerId = new Property(store, "partnerId");
    private Property partnerAvatar = new Property(store, "partnerAvatar");
    private Property textToSpeechDisabler = new Property(store, "textToSpeechDisabler");
    private Property userLanguage = new Property(store, "userLanguage");
    private Property userCountry = new Property(store, "userCountry");
    private Property partnerLanguage = new Property(store, "partnerLanguage");
    private Property partnerCountry = new Property(store, "partnerCountry");

    public Property getInstallationId() { return installationId; }
    public Property getUserId() { return userId; }
    public Property getUserAvatar() { return userAvatar; }
    public Property getPartnerId() { return partnerId; }
    public Property getPartnerAvatar() { return partnerAvatar; }
    public Property getTextToSpeechDisabler() { return textToSpeechDisabler; }
    public Property getUserLanguage() { return userLanguage; }
    public Property getUserCountry() { return userCountry; }
    public Property getPartnerLanguage() { return partnerLanguage; }
    public Property getPartnerCountry() { return partnerCountry; }

    public boolean isTextToSpeechDisabler() {
        return getTextToSpeechDisabler().get().equals("1");
    }

    public void enableTextToSpeechDisabler() {
        getTextToSpeechDisabler().set("1");
    }

    public void disableTextToSpeechDisabler() {
        getTextToSpeechDisabler().set("0");
    }

}
