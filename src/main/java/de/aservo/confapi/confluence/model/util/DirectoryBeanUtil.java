package de.aservo.confapi.confluence.model.util;

import com.atlassian.crowd.directory.RemoteCrowdDirectory;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import de.aservo.confapi.commons.model.AbstractDirectoryBean;
import de.aservo.confapi.commons.model.DirectoryCrowdBean;
import de.aservo.confapi.commons.model.DirectoryCrowdBean.DirectoryCrowdAdvanced;
import de.aservo.confapi.commons.model.DirectoryCrowdBean.DirectoryCrowdServer;
import de.aservo.confapi.commons.model.DirectoryCrowdBean.DirectoryCrowdServer.DirectoryCrowdServerProxy;
import de.aservo.confapi.commons.model.DirectoryGenericBean;
import de.aservo.confapi.commons.model.DirectoryInternalBean;
import de.aservo.confapi.commons.model.DirectoryLdapBean;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.atlassian.crowd.directory.RemoteCrowdDirectory.*;
import static com.atlassian.crowd.directory.SynchronisableDirectoryProperties.*;
import static com.atlassian.crowd.model.directory.DirectoryImpl.ATTRIBUTE_KEY_USE_NESTED_GROUPS;
import static de.aservo.confapi.commons.util.ConversionUtil.*;

public class DirectoryBeanUtil {

    /**
     * Build directory directory.
     *
     * @return the directory
     */
    @NotNull
    public static Directory toDirectory(
            @NotNull final DirectoryCrowdBean directoryBean) {

        final Map<String, String> attributes = new HashMap<>();
        if (directoryBean.getServer() != null) {
            attributes.put(CROWD_SERVER_URL, directoryBean.getServer().getUrl().toString());
            attributes.put(APPLICATION_NAME, directoryBean.getServer().getAppUsername());
            attributes.put(APPLICATION_PASSWORD, directoryBean.getServer().getAppPassword());
            if (directoryBean.getServer().getProxy() != null) {
                attributes.put(CROWD_HTTP_PROXY_HOST, directoryBean.getServer().getProxy().getHost());
                if (directoryBean.getServer().getProxy().getPort() != null) {
                    attributes.put(CROWD_HTTP_PROXY_PORT, directoryBean.getServer().getProxy().getPort().toString());
                }
                attributes.put(CROWD_HTTP_PROXY_USERNAME, directoryBean.getServer().getProxy().getUsername());
                attributes.put(CROWD_HTTP_PROXY_PASSWORD, directoryBean.getServer().getProxy().getPassword());
            }
        }
        if (directoryBean.getAdvanced() != null) {
            attributes.put(CACHE_SYNCHRONISE_INTERVAL, directoryBean.getAdvanced().getUpdateSyncIntervalInMinutes() != 0 ?
                    String.valueOf(directoryBean.getAdvanced().getUpdateSyncIntervalInMinutes()) : "3600");
            attributes.put(ATTRIBUTE_KEY_USE_NESTED_GROUPS, String.valueOf(directoryBean.getAdvanced().getEnableNestedGroups()));
            attributes.put(INCREMENTAL_SYNC_ENABLED, String.valueOf(directoryBean.getAdvanced().getEnableIncrementalSync()));
            attributes.put(SYNC_GROUP_MEMBERSHIP_AFTER_SUCCESSFUL_USER_AUTH_ENABLED, directoryBean.getAdvanced().getUpdateGroupMembershipMethod());
        }

        return ImmutableDirectory.builder(directoryBean.getName(), DirectoryBeanUtil.getDirectoryType(directoryBean), RemoteCrowdDirectory.class.getName())
                .setActive(directoryBean.getActive() != null && directoryBean.getActive())
                .setDescription(directoryBean.getDescription())
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
    public static AbstractDirectoryBean toDirectoryBean(
            @NotNull final Directory directory) {

        final Map<String, String> attributes = directory.getAttributes();
        final AbstractDirectoryBean directoryBean;

        if (DirectoryType.CROWD.equals(directory.getType())) {

            DirectoryCrowdServer serverBean = new DirectoryCrowdServer();
            serverBean.setUrl(URI.create(attributes.get(CROWD_SERVER_URL)));
            if (attributes.containsKey(CROWD_HTTP_PROXY_HOST)) {
                DirectoryCrowdServerProxy proxy = new DirectoryCrowdServerProxy();
                proxy.setUsername(attributes.get(CROWD_HTTP_PROXY_USERNAME));
                proxy.setHost(attributes.get(CROWD_HTTP_PROXY_HOST));
                if (attributes.get(CROWD_HTTP_PROXY_PORT) != null) {
                    proxy.setPort(Integer.valueOf(attributes.get(CROWD_HTTP_PROXY_PORT)));
                }
                serverBean.setProxy(proxy);
            }
            serverBean.setConnectionTimeoutInMillis(toLong(attributes.get(CROWD_HTTP_TIMEOUT)));
            serverBean.setMaxConnections(toInt(attributes.get(CROWD_HTTP_MAX_CONNECTIONS)));
            serverBean.setAppUsername(attributes.get(APPLICATION_NAME));

            DirectoryCrowdAdvanced advanced = new DirectoryCrowdAdvanced();
            advanced.setEnableIncrementalSync(toBoolean(attributes.get(INCREMENTAL_SYNC_ENABLED)));
            advanced.setEnableNestedGroups(toBoolean(attributes.get(ATTRIBUTE_KEY_USE_NESTED_GROUPS)));
            advanced.setUpdateSyncIntervalInMinutes(toInt(attributes.get(CACHE_SYNCHRONISE_INTERVAL)));

            DirectoryCrowdBean directoryCrowdBean = new DirectoryCrowdBean();
            directoryCrowdBean.setServer(serverBean);
            directoryCrowdBean.setAdvanced(advanced);
            directoryBean = directoryCrowdBean;

        } else  {
            directoryBean = new DirectoryGenericBean();
        }

        directoryBean.setId(directory.getId());
        directoryBean.setName(directory.getName());
        directoryBean.setActive(directory.isActive());
        directoryBean.setDescription(directory.getDescription());
        return directoryBean;
    }

    public static DirectoryType getDirectoryType(
            @NotNull final AbstractDirectoryBean directoryBean) {
        if (directoryBean instanceof DirectoryInternalBean) {
            return DirectoryType.INTERNAL;
        } else if (directoryBean instanceof DirectoryCrowdBean) {
            return DirectoryType.CROWD;
        } else if (directoryBean instanceof DirectoryLdapBean) {
            return DirectoryType.DELEGATING;
        } else {
            return DirectoryType.UNKNOWN;
        }
    }

    private DirectoryBeanUtil() {
    }

}
