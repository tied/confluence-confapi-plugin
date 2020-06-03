package de.aservo.atlassian.confluence.confapi.model.util;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import de.aservo.confapi.commons.model.DirectoryBean;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static com.atlassian.crowd.directory.RemoteCrowdDirectory.*;
import static com.atlassian.crowd.directory.SynchronisableDirectoryProperties.*;
import static com.atlassian.crowd.directory.SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.WHEN_AUTHENTICATION_CREATED_THE_USER;
import static com.atlassian.crowd.model.directory.DirectoryImpl.ATTRIBUTE_KEY_USE_NESTED_GROUPS;

public class DirectoryBeanUtil {

    /**
     * Build directory directory.
     *
     * @return the directory
     */
    @NotNull
    public static Directory toDirectory(
            @NotNull final DirectoryBean directoryBean) {

        final Map<String, String> attributes = new HashMap<>();
        attributes.put(CROWD_SERVER_URL, directoryBean.getCrowdUrl());
        attributes.put(APPLICATION_PASSWORD, directoryBean.getAppPassword());
        attributes.put(APPLICATION_NAME, directoryBean.getClientName());
        attributes.put(CROWD_HTTP_PROXY_HOST, directoryBean.getProxyHost());
        attributes.put(CROWD_HTTP_PROXY_PORT, directoryBean.getProxyPort());
        attributes.put(CROWD_HTTP_PROXY_USERNAME, directoryBean.getProxyUsername());
        attributes.put(CROWD_HTTP_PROXY_PASSWORD, directoryBean.getProxyPassword());
        attributes.put(CACHE_SYNCHRONISE_INTERVAL, "3600");
        attributes.put(ATTRIBUTE_KEY_USE_NESTED_GROUPS, "false");
        attributes.put(INCREMENTAL_SYNC_ENABLED, "true");
        attributes.put(SYNC_GROUP_MEMBERSHIP_AFTER_SUCCESSFUL_USER_AUTH_ENABLED, WHEN_AUTHENTICATION_CREATED_THE_USER.getValue());

        return ImmutableDirectory.builder(directoryBean.getName(), DirectoryBeanUtil.getDirectoryType(directoryBean), directoryBean.getImplClass())
                .setActive(directoryBean.isActive())
                .setAttributes(attributes)
                .build();
    }

    /**
     * Build user directory bean user directory bean.
     *
     * @param directory the directory
     * @return the user directory bean
     */
    @NotNull
    public static DirectoryBean toDirectoryBean(
            @NotNull final Directory directory) {

        final Map<String, String> attributes = directory.getAttributes();
        final DirectoryBean directoryBean = new DirectoryBean();
        directoryBean.setName(directory.getName());
        directoryBean.setActive(directory.isActive());
        directoryBean.setType(directory.getType().name());
        directoryBean.setDescription(directory.getDescription());
        directoryBean.setCrowdUrl(attributes.get(CROWD_SERVER_URL));
        directoryBean.setClientName(attributes.get(APPLICATION_NAME));
        directoryBean.setProxyHost(attributes.get(CROWD_HTTP_PROXY_HOST));
        directoryBean.setProxyPort(attributes.get(CROWD_HTTP_PROXY_PORT));
        directoryBean.setProxyUsername(attributes.get(CROWD_HTTP_PROXY_USERNAME));
        directoryBean.setImplClass(directory.getImplementationClass());
        return directoryBean;
    }

    public static DirectoryType getDirectoryType(
            @NotNull final DirectoryBean directoryBean) {

        return DirectoryType.valueOf(directoryBean.getType().toUpperCase());
    }

    private DirectoryBeanUtil() {
    }

}
