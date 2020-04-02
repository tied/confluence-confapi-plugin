package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.atlassian.confapi.constants.ConfAPI;
import de.aservo.atlassian.confapi.model.SettingsBean;
import de.aservo.atlassian.confluence.confapi.filter.AdminOnlyResourceFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Resource to set general configuration.
 */
@Path(ConfAPI.SETTINGS)
@Produces({MediaType.APPLICATION_JSON})
@ResourceFilters(AdminOnlyResourceFilter.class)
@Component
public class SettingsResource {

    @ComponentImport
    private final SettingsManager settingsManager;

    @Inject
    public SettingsResource(
            final SettingsManager settingsManager) {

        this.settingsManager = settingsManager;
    }

    @GET
    @Operation(
            tags = { ConfAPI.SETTINGS },
            summary = "Get the application settings",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = SettingsBean.class))),
            }
    )
    public Response getSettings() {
        final Settings settings = settingsManager.getGlobalSettings();

        final SettingsBean settingsBean = new SettingsBean();
        settingsBean.setBaseUrl(settings.getBaseUrl());
        settingsBean.setTitle(settings.getSiteTitle());

        return Response.ok(settingsBean).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            tags = { ConfAPI.SETTINGS },
            summary = "Set the application settings",
            responses = {
                    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = SettingsBean.class))),
            }
    )
    public Response setSettings(
            @NotNull final SettingsBean bean) {

        final Settings settings = settingsManager.getGlobalSettings();

        if (StringUtils.isNotBlank(bean.getBaseUrl())) {
            settings.setBaseUrl(bean.getBaseUrl());
        }

        if (StringUtils.isNotBlank(bean.getTitle())) {
            settings.setSiteTitle(bean.getTitle());
        }

        settingsManager.updateGlobalSettings(settings);

        return getSettings();
    }

}
