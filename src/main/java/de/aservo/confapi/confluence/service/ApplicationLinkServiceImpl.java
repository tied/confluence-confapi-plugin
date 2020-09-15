package de.aservo.confapi.confluence.service;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.application.bamboo.BambooApplicationType;
import com.atlassian.applinks.api.application.bitbucket.BitbucketApplicationType;
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType;
import com.atlassian.applinks.api.application.crowd.CrowdApplicationType;
import com.atlassian.applinks.api.application.fecru.FishEyeCrucibleApplicationType;
import com.atlassian.applinks.api.application.jira.JiraApplicationType;
import com.atlassian.applinks.core.ApplinkStatus;
import com.atlassian.applinks.core.ApplinkStatusService;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.confapi.commons.exception.BadRequestException;
import de.aservo.confapi.commons.model.ApplicationLinkBean;
import de.aservo.confapi.commons.model.ApplicationLinksBean;
import de.aservo.confapi.commons.service.api.ApplicationLinksService;
import de.aservo.confapi.confluence.model.DefaultAuthenticationScenario;
import de.aservo.confapi.confluence.model.util.ApplicationLinkBeanUtil;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.atlassian.applinks.internal.status.error.ApplinkErrorType.CONNECTION_REFUSED;
import static de.aservo.confapi.commons.model.ApplicationLinkBean.ApplicationLinkStatus.*;
import static de.aservo.confapi.commons.util.BeanValidationUtil.validate;
import static de.aservo.confapi.confluence.model.util.ApplicationLinkBeanUtil.toApplicationLinkBean;

@Component
@ExportAsService(ApplicationLinksService.class)
public class ApplicationLinkServiceImpl implements ApplicationLinksService {

    private static final Logger log = LoggerFactory.getLogger(ApplicationLinkServiceImpl.class);

    private final MutatingApplicationLinkService mutatingApplicationLinkService;
    private final TypeAccessor typeAccessor;
    private final ApplinkStatusService applinkStatusService;

    @Inject
    public ApplicationLinkServiceImpl(@ComponentImport MutatingApplicationLinkService mutatingApplicationLinkService,
                                      @ComponentImport TypeAccessor typeAccessor,
                                      @ComponentImport ApplinkStatusService applinkStatusService) {
        this.mutatingApplicationLinkService = mutatingApplicationLinkService;
        this.typeAccessor = typeAccessor;
        this.applinkStatusService = applinkStatusService;
    }

    @Override
    public ApplicationLinksBean getApplicationLinks() {
        Iterable<ApplicationLink> applicationLinksIterable = mutatingApplicationLinkService.getApplicationLinks();

        List<ApplicationLinkBean> applicationLinkBeans = StreamSupport.stream(applicationLinksIterable.spliterator(),false)
                .map(this::getApplicationLinkBeanWithStatus)
                .collect(Collectors.toList());

        return new ApplicationLinksBean(applicationLinkBeans);
    }


    @Override
    public ApplicationLinksBean setApplicationLinks(
            final ApplicationLinksBean applicationLinksBean,
            final boolean ignoreSetupErrors) {

        applicationLinksBean.getApplicationLinks().forEach(link -> addApplicationLink(link, ignoreSetupErrors));
        return getApplicationLinks();
    }

    @Override
    public ApplicationLinkBean addApplicationLink(
            final ApplicationLinkBean linkBean,
            final boolean ignoreSetupErrors) {

        //preparations
        validate(linkBean);

        ApplicationLinkDetails linkDetails = ApplicationLinkBeanUtil.toApplicationLinkDetails(linkBean);
        ApplicationType applicationType = buildApplicationType(linkBean.getType());

        //check if there is already an application link of supplied type and if yes, remove it
        Class<? extends ApplicationType> appType = applicationType != null ? applicationType.getClass() : null;
        ApplicationLink primaryApplicationLink = mutatingApplicationLinkService.getPrimaryApplicationLink(appType);
        if (primaryApplicationLink != null) {
            log.info("An existing application link configuration '{}' was found and is removed now before adding the new configuration",
                    primaryApplicationLink.getName());
            mutatingApplicationLinkService.deleteApplicationLink(primaryApplicationLink);
        }

        //add new application link, this should always work - even if remote app is not accessible
        ApplicationLink applicationLink;
        try {
            applicationLink = mutatingApplicationLinkService.createApplicationLink(applicationType, linkDetails);
        } catch (ManifestNotFoundException e) {
            throw new BadRequestException(e.getMessage());
        }

        //configure authenticator, this might fail if setup is incorrect or remote app is unavailable
        try {
            mutatingApplicationLinkService.configureAuthenticationForApplicationLink(applicationLink,
                    new DefaultAuthenticationScenario(), linkBean.getUsername(), linkBean.getPassword());
        } catch (AuthenticationConfigurationException e) {
            if (!ignoreSetupErrors) {
                throw new BadRequestException(e.getMessage());
            }
        }

        return getApplicationLinkBeanWithStatus(applicationLink);
    }

    private ApplicationType buildApplicationType(ApplicationLinkBean.ApplicationLinkTypes linkType) {
        switch (linkType) {
            case BAMBOO:
                return typeAccessor.getApplicationType(BambooApplicationType.class);
            case JIRA:
                return typeAccessor.getApplicationType(JiraApplicationType.class);
            case BITBUCKET:
                return typeAccessor.getApplicationType(BitbucketApplicationType.class);
            case CONFLUENCE:
                return typeAccessor.getApplicationType(ConfluenceApplicationType.class);
            case FISHEYE:
                return typeAccessor.getApplicationType(FishEyeCrucibleApplicationType.class);
            case CROWD:
                return typeAccessor.getApplicationType(CrowdApplicationType.class);
            default:
                throw new NotImplementedException("application type '" + linkType + "' not implemented");
        }
    }

    private ApplicationLinkBean getApplicationLinkBeanWithStatus(ApplicationLink applicationLink) {

        ApplicationLinkBean applicationLinkBean = toApplicationLinkBean(applicationLink);

        try {
            ApplinkStatus applinkStatus = applinkStatusService.getApplinkStatus(applicationLink.getId());
            if (applinkStatus.isWorking()) {
                applicationLinkBean.setStatus(AVAILABLE);
            } else {
                if (applinkStatus.getError() != null && CONNECTION_REFUSED.equals(applinkStatus.getError().getType())) {
                    applicationLinkBean.setStatus(UNAVAILABLE);
                } else {
                    applicationLinkBean.setStatus(CONFIGURATION_ERROR);
                }
            }
        } catch (NoAccessException | NoSuchApplinkException e) {
            applicationLinkBean.setStatus(CONFIGURATION_ERROR);
        }

        return applicationLinkBean;
    }
}
