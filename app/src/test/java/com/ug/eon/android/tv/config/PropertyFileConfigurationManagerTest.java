package com.ug.eon.android.tv.config;

import android.content.Context;

import com.ug.eon.android.tv.util.SystemInfo;

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PropertyFileConfigurationManagerTest {

    @Test
    public void testWatchNextEnabled() {
        Context context = mock(Context.class);
        MockPropertyFileProvider propertyFileProvider = new MockPropertyFileProvider();
        propertyFileProvider.setWatchNextEnabled(true);
        propertyFileProvider.setWatchNextMinApi(26);
        PropertyFileConfigurationManager propertyFileConfigurationManager = new PropertyFileConfigurationManager(context, propertyFileProvider);

        assertTrue(propertyFileConfigurationManager.isFeatureEnabled(EonFeature.WatchNext));
    }

    @Test
    public void testWatchNextDisabled() {
        Context context = mock(Context.class);
        MockPropertyFileProvider propertyFileProvider = new MockPropertyFileProvider();
        propertyFileProvider.setWatchNextEnabled(false);
        PropertyFileConfigurationManager propertyFileConfigurationManager = new PropertyFileConfigurationManager(context, propertyFileProvider);

        assertFalse(propertyFileConfigurationManager.isFeatureEnabled(EonFeature.WatchNext));
    }

    @Test
    public void testWatchNextMinApiLevelLower() {
        Context context = mock(Context.class);
        MockPropertyFileProvider propertyFileProvider = new MockPropertyFileProvider();
        propertyFileProvider.setWatchNextMinApi(SystemInfo.getWatchNextMinSupportedApiLevel() - 1);
        PropertyFileConfigurationManager propertyFileConfigurationManager = new PropertyFileConfigurationManager(context, propertyFileProvider);

        assertTrue(propertyFileConfigurationManager.isFeatureEnabled(EonFeature.WatchNext));
    }

    @Test
    public void testWatchNextMinApiLevelHigher() {
        Context context = mock(Context.class);
        MockPropertyFileProvider propertyFileProvider = new MockPropertyFileProvider();
        propertyFileProvider.setWatchNextMinApi(SystemInfo.getWatchNextMinSupportedApiLevel() + 1);
        PropertyFileConfigurationManager propertyFileConfigurationManager = new PropertyFileConfigurationManager(context, propertyFileProvider);

        assertFalse(propertyFileConfigurationManager.isFeatureEnabled(EonFeature.WatchNext));
    }

    @Test
    public void testCustomPropertyValues1() {
        Context context = mock(Context.class);
        MockPropertyFileProvider propertyFileProvider = new MockPropertyFileProvider();
        propertyFileProvider.setCustomVal(PropertyFileConfigurationManager.WATCH_NEXT_PROPERTY, "fireball"); // "fireball" will be treated as false
        propertyFileProvider.setCustomVal(PropertyFileConfigurationManager.WATCH_NEXT_MIN_API, "-345345345345345345345345");
        PropertyFileConfigurationManager propertyFileConfigurationManager = new PropertyFileConfigurationManager(context, propertyFileProvider);

        assertFalse(propertyFileConfigurationManager.isFeatureEnabled(EonFeature.WatchNext));
    }

    @Test
    public void testCustomPropertyValues2() {
        Context context = mock(Context.class);
        MockPropertyFileProvider propertyFileProvider = new MockPropertyFileProvider();
        propertyFileProvider.setCustomVal(PropertyFileConfigurationManager.WATCH_NEXT_PROPERTY, ""); // "" will be treated as false
        propertyFileProvider.setCustomVal(PropertyFileConfigurationManager.WATCH_NEXT_MIN_API, "");
        PropertyFileConfigurationManager propertyFileConfigurationManager = new PropertyFileConfigurationManager(context, propertyFileProvider);

        assertFalse(propertyFileConfigurationManager.isFeatureEnabled(EonFeature.WatchNext));
    }

    @Test
    public void testCustomPropertyValues3() {
        Context context = mock(Context.class);
        MockPropertyFileProvider propertyFileProvider = new MockPropertyFileProvider();
        propertyFileProvider.setCustomVal(PropertyFileConfigurationManager.WATCH_NEXT_PROPERTY, ""); // "" will be treated as false
        propertyFileProvider.setCustomVal(PropertyFileConfigurationManager.WATCH_NEXT_MIN_API, "operable");
        PropertyFileConfigurationManager propertyFileConfigurationManager = new PropertyFileConfigurationManager(context, propertyFileProvider);

        assertFalse(propertyFileConfigurationManager.isFeatureEnabled(EonFeature.WatchNext));
    }

    @Test
    public void testPropertiesMissing() {
        Context context = mock(Context.class);
        MockPropertyFileProvider propertyFileProvider = new MockPropertyFileProvider();
        PropertyFileConfigurationManager propertyFileConfigurationManager = new PropertyFileConfigurationManager(context, propertyFileProvider);

        assertTrue(propertyFileConfigurationManager.isFeatureEnabled(EonFeature.WatchNext));
    }

    public class MockPropertyFileProvider implements PropertyFileProvider {

        private Properties properties;

        public MockPropertyFileProvider() {
            properties = new Properties();
        }

        @Override
        public Properties getProperties(Context context, String filePath) {
            return properties;
        }

        void setWatchNextEnabled(boolean enabled) {
            String val = enabled ? "true" : "false";
            properties.setProperty(PropertyFileConfigurationManager.WATCH_NEXT_PROPERTY, val);
        }

        void setWatchNextMinApi(int minApi) {
            properties.setProperty(PropertyFileConfigurationManager.WATCH_NEXT_MIN_API, Integer.toString(minApi));
        }

        void setCustomVal(String key, String val) {
            properties.setProperty(key, val);
        }
    }
}
