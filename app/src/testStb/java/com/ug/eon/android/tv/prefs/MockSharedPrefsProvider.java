package com.ug.eon.android.tv.prefs;

/**
 * Created by nemanja.todoric on 3/27/2018.
 */

public class MockSharedPrefsProvider implements SharedPrefsProvider {

    public static final String MockAccessToken = "access_abcdefgh";
    public static final String MockRefreshToken = "refresh_abcdefgh";
    public static final String MockDeviceNumber = "device_abcdefgh";

    public static final String MockServerEnv = "prod";
    public static final String MockInfoServerBaseUrl = "https://api.ug.cdn.united.cloud";
    public static final String MockApiVersion = "v1";
    public static final String MockImageServerUrl = "https://images.ug.cdn.united.cloud";
    public static final String MockStaticServer = "https://static.ug.cdn.united.cloud";

    public static final String MockWidevineLicenceServer = "https://pfsense-conax.united.cloud:28094/license";

    private static final String MOCK_SERVER_PREFS = "{" +
            "\"name\":" + "\"" + MockServerEnv + "\"" + "," +
            "\"infoServerBaseUrl\":" + "\"" + MockInfoServerBaseUrl + "\"" + "," +
            "\"apiVersion\":" + "\"" + MockApiVersion + "\"" + "," +
            "\"imageServerUrl\": " + "\"" + MockImageServerUrl + "\"" + "," +
            "\"staticServer\":" + "\"" + MockStaticServer + "\"" +
            "}";

    private final String MOCK_AUTH_PREFS = "{" +
            "\"access_token\" :" + "\"" + MockAccessToken + "\"" + ", " +
            "\"refresh_token\" :" + "\"" + MockRefreshToken + "\"" + ", " +
            "\"device_number\" : " + "\"" + MockDeviceNumber + "\"" +
            "} ";

    private final String MOCK_SERVICE_PROVIDER_PREFS = "{" +
            "\"certificateUrlFairplay\": \"string\", " +
            "\"country\": \"string\", " +
            "\"dvbInfo\": { " +
            "\"dvbFallbackLimit\": 0, " +
            "\"frequencies\": [ " +
            "0" +
            "]" +
            "}," +
            "\"id\": 0, " +
            "\"identifier\": \"string\", " +
            "\"licenseServerUrlFairplay\": \"string\", " +
            "\"licenseServerUrlPlayready\": \"string\", " +
            "\"licenseServerUrlWidewine\": \"" + MockWidevineLicenceServer + "\" ," +
            "\"name\": \"string\", " +
            "\"supportPhoneNumber\": \"string\", " +
            "\"supportWebAddress\": \"string\" " +
            "}";

    @Override
    public String getString(String key, String defaultValue) {
        switch (key) {
            case "accessObj":
                return MOCK_AUTH_PREFS;
            case "servers":
                return MOCK_SERVER_PREFS;
            case "serviceProviders":
                return MOCK_SERVICE_PROVIDER_PREFS;
            default:
                break;
        }

        return defaultValue;
    }

    @Override
    public void setString(String key, String value) {}

    @Override
    public void remove(String key) {
        
    }
}
