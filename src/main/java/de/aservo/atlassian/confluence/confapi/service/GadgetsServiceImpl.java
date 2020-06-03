package de.aservo.atlassian.confluence.confapi.service;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpec;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.confapi.commons.exception.BadRequestException;
import de.aservo.confapi.commons.model.GadgetBean;
import de.aservo.confapi.commons.model.GadgetsBean;
import de.aservo.confapi.commons.service.api.GadgetsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@ExportAsService(GadgetsService.class)
public class GadgetsServiceImpl implements GadgetsService {

    private static final Logger log = LoggerFactory.getLogger(GadgetsServiceImpl.class);


    private final ExternalGadgetSpecStore externalGadgetSpecStore;
    private final GadgetSpecFactory gadgetSpecFactory;
    private final LocaleManager localeManager;

    @Inject
    public GadgetsServiceImpl(
            @ComponentImport ExternalGadgetSpecStore externalGadgetSpecStore,
            @ComponentImport GadgetSpecFactory gadgetSpecFactory,
            @ComponentImport LocaleManager localeManager) {
        this.externalGadgetSpecStore = externalGadgetSpecStore;
        this.gadgetSpecFactory = gadgetSpecFactory;
        this.localeManager = localeManager;
    }

    @Override
    public GadgetsBean getGadgets() {
        Iterable<ExternalGadgetSpec> specIterable = externalGadgetSpecStore.entries();
        List<GadgetBean> gadgetBeanList = StreamSupport.stream(specIterable.spliterator(), false)
                .map(spec -> spec.getSpecUri().toString())
                .map(url -> {
                    GadgetBean gadgetBean = new GadgetBean();
                    gadgetBean.setUrl(url);
                    return gadgetBean;
                }).collect(Collectors.toList());
        return new GadgetsBean(gadgetBeanList);
    }

    @Override
    public GadgetsBean setGadgets(GadgetsBean gadgetsBean) {
        //remove existing gadgets before adding the new ones
        externalGadgetSpecStore.entries().forEach(gadget -> externalGadgetSpecStore.remove(gadget.getId()));
        gadgetsBean.getGadgets().forEach(this::addGadget);
        return getGadgets();
    }

    @Override
    public GadgetsBean addGadget(GadgetBean gadgetBean) {
        //initial checks
        String url = gadgetBean.getUrl();
        if (StringUtils.isBlank(url)) {
            throw new BadRequestException("'url' must not be null or empty!");
        }
        URI uri;
        try {
            uri = new URI(url.trim());
        } catch (URISyntaxException e) {
            throw new BadRequestException(String.format("Cannot interpret gadget url '%s'", url));
        }

        //validate gadget url
        log.debug("testing external gadget link url for validity: {}", url);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Locale locale = localeManager.getLocale(user);
        GadgetRequestContext requestContext = GadgetRequestContext.Builder.gadgetRequestContext()
                .locale(locale)
                .ignoreCache(false)
                .user(new GadgetRequestContext.User(user.getKey().getStringValue(), user.getName()))
                .build();
        gadgetSpecFactory.getGadgetSpec(uri, requestContext);

        //add gadget url to store
        externalGadgetSpecStore.add(uri);

        return getGadgets();
    }
}
