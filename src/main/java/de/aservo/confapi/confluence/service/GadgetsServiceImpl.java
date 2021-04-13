package de.aservo.confapi.confluence.service;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpec;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.confapi.commons.exception.BadRequestException;
import de.aservo.confapi.commons.exception.NotFoundException;
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
import java.util.Optional;
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
                .map(spec -> {
                    GadgetBean gadgetBean = new GadgetBean();
                    gadgetBean.setId(Long.valueOf(spec.getId().value()));
                    gadgetBean.setUrl(spec.getSpecUri());
                    return gadgetBean;
                }).collect(Collectors.toList());
        return new GadgetsBean(gadgetBeanList);
    }

    @Override
    public GadgetBean getGadget(long id) {
        return findGadget(id);
    }

    @Override
    public GadgetsBean setGadgets(GadgetsBean gadgetsBean) {
        //as the gadget only consists of an url, only new gadgets need to be added, existing gadget urls remain
        GadgetsBean existingGadgets = getGadgets();
        gadgetsBean.getGadgets().forEach(gadgetBean -> {
            Optional<GadgetBean> gadget = existingGadgets.getGadgets().stream()
                    .filter(bean -> bean.getUrl().toString().equals(gadgetBean.getUrl().toString())).findFirst();
            if (!gadget.isPresent()) {
                addGadget(gadgetBean);
            }
        });
        return getGadgets();
    }

    @Override
    public GadgetBean setGadget(long id, @NotNull GadgetBean gadgetBean) {
        deleteGadget(id);
        return addGadget(gadgetBean);
    }

    @Override
    public GadgetBean addGadget(GadgetBean gadgetBean) {
        URI url = gadgetBean.getUrl();
        GadgetBean addedGadgetBean = new GadgetBean();
        ExternalGadgetSpec addedGadget = externalGadgetSpecStore.add(url);
        try{
            //validate gadget url
            log.debug("testing external gadget link url for validity: {}", url);

            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            Locale locale = localeManager.getLocale(user);
            GadgetRequestContext requestContext = GadgetRequestContext.Builder.gadgetRequestContext()
                    .locale(locale)
                    .ignoreCache(true)
                    .user(new GadgetRequestContext.User(user.getKey().getStringValue(), user.getName()))
                    .build();
            gadgetSpecFactory.getGadgetSpec(url, requestContext);

            addedGadgetBean.setUrl(addedGadget.getSpecUri());
        } catch (GadgetParsingException e) {
            externalGadgetSpecStore.remove(addedGadget.getId());
            throw new BadRequestException("Invalid Gadget URL");
        }
        return addedGadgetBean;
    }

    @Override
    public void deleteGadgets(boolean force) {
        if (!force) {
            throw new BadRequestException("'force = true' must be supplied to delete all entries");
        } else {
            externalGadgetSpecStore.entries().forEach(gadget -> externalGadgetSpecStore.remove(gadget.getId()));
        }
    }

    @Override
    public void deleteGadget(long id) {

        //ensure gadget exists
        findGadget(id);

        //remove gadget
        externalGadgetSpecStore.remove(ExternalGadgetSpecId.valueOf(String.valueOf(id)));
    }

    private GadgetBean findGadget(long id) {
        Optional<GadgetBean> result = getGadgets().getGadgets().stream().filter(gadget -> gadget.getId().equals(id)).findFirst();
        if (!result.isPresent()) {
            throw new NotFoundException(String.format("gadget with id '%s' could not be found", id));
        } else {
            return result.get();
        }
    }
}
