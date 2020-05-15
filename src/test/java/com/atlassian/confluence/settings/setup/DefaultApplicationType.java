package com.atlassian.confluence.settings.setup;

import com.atlassian.applinks.api.application.jira.JiraApplicationType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;

public class DefaultApplicationType implements JiraApplicationType {

    @Nonnull
    @Override
    public String getI18nKey() {
        return "";
    }

    @Nullable
    @Override
    public URI getIconUrl() {
        return null;
    }

}
