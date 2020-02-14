package de.aservo.atlassian.confluence.confapi.model;

import com.atlassian.confluence.settings.setup.DefaultTestSettings;
import com.atlassian.confluence.setup.settings.Settings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SettingsBeanTest {

    @Test
    public void testDefaultConstructor() {
        final SettingsBean bean = new SettingsBean();

        assertNull(bean.getBaseurl());
        assertNull(bean.getTitle());
    }

    @Test
    public void testFromConstructor() throws Exception {
        final Settings settings = new DefaultTestSettings();
        final SettingsBean bean = SettingsBean.from(settings);

        assertEquals(bean.getBaseurl(), settings.getBaseUrl());
        assertEquals(bean.getTitle(), settings.getSiteTitle());
    }

    @Test
    public void testEquals() throws Exception {
        final Settings settings = new DefaultTestSettings();
        final SettingsBean bean1 = SettingsBean.from(settings);
        final SettingsBean bean2 = SettingsBean.from(settings);
        assertEquals(bean1, bean2);
    }

    @Test
    public void testEqualsNull() {
        final SettingsBean bean = new SettingsBean();
        assertFalse(bean.equals(null));
    }

    @Test
    public void testEqualsSameInstance() {
        final SettingsBean bean = new SettingsBean();
        assertEquals(bean, bean);
    }

    @Test
    public void testEqualsOtherType() {
        final SettingsBean bean = new SettingsBean();
        assertFalse(bean.equals(new Object()));
    }

}
