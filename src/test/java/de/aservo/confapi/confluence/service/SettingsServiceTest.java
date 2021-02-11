package de.aservo.confapi.confluence.service;

import com.atlassian.confluence.settings.setup.DefaultTestSettings;
import com.atlassian.confluence.settings.setup.OtherTestSettings;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import de.aservo.confapi.commons.model.SettingsBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SettingsServiceTest {

    @Mock
    private SettingsManager settingsManager;

    private SettingsServiceImpl settingsService;

    @Before
    public void setup() {
        settingsService = new SettingsServiceImpl(settingsManager);
    }

    @Test
    public void testGetSettings() {
        final Settings settings = new DefaultTestSettings();
        String customContactMessage = "Test";
        settings.setCustomContactMessage(customContactMessage);
        doReturn(settings).when(settingsManager).getGlobalSettings();

        final SettingsBean settingsBean = settingsService.getSettings();

        final SettingsBean settingsBeanRef = new SettingsBean();
        settingsBeanRef.setBaseUrl(URI.create(settings.getBaseUrl()));
        settingsBeanRef.setTitle(settings.getSiteTitle());
        settingsBeanRef.setContactMessage(customContactMessage);

        assertEquals(settingsBeanRef, settingsBean);
    }

    @Test
    public void testPutSettings() {
        final Settings defaultSettings = new DefaultTestSettings();
        doReturn(defaultSettings).when(settingsManager).getGlobalSettings();

        final Settings updateSettings = new OtherTestSettings();
        String customContactMessage = "Test";
        updateSettings.setCustomContactMessage(customContactMessage);

        final SettingsBean requestBean = new SettingsBean();
        requestBean.setBaseUrl(URI.create(updateSettings.getBaseUrl()));
        requestBean.setTitle(updateSettings.getSiteTitle());
        requestBean.setContactMessage(updateSettings.getCustomContactMessage());

        final SettingsBean responseBean = settingsService.setSettings(requestBean);

        final ArgumentCaptor<Settings> settingsCaptor = ArgumentCaptor.forClass(Settings.class);
        verify(settingsManager).updateGlobalSettings(settingsCaptor.capture());
        final Settings settings = settingsCaptor.getValue();

        final SettingsBean settingsBean = new SettingsBean();
        settingsBean.setBaseUrl(URI.create(settings.getBaseUrl()));
        settingsBean.setTitle(settings.getSiteTitle());
        settingsBean.setContactMessage(settings.getCustomContactMessage());

        assertEquals(requestBean, settingsBean);
        assertEquals(requestBean, responseBean);
    }

}
