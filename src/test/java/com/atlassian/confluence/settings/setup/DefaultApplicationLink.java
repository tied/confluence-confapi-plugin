package com.atlassian.confluence.settings.setup;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URI;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DefaultApplicationLink implements ApplicationLink {

    private ApplicationId id;
    private ApplicationType type;
    private String name;
    private URI displayUrl;
    private URI rpcUrl;
    private boolean primary;
    private boolean system;

    @Override
    public Object getProperty(String s) {
        return null;
    }

    @Override
    public Object putProperty(String s, Object o) {
        return null;
    }

    @Override
    public Object removeProperty(String s) {
        return null;
    }

    @Override
    public ApplicationLinkRequestFactory createAuthenticatedRequestFactory() {
        return null;
    }

    @Override
    public ApplicationLinkRequestFactory createAuthenticatedRequestFactory(Class<? extends AuthenticationProvider> aClass) {
        return null;
    }

    @Override
    public ApplicationLinkRequestFactory createImpersonatingAuthenticatedRequestFactory() {
        return null;
    }

    @Override
    public ApplicationLinkRequestFactory createNonImpersonatingAuthenticatedRequestFactory() {
        return null;
    }
}
