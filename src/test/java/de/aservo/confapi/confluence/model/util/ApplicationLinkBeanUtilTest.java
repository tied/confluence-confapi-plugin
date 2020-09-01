package de.aservo.confapi.confluence.model.util;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.application.bamboo.BambooApplicationType;
import com.atlassian.applinks.api.application.bitbucket.BitbucketApplicationType;
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType;
import com.atlassian.applinks.api.application.crowd.CrowdApplicationType;
import com.atlassian.applinks.api.application.fecru.FishEyeCrucibleApplicationType;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.api.application.refapp.RefAppApplicationType;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.confluence.settings.setup.DefaultApplicationLink;
import com.atlassian.confluence.settings.setup.DefaultApplicationType;
import de.aservo.confapi.commons.model.ApplicationLinkBean;
import de.aservo.confapi.commons.model.type.ApplicationLinkTypes;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationLinkBeanUtilTest {

    @Test
    public void testToApplicationLinkBean() throws URISyntaxException {
        final ApplicationId applicationId = new ApplicationId(UUID.randomUUID().toString());
        final URI displayUri = new URI("http://localhost");
        final URI rpcUri = new URI("http://rpc.example.com");
        final ApplicationLink applicationLink = new DefaultApplicationLink(
                applicationId, new DefaultApplicationType(), "test", displayUri, rpcUri, false, false);
        final ApplicationLinkBean bean =ApplicationLinkBeanUtil.toApplicationLinkBean(applicationLink);

        assertNotNull(bean);
        assertEquals(bean.getName(), applicationLink.getName());
        assertEquals(bean.getDisplayUrl(), applicationLink.getDisplayUrl().toString());
        assertEquals(bean.getRpcUrl(), applicationLink.getRpcUrl().toString());
        assertEquals(bean.isPrimary(), applicationLink.isPrimary());
    }

    @Test
    public void testToApplicationLinkDetails() throws Exception {
        final ApplicationLinkBean bean = ApplicationLinkBean.EXAMPLE_1;
        final ApplicationLinkDetails linkDetails = ApplicationLinkBeanUtil.toApplicationLinkDetails(bean);

        assertNotNull(linkDetails);
        assertEquals(bean.getName(), linkDetails.getName());
        assertEquals(bean.getDisplayUrl(), linkDetails.getDisplayUrl().toString());
        assertEquals(bean.getRpcUrl(), linkDetails.getRpcUrl().toString());
        assertEquals(bean.isPrimary(), linkDetails.isPrimary());
    }

    @Test
    public void testLinkTypeGenerator() throws URISyntaxException {
        for (ApplicationLinkTypes linkType : ApplicationLinkTypes.values()) {
            ApplicationType applicationType = null;
            switch (linkType) {
                case BAMBOO:
                    applicationType = mock(BambooApplicationType.class);
                    break;
                case JIRA:
                    applicationType = mock(JiraApplicationType.class);
                    break;
                case BITBUCKET:
                    applicationType = mock(BitbucketApplicationType.class);
                    break;
                case CONFLUENCE:
                    applicationType = mock(ConfluenceApplicationType.class);
                    break;
                case FISHEYE:
                    applicationType = mock(FishEyeCrucibleApplicationType.class);
                    break;
                case CROWD:
                    applicationType = mock(CrowdApplicationType.class);
                    break;
            }
            ApplicationId applicationId = new ApplicationId(UUID.randomUUID().toString());
            URI uri = new URI("http://localhost");
            ApplicationLink applicationLink = new DefaultApplicationLink(
                    applicationId, applicationType, "test", uri, uri, false, false);
            ApplicationLinkBean bean = ApplicationLinkBeanUtil.toApplicationLinkBean(applicationLink);
            assertEquals(linkType, bean.getLinkType());
        }
    }

    @Test(expected = NotImplementedException.class)
    public void testNonImplementedLinkTypeGenerator() throws URISyntaxException {
        ApplicationType applicationType = mock(RefAppApplicationType.class);
        ApplicationId applicationId = new ApplicationId(UUID.randomUUID().toString());
        URI uri = new URI("http://localhost");
        ApplicationLink applicationLink = new DefaultApplicationLink(
                applicationId, applicationType, "test", uri, uri, false, false);
        ApplicationLinkBeanUtil.toApplicationLinkBean(applicationLink);
    }

}
