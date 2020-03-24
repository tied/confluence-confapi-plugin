package com.atlassian.confluence.settings.setup;

import com.atlassian.confluence.setup.settings.Settings;

public class OtherTestSettings extends Settings {

    public static final String BASEURL = "http://localhost:1990/confluence";
    public static final String TITLE = "Other Confluence";

    public OtherTestSettings() {
        setBaseUrl(BASEURL);
        setSiteTitle(TITLE);
    }

}
