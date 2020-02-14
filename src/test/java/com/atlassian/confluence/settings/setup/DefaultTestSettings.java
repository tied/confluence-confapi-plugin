package com.atlassian.confluence.settings.setup;

import com.atlassian.confluence.setup.settings.Settings;

public class DefaultTestSettings extends Settings {

    public static final String BASEURL = "https://confluence.aservo.com";
    public static final String TITLE = "ASERVO Confluence";

    public DefaultTestSettings() {
        setBaseUrl(BASEURL);
        setSiteTitle(TITLE);
    }

}
