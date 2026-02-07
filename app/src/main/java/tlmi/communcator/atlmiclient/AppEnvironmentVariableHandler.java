package tlmi.communcator.atlmiclient;

import android.content.Context;

import ac4y.environment.Ac4yEnvironmentVariableHandler;
import ac4y.environment.Ac4ySharedPreferencesProperty;

public class AppEnvironmentVariableHandler extends Ac4yEnvironmentVariableHandler {

    public AppEnvironmentVariableHandler(Context context){
        super(context);
        createInstallationId();
        createUserId();
        createPartnerId();
        createTextToSpeechDisabler();
        createUserAvatar();
        createPartnerAvatar();
        createUserCountry();
        createUserLanguage();
        createPartnerCountry();
        createPartnerLanguage();
    }


    private static String INSTALLATION_ID_PROPERTY = "installationId";

    public Ac4ySharedPreferencesProperty getInstallationId() {
        return installationId;
    }

    public void setInstallationId(Ac4ySharedPreferencesProperty installationId) {
        this.installationId = installationId;
    }

    public void createInstallationId() {
        setInstallationId(new Ac4ySharedPreferencesProperty(getStore(), INSTALLATION_ID_PROPERTY));
    }

    private Ac4ySharedPreferencesProperty installationId;


    private static String USER_ID_PROPERTY = "userId";

    public Ac4ySharedPreferencesProperty getUserId() {
        return userId;
    }

    public void setUserId(Ac4ySharedPreferencesProperty userId) {
        this.userId = userId;
    }

    public void createUserId() {
        setUserId(new Ac4ySharedPreferencesProperty(getStore(), USER_ID_PROPERTY));
    }

    private Ac4ySharedPreferencesProperty userId;




    private static String USER_AVATAR_PROPERTY = "userAvatar";

    public Ac4ySharedPreferencesProperty getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(Ac4ySharedPreferencesProperty userAvatar) {
        this.userAvatar = userAvatar;
    }

    private Ac4ySharedPreferencesProperty userAvatar;


    public void createUserAvatar() {
        setUserAvatar(new Ac4ySharedPreferencesProperty(getStore(), USER_AVATAR_PROPERTY));
    }




    private static String PARTNER_ID_PROPERTY = "partnerId";

    private Ac4ySharedPreferencesProperty partnerId;

    public Ac4ySharedPreferencesProperty getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Ac4ySharedPreferencesProperty partnerId) {
        this.partnerId = partnerId;
    }

    public void createPartnerId() {
        setPartnerId(new Ac4ySharedPreferencesProperty(getStore(), PARTNER_ID_PROPERTY));
    }



    private static String PARTNER_AVATAR_PROPERTY = "partnerAvatar";

    public Ac4ySharedPreferencesProperty getPartnerAvatar() {
        return partnerAvatar;
    }

    public void setPartnerAvatar(Ac4ySharedPreferencesProperty partnerAvatar) {
        this.partnerAvatar = partnerAvatar;
    }

    private Ac4ySharedPreferencesProperty partnerAvatar;


    public void createPartnerAvatar() {
        setPartnerAvatar(new Ac4ySharedPreferencesProperty(getStore(), PARTNER_AVATAR_PROPERTY));
    }





    private static String TEXT_TO_SPEECH_DISABLER_PROPERTY = "textToSpeechDisabler";

    public Ac4ySharedPreferencesProperty getTextToSpeechDisabler() {
        return textToSpeechDisabler;
    }

    public boolean isTextToSpeechDisabler() {
        return getTextToSpeechDisabler().get().equals("1");
    }

    public void enableTextToSpeechDisabler() {
        getTextToSpeechDisabler().set("1");
    }

    public void disableTextToSpeechDisabler() {
        getTextToSpeechDisabler().set("0");
    }

    public void setTextToSpeechDisabler(Ac4ySharedPreferencesProperty textToSpeechDisabler) {
        this.textToSpeechDisabler = textToSpeechDisabler;
    }

    private Ac4ySharedPreferencesProperty textToSpeechDisabler;

    public void createTextToSpeechDisabler() {
        setTextToSpeechDisabler(new Ac4ySharedPreferencesProperty(getStore(), TEXT_TO_SPEECH_DISABLER_PROPERTY));
    }




    private static String USER_LANGUAGE_PROPERTY = "userLanguage";

    public void createUserLanguage() {
        setUserLanguage(new Ac4ySharedPreferencesProperty(getStore(), USER_LANGUAGE_PROPERTY));
    }

    public Ac4ySharedPreferencesProperty getUserLanguage() {
        return userLanguage;
    }

    public void setUserLanguage(Ac4ySharedPreferencesProperty userLanguage) {
        this.userLanguage = userLanguage;
    }

    private Ac4ySharedPreferencesProperty userLanguage;



    private static String USER_COUNTRY_PROPERTY = "userCountry";

    public void createUserCountry() {
        setUserCountry(new Ac4ySharedPreferencesProperty(getStore(), USER_COUNTRY_PROPERTY));
    }

    public Ac4ySharedPreferencesProperty getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(Ac4ySharedPreferencesProperty userCountry) {
        this.userCountry = userCountry;
    }

    private Ac4ySharedPreferencesProperty userCountry;




    private static String PARTNER_LANGUAGE_PROPERTY = "partnerLanguage";

    public void createPartnerLanguage() {
        setPartnerLanguage(new Ac4ySharedPreferencesProperty(getStore(), PARTNER_LANGUAGE_PROPERTY));
    }

    public Ac4ySharedPreferencesProperty getPartnerLanguage() {
        return partnerLanguage;
    }

    public void setPartnerLanguage(Ac4ySharedPreferencesProperty partnerLanguage) {
        this.partnerLanguage = partnerLanguage;
    }

    private Ac4ySharedPreferencesProperty partnerLanguage;



    private static String PARTNER_COUNTRY_PROPERTY = "partnerCountry";

    public void createPartnerCountry() {
        setPartnerCountry(new Ac4ySharedPreferencesProperty(getStore(), PARTNER_COUNTRY_PROPERTY));
    }

    public Ac4ySharedPreferencesProperty getPartnerCountry() {
        return partnerCountry;
    }

    public void setPartnerCountry(Ac4ySharedPreferencesProperty partnerCountry) {
        this.partnerCountry = partnerCountry;
    }

    private Ac4ySharedPreferencesProperty partnerCountry;



}