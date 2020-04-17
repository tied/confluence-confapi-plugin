package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.model.ErrorCollection;
import de.aservo.atlassian.confapi.model.LicenseBean;
import de.aservo.atlassian.confapi.model.LicensesBean;
import de.aservo.atlassian.confapi.rest.LicenseResourceInterface;
import de.aservo.atlassian.confluence.confapi.filter.AdminOnlyResourceFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Collections;

import static com.atlassian.confluence.setup.ConfluenceBootstrapConstants.DEFAULT_LICENSE_REGISTRY_KEY;

/**
 * The type Licence resource.
 */
@Path(ConfAPI.LICENSE)
@Produces(MediaType.APPLICATION_JSON)
@ResourceFilters(AdminOnlyResourceFilter.class)
@Component
public class LicenceResource implements LicenseResourceInterface {

    private static final Logger log = LoggerFactory.getLogger(LicenceResource.class);

    private final LicenseHandler licenseHandler;

    /**
     * Instantiates a new Licence resource.
     *
     * @param licenseHandler the license handler
     */
    @Inject
    public LicenceResource(@ComponentImport LicenseHandler licenseHandler) {
        this.licenseHandler = licenseHandler;
    }

    /**
     * Gets license.
     *
     * @return the license
     */
    @GET
    @Operation(summary = "Retrieves license information",
            tags = { ConfAPI.LICENSES },
            description = "Upon successful request, returns a `LicensesBean` object containing license details",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LicensesBean.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            })
    @Override
    public Response getLicenses() {
        final ErrorCollection errorCollection = new ErrorCollection();
        try {
            SingleProductLicenseDetailsView conf = licenseHandler.getProductLicenseDetails(DEFAULT_LICENSE_REGISTRY_KEY);
            LicensesBean licensesBean = new LicensesBean();
            licensesBean.setLicenses(Collections.singletonList(new LicenseBean(conf)));
            return Response.ok(licensesBean).build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errorCollection.addErrorMessage(e.getMessage());
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(errorCollection).build();
    }

    /**
     * Add license.
     *
     * @param licenseKey the license key to add
     * @return
     */
    @PUT
    @Consumes({MediaType.TEXT_PLAIN})
    @Operation(summary = "Adds a new license",
            tags = { ConfAPI.LICENSES },
            description = "Existing license details are overwritten. Upon successful request, returns a `LicensesBean` object containing license details",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = LicensesBean.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ErrorCollection.class)))
            })
    @Override
    public Response setLicense(
            @QueryParam("clear") @Parameter(description="Clears license details before updating. This parameter is currently ignored.") @DefaultValue("false") boolean clear,
            String licenseKey) {
        final ErrorCollection errorCollection = new ErrorCollection();
        try {
            licenseHandler.addProductLicense(DEFAULT_LICENSE_REGISTRY_KEY, licenseKey);
            return getLicenses();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            errorCollection.addErrorMessage(e.getMessage());
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(errorCollection).build();
    }
}
