package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.confluence.settings.setup.DefaultTestSettings;
import com.atlassian.confluence.settings.setup.OtherTestSettings;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import de.aservo.atlassian.confluence.confapi.model.SettingsBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SettingsResourceTest {


    public static final String OTHER_BASEURL = "http://localhost:1990/confluence";
    public static final String OTHER_TITLE = "Other title";

    @Mock
    private SettingsManager settingsManager;

    @Test
    public void testGetSettings() {
        final Settings settings = new DefaultTestSettings();
        doReturn(settings).when(settingsManager).getGlobalSettings();

        final SettingsResource resource = new SettingsResource(settingsManager);
        final Response response = resource.getSettings();
        final SettingsBean bean = (SettingsBean) response.getEntity();

        assertEquals(SettingsBean.from(settings), bean);
    }

    @Test
    public void testPutSettings() {
        final Settings defaultSettings = new DefaultTestSettings();
        doReturn(defaultSettings).when(settingsManager).getGlobalSettings();

        final Settings updateSettings = new OtherTestSettings();
        final SettingsResource resource = new SettingsResource(settingsManager);
        final SettingsBean requestBean = SettingsBean.from(updateSettings);
        final Response response = resource.putSettings(requestBean);
        final SettingsBean responseBean = (SettingsBean) response.getEntity();

        final ArgumentCaptor<Settings> settingsCaptor = ArgumentCaptor.forClass(Settings.class);
        verify(settingsManager).updateGlobalSettings(settingsCaptor.capture());
        final Settings settings = settingsCaptor.getValue();

        assertEquals(SettingsBean.from(updateSettings), SettingsBean.from(settings));
        assertEquals(requestBean, responseBean);
    }

}
