package de.aservo.atlassian.confluence.confapi.service;

import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.atlassian.confapi.model.SettingsBean;
import de.aservo.atlassian.confapi.service.api.SettingsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

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
        settingsBean.setBaseUrl(settings.getBaseUrl());
        settingsBean.setTitle(settings.getSiteTitle());

        return settingsBean;
    }

    @Override
    public SettingsBean setSettings(SettingsBean settingsBean) {
        final Settings settings = settingsManager.getGlobalSettings();

        if (StringUtils.isNotBlank(settingsBean.getBaseUrl())) {
            settings.setBaseUrl(settingsBean.getBaseUrl());
        }

        if (StringUtils.isNotBlank(settingsBean.getTitle())) {
            settings.setSiteTitle(settingsBean.getTitle());
        }

        settingsManager.updateGlobalSettings(settings);

        return getSettings();
    }
}
