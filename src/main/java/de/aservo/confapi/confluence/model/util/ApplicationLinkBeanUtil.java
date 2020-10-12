package de.aservo.confapi.confluence.model.util;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.application.bamboo.BambooApplicationType;
import com.atlassian.applinks.api.application.bitbucket.BitbucketApplicationType;
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType;
import com.atlassian.applinks.api.application.crowd.CrowdApplicationType;
import com.atlassian.applinks.api.application.fecru.FishEyeCrucibleApplicationType;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import de.aservo.confapi.commons.model.ApplicationLinkBean;
import de.aservo.confapi.commons.model.ApplicationLinkBean.ApplicationLinkType;
import org.apache.commons.lang3.NotImplementedException;

import javax.validation.constraints.NotNull;
import java.util.UUID;

import static de.aservo.confapi.commons.model.ApplicationLinkBean.ApplicationLinkType.*;

public class ApplicationLinkBeanUtil {

    /**
     * Instantiates a new Application link bean.
     *
     * @param linkDetails the link details
     */
    @NotNull
    public static ApplicationLinkBean toApplicationLinkBean(
            @NotNull final ApplicationLink linkDetails) {

        final ApplicationLinkBean applicationLinkBean = new ApplicationLinkBean();
        applicationLinkBean.setUuid(UUID.fromString(linkDetails.getId().get()));
        applicationLinkBean.setName(linkDetails.getName());
        applicationLinkBean.setType(getLinkTypeFromAppType(linkDetails.getType()));
        applicationLinkBean.setDisplayUrl(linkDetails.getDisplayUrl());
        applicationLinkBean.setRpcUrl(linkDetails.getRpcUrl());
        applicationLinkBean.setPrimary(linkDetails.isPrimary());
        return applicationLinkBean;
    }

    /**
     * To application link details application link details.
     *
     * @return the application link details
     */
    @NotNull
    public static ApplicationLinkDetails toApplicationLinkDetails(
            @NotNull final ApplicationLinkBean applicationLinkBean) {

        return ApplicationLinkDetails.builder()
                .name(applicationLinkBean.getName())
                .displayUrl(applicationLinkBean.getDisplayUrl())
                .rpcUrl(applicationLinkBean.getRpcUrl())
                .isPrimary(applicationLinkBean.isPrimary())
                .build();
    }

    /**
     * Gets the linktype ApplicationLinkTypes enum value.
     *
     * @param type the ApplicationType
     * @return the linktype
     */
    private static ApplicationLinkType getLinkTypeFromAppType(
            @NotNull final ApplicationType type) {

        if (type instanceof BambooApplicationType) {
            return BAMBOO;
        } else if (type instanceof JiraApplicationType) {
            return JIRA;
        } else if (type instanceof BitbucketApplicationType) {
            return BITBUCKET;
        } else if (type instanceof ConfluenceApplicationType) {
            return CONFLUENCE;
        } else if (type instanceof FishEyeCrucibleApplicationType) {
            return FISHEYE;
        } else if (type instanceof CrowdApplicationType) {
            return CROWD;
        } else {
            throw new NotImplementedException("application type '" + type.getClass() + "' not implemented");
        }
    }

    private ApplicationLinkBeanUtil() {
    }

}
