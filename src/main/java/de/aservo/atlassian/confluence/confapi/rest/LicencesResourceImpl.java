//package de.aservo.atlassian.confluence.confapi.rest;
//
//import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
//import com.atlassian.sal.api.license.LicenseHandler;
//import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
//import com.sun.jersey.spi.container.ResourceFilters;
//import de.aservo.atlassian.confapi.constants.ConfAPI;
//import de.aservo.atlassian.confapi.model.ErrorCollection;
//import de.aservo.atlassian.confapi.model.LicensesBean;
//import de.aservo.atlassian.confapi.rest.api.LicensesResource;
//import de.aservo.atlassian.confluence.confapi.filter.AdminOnlyResourceFilter;
//import de.aservo.atlassian.confluence.confapi.model.util.LicenseBeanUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import javax.inject.Inject;
//import javax.validation.constraints.NotNull;
//import javax.ws.rs.DefaultValue;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import java.util.Collections;
//
//import static com.atlassian.confluence.setup.ConfluenceBootstrapConstants.DEFAULT_LICENSE_REGISTRY_KEY;
//
///**
// * The type Licence resource.
// */
//@Path(ConfAPI.LICENSE)
//@Produces(MediaType.APPLICATION_JSON)
//@ResourceFilters(AdminOnlyResourceFilter.class)
//@Component
//public class LicencesResourceImpl implements LicensesResource {
//
//    private static final Logger log = LoggerFactory.getLogger(LicencesResourceImpl.class);
//
//    private final LicenseHandler licenseHandler;
//
//    /**
//     * Instantiates a new Licence resource.
//     *
//     * @param licenseHandler the license handler
//     */
//    @Inject
//    public LicencesResourceImpl(@ComponentImport LicenseHandler licenseHandler) {
//        this.licenseHandler = licenseHandler;
//    }
//
//    /**
//     * Gets license.
//     *
//     * @return the license
//     */
//    @Override
//    public Response getLicenses() {
//        final ErrorCollection errorCollection = new ErrorCollection();
//        try {
//            SingleProductLicenseDetailsView conf = licenseHandler.getProductLicenseDetails(DEFAULT_LICENSE_REGISTRY_KEY);
//            LicensesBean licensesBean = new LicensesBean();
//            licensesBean.setLicenses(Collections.singletonList(LicenseBeanUtil.toLicenseBean(conf)));
//            return Response.ok(licensesBean).build();
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            errorCollection.addErrorMessage(e.getMessage());
//        }
//        return Response.status(Response.Status.BAD_REQUEST).entity(errorCollection).build();
//    }
//
//    /**
//     * Add license.
//     *
//     * @param licenseKey the license key to add
//     * @return
//     */
//    @Override
//    public Response setLicense(
//            @QueryParam("clear") @DefaultValue("false") final boolean clear,
//            @NotNull final String licenseKey) {
//
//        final ErrorCollection errorCollection = new ErrorCollection();
//        try {
//            licenseHandler.addProductLicense(DEFAULT_LICENSE_REGISTRY_KEY, licenseKey);
//            return getLicenses();
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            errorCollection.addErrorMessage(e.getMessage());
//        }
//        return Response.status(Response.Status.BAD_REQUEST).entity(errorCollection).build();
//    }
//
//}
