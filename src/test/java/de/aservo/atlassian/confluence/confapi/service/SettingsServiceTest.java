package de.aservo.atlassian.confluence.confapi.service;

import com.atlassian.confluence.settings.setup.DefaultTestSettings;
import com.atlassian.confluence.settings.setup.OtherTestSettings;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import de.aservo.atlassian.confapi.model.SettingsBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
        doReturn(settings).when(settingsManager).getGlobalSettings();

        final SettingsBean settingsBean = settingsService.getSettings();

        final SettingsBean settingsBeanRef = new SettingsBean();
        settingsBeanRef.setBaseUrl(settings.getBaseUrl());
        settingsBeanRef.setTitle(settings.getSiteTitle());

        assertEquals(settingsBeanRef, settingsBean);
    }

    @Test
    public void testPutSettings() {
        final Settings defaultSettings = new DefaultTestSettings();
        doReturn(defaultSettings).when(settingsManager).getGlobalSettings();

        final Settings updateSettings = new OtherTestSettings();

        final SettingsBean requestBean = new SettingsBean();
        requestBean.setBaseUrl(updateSettings.getBaseUrl());
        requestBean.setTitle(updateSettings.getSiteTitle());

        final SettingsBean responseBean = settingsService.setSettings(requestBean);

        final ArgumentCaptor<Settings> settingsCaptor = ArgumentCaptor.forClass(Settings.class);
        verify(settingsManager).updateGlobalSettings(settingsCaptor.capture());
        final Settings settings = settingsCaptor.getValue();

        final SettingsBean settingsBean = new SettingsBean();
        settingsBean.setBaseUrl(settings.getBaseUrl());
        settingsBean.setTitle(settings.getSiteTitle());

        assertEquals(requestBean, settingsBean);
        assertEquals(requestBean, responseBean);
    }
}
