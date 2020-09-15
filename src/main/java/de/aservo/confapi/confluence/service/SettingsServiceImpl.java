package de.aservo.confapi.confluence.service;

import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.confapi.commons.model.SettingsBean;
import de.aservo.confapi.commons.service.api.SettingsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.URI;

@Component
@ExportAsService(SettingsService.class)
public class SettingsServiceImpl implements SettingsService {

    private final SettingsManager settingsManager;

    @Inject
    public SettingsServiceImpl(@ComponentImport final SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public SettingsBean getSettings() {
        final Settings settings = settingsManager.getGlobalSettings();

        final SettingsBean settingsBean = new SettingsBean();
        settingsBean.setBaseUrl(URI.create(settings.getBaseUrl()));
        settingsBean.setTitle(settings.getSiteTitle());

        return settingsBean;
    }

    @Override
    public SettingsBean setSettings(SettingsBean settingsBean) {
        final Settings settings = settingsManager.getGlobalSettings();

        if (settingsBean.getBaseUrl() != null) {
            settings.setBaseUrl(settingsBean.getBaseUrl().toString());
        }

        if (StringUtils.isNotBlank(settingsBean.getTitle())) {
            settings.setSiteTitle(settingsBean.getTitle());
        }

        settingsManager.updateGlobalSettings(settings);

        return getSettings();
    }
}
