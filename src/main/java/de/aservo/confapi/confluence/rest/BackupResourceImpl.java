package de.aservo.confapi.confluence.rest;

import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.confluence.filter.SysAdminOnlyResourceFilter;
import de.aservo.confapi.confluence.model.BackupBean;
import de.aservo.confapi.confluence.model.BackupQueueBean;
import de.aservo.confapi.confluence.rest.api.BackupResource;
import de.aservo.confapi.confluence.service.api.BackupService;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static de.aservo.confapi.confluence.util.HttpUtil.isLongRunningTaskSupported;
import static javax.ws.rs.core.Response.Status.*;

@Path(ConfAPI.BACKUP)
@ResourceFilters(SysAdminOnlyResourceFilter.class)
@Component
public class BackupResourceImpl implements BackupResource {

    private final BackupService backupService;

    @Inject
    public BackupResourceImpl(
            final BackupService backupService) {

        this.backupService = backupService;
    }

    @Override
    public Response getExport(
            @Nonnull final BackupBean backupBean) {

        if (isLongRunningTaskSupported()) {
            return Response.status(ACCEPTED)
                    .location(backupService.getExportAsynchronously(backupBean))
                    .build();
        }

        return Response.status(CREATED)
                .location(backupService.getExportSynchronously(backupBean))
                .build();
    }

    @Override
    public Response getExportByKey(
            @Nonnull final String key) {

        final BackupBean backupBean = new BackupBean();
        backupBean.setKey(key);
        backupBean.setBackupAttachments(true);
        backupBean.setBackupComments(true);

        return getExport(backupBean);
    }

    @Override
    public Response getQueue(
            @Nonnull final String uuid) {

        final BackupQueueBean backupQueueBean = backupService.getQueue(uuid);

        if (backupQueueBean == null) {
            return Response.status(NOT_FOUND).build();
        }

        // It's not possible to create a ResponseBuilder without a status,
        // so take "ok", which is returned if task has not completed yet
        Response.ResponseBuilder responseBuilder = Response.ok().entity(backupQueueBean);

        if (backupQueueBean.getPercentageComplete() == 100) {
            // override responseBuilder status when task is completed
            responseBuilder = responseBuilder.status(CREATED);

            if (backupQueueBean.getEntityUri() != null) {
                // set location header if entity URI is set
                responseBuilder = responseBuilder.location(backupQueueBean.getEntityUri());
            }
        }

        return responseBuilder.build();
    }

}
