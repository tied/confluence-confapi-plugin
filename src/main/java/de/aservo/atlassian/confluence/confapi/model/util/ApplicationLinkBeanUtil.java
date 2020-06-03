package de.aservo.atlassian.confluence.confapi.model.util;

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
import de.aservo.confapi.commons.model.type.ApplicationLinkTypes;
import org.apache.commons.lang3.NotImplementedException;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;

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
        applicationLinkBean.setServerId(linkDetails.getId().toString());
        applicationLinkBean.setAppType(linkDetails.getType().toString());
        applicationLinkBean.setName(linkDetails.getName());
        applicationLinkBean.setDisplayUrl(linkDetails.getDisplayUrl().toString());
        applicationLinkBean.setRpcUrl(linkDetails.getRpcUrl().toString());
        applicationLinkBean.setPrimary(linkDetails.isPrimary());
        applicationLinkBean.setLinkType(getLinkTypeFromAppType(linkDetails.getType()));
        return applicationLinkBean;
    }

    /**
     * To application link details application link details.
     *
     * @return the application link details
     * @throws URISyntaxException the uri syntax exception
     */
    @NotNull
    public static ApplicationLinkDetails toApplicationLinkDetails(
            @NotNull final ApplicationLinkBean applicationLinkBean) throws URISyntaxException {

        return ApplicationLinkDetails.builder()
                .name(applicationLinkBean.getName())
                .displayUrl(new URI(applicationLinkBean.getDisplayUrl()))
                .rpcUrl(new URI(applicationLinkBean.getRpcUrl()))
                .isPrimary(applicationLinkBean.isPrimary())
                .build();
    }

    /**
     * Gets the linktype ApplicationLinkTypes enum value.
     *
     * @param type the ApplicationType
     * @return the linktype
     */
    private static ApplicationLinkTypes getLinkTypeFromAppType(
            @NotNull final ApplicationType type) {

        if (type instanceof BambooApplicationType) {
            return ApplicationLinkTypes.BAMBOO;
        } else if (type instanceof JiraApplicationType) {
            return ApplicationLinkTypes.JIRA;
        } else if (type instanceof BitbucketApplicationType) {
            return ApplicationLinkTypes.BITBUCKET;
        } else if (type instanceof ConfluenceApplicationType) {
            return ApplicationLinkTypes.CONFLUENCE;
        } else if (type instanceof FishEyeCrucibleApplicationType) {
            return ApplicationLinkTypes.FISHEYE;
        } else if (type instanceof CrowdApplicationType) {
            return ApplicationLinkTypes.CROWD;
        } else {
            throw new NotImplementedException("application type '" + type.getClass() + "' not implemented");
        }
    }

    private ApplicationLinkBeanUtil() {
    }

}
