package de.aservo.confapi.confluence.rest;

import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.sun.jersey.spi.container.ResourceFilters;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.confluence.filter.SysAdminOnlyResourceFilter;
import de.aservo.confapi.confluence.model.BackupBean;
import de.aservo.confapi.confluence.model.BackupQueueBean;
import de.aservo.confapi.confluence.rest.api.BackupResource;
import de.aservo.confapi.confluence.service.api.BackupService;
import de.aservo.confapi.confluence.util.FilePartUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.UUID;

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
            final boolean forceSynchronous,
            @Nonnull final BackupBean backupBean) {

        if (isLongRunningTaskSupported() && !forceSynchronous) {
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
            final boolean forceSynchronous,
            @Nonnull final String key) {

        final BackupBean backupBean = new BackupBean();
        backupBean.setKey(key);
        backupBean.setBackupAttachments(true);
        backupBean.setBackupComments(true);

        return getExport(forceSynchronous, backupBean);
    }

    public Response doImportByFileUpload(
            @Nonnull final FilePart filePart) {

        final File file = FilePartUtil.createFile(filePart);

        if (isLongRunningTaskSupported()) {
            return Response.status(ACCEPTED)
                    .location(backupService.doImportAsynchronously(file))
                    .build();
        }

        backupService.doImportSynchronously(file);
        return Response.status(CREATED).build();
    }

    @Override
    public Response getQueue(
            @Nonnull final UUID uuid) {

        final BackupQueueBean backupQueueBean = backupService.getQueue(uuid);

        if (backupQueueBean == null) {
            return Response.status(NOT_FOUND).build();
        }

        // It's not possible to create a ResponseBuilder without a status,
        // so take "ok", which is returned if task has not completed yet
        final Response.ResponseBuilder responseBuilder = Response.ok().entity(backupQueueBean);

        if (backupQueueBean.getPercentageComplete() == 100) {
            // override responseBuilder status when task is completed
            responseBuilder.status(CREATED);

            if (backupQueueBean.getEntityUrl() != null) {
                // set location header if entity URI is set
                responseBuilder.location(backupQueueBean.getEntityUrl());
            }
        }

        return responseBuilder.build();
    }

}
