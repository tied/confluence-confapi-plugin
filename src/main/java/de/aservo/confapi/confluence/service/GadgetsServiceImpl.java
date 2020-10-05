package de.aservo.confapi.confluence.service;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpec;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.confapi.commons.model.GadgetBean;
import de.aservo.confapi.commons.model.GadgetsBean;
import de.aservo.confapi.commons.service.api.GadgetsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.net.URI;
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
                .map(ExternalGadgetSpec::getSpecUri)
                .map(url -> {
                    GadgetBean gadgetBean = new GadgetBean();
                    gadgetBean.setUrl(url);
                    return gadgetBean;
                }).collect(Collectors.toList());
        return new GadgetsBean(gadgetBeanList);
    }

    @Override
    public GadgetBean getGadget(long l) {
        return null;
    }

    @Override
    public GadgetsBean setGadgets(GadgetsBean gadgetsBean) {
        //remove existing gadgets before adding the new ones
        externalGadgetSpecStore.entries().forEach(gadget -> externalGadgetSpecStore.remove(gadget.getId()));
        gadgetsBean.getGadgets().forEach(this::addGadget);
        return getGadgets();
    }

    @Override
    public GadgetBean setGadget(long l, @NotNull GadgetBean gadgetBean) {
        return null;
    }

    @Override
    public GadgetBean addGadget(GadgetBean gadgetBean) {
        //initial checks
        URI url = gadgetBean.getUrl();

        //validate gadget url
        log.debug("testing external gadget link url for validity: {}", url);
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Locale locale = localeManager.getLocale(user);
        GadgetRequestContext requestContext = GadgetRequestContext.Builder.gadgetRequestContext()
                .locale(locale)
                .ignoreCache(false)
                .user(new GadgetRequestContext.User(user.getKey().getStringValue(), user.getName()))
                .build();
        gadgetSpecFactory.getGadgetSpec(url, requestContext);

        //add gadget url to store
        ExternalGadgetSpec addedGadget = externalGadgetSpecStore.add(url);

        GadgetBean addedGadgetBean = new GadgetBean();
        addedGadgetBean.setUrl(addedGadget.getSpecUri());
        return addedGadgetBean;
    }

    @Override
    public void deleteGadgets(boolean b) {

    }

    @Override
    public void deleteGadget(long l) {

    }
}
