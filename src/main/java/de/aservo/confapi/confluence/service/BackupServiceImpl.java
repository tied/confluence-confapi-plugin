package de.aservo.confapi.confluence.service;

import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.event.events.cluster.ClusterReindexRequiredEvent;
import com.atlassian.confluence.importexport.DefaultExportContext;
import com.atlassian.confluence.importexport.DefaultImportContext;
import com.atlassian.confluence.importexport.ExportContext;
import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.importexport.actions.ExportSpaceLongRunningTask;
import com.atlassian.confluence.importexport.actions.ImportLongRunningTask;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.security.DownloadGateKeeper;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.spring.container.ContainerManager;
import de.aservo.confapi.commons.exception.BadRequestException;
import de.aservo.confapi.commons.exception.InternalServerErrorException;
import de.aservo.confapi.confluence.model.BackupBean;
import de.aservo.confapi.confluence.model.BackupQueueBean;
import de.aservo.confapi.confluence.service.api.BackupService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.Optional;

import static de.aservo.confapi.commons.constants.ConfAPI.BACKUP;
import static de.aservo.confapi.commons.constants.ConfAPI.BACKUP_QUEUE;
import static de.aservo.confapi.confluence.util.HttpUtil.*;

@Component
@ExportAsService(BackupService.class)
public class BackupServiceImpl implements BackupService {

    private static final Logger log = LoggerFactory.getLogger(BackupServiceImpl.class);

    private static final String COMPONENT_GATE_KEEPER = "gateKeeper";

    private final EventPublisher eventPublisher;
    private final ImportExportManager importExportManager;
    private final IndexManager indexManager;
    private final LongRunningTaskManager longRunningTaskManager;
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final SpaceService spaceService;

    @Inject
    public BackupServiceImpl(
            @ComponentImport final EventPublisher eventPublisher,
            @ComponentImport final ImportExportManager importExportManager,
            @ComponentImport final IndexManager indexManager,
            @ComponentImport final LongRunningTaskManager longRunningTaskManager,
            @ComponentImport final PermissionManager permissionManager,
            @ComponentImport final SpaceManager spaceManager,
            @ComponentImport final SpaceService spaceService) {

        this.eventPublisher = eventPublisher;
        this.importExportManager = importExportManager;
        this.indexManager = indexManager;
        this.longRunningTaskManager = longRunningTaskManager;
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.spaceService = spaceService;
    }

    @Override
    public URI getExportSynchronously(
            final BackupBean backupBean) {

        final Space space = getSpace(backupBean.getKey());
        final ExportContext exportContext = createExportContext(backupBean);
        final ExportSpaceLongRunningTask task = createExportSpaceLongRunningTask(exportContext);

        log.info("Starting synchronous export of space '{}'", space.getKey());
        task.run();

        return createUri(task.getDownloadPath());
    }

    @Override
    public URI getExportAsynchronously(
            final BackupBean backupBean) {

        final Space space = getSpace(backupBean.getKey());
        final ExportContext exportContext = createExportContext(backupBean);
        final ExportSpaceLongRunningTask task = createExportSpaceLongRunningTask(exportContext);

        final LongRunningTaskId taskId = longRunningTaskManager.startLongRunningTask(getUser(), task);
        final String taskUuid = taskId.toString();
        log.info("Started asynchronous task '{}' for export of space '{}'", taskUuid, space.getKey());

        return createRestUri(BACKUP, BACKUP_QUEUE, taskUuid);
    }

    @Override
    public void doImportSynchronously(
            final File file) {

        final ImportContext importContext = createImportContext(file);
        final ImportLongRunningTask task = createImportLongRunningTask(importContext);

        log.info("Starting synchronous import; long-running tasks not enabled");
        // run with call runInternal which executes reindex as well
        task.run();
    }

    @Override
    public URI doImportAsynchronously(
            final File file) {

        final ImportContext importContext = createImportContext(file);
        final ImportLongRunningTask task = createImportLongRunningTask(importContext);

        final LongRunningTaskId taskId = longRunningTaskManager.startLongRunningTask(getUser(), task);
        final String taskUuid = taskId.toString();
        log.info("Started asynchronous task {} for import", taskUuid);

        return createRestUri(BACKUP, BACKUP_QUEUE, taskUuid);
    }

    @Override
    public BackupQueueBean getQueue(
            final String uuid) {

        final LongRunningTaskId taskId = LongRunningTaskId.valueOf(uuid);
        final LongRunningTask task = longRunningTaskManager.getLongRunningTask(getUser(), taskId);
        log.info("Trying to get queue information for task with uuid '{}'", uuid);

        if (task == null) {
            return null;
        }

        final BackupQueueBean backupQueueBean = new BackupQueueBean();
        backupQueueBean.setPercentageComplete(task.getPercentageComplete());
        backupQueueBean.setElapsedTimeInMillis(task.getElapsedTime());
        backupQueueBean.setEstimatedTimeRemainingInMillis(task.getEstimatedTimeRemaining());

        if (!(task instanceof ExportSpaceLongRunningTask || task instanceof ImportLongRunningTask)) {
            throw new BadRequestException(String.format(
                    "Given task uuid '%s' does not belong to an space export or import task", uuid));
        }

        if (task.isComplete()) {
            if (!task.isSuccessful()) {
                throw new InternalServerErrorException(String.format(
                        "Given task with uuid '%s' completed unsuccessfully", uuid));
            }

            if (task instanceof ExportSpaceLongRunningTask) {
                final ExportSpaceLongRunningTask exportTask = (ExportSpaceLongRunningTask) task;
                backupQueueBean.setEntityUri(createUri(exportTask.getDownloadPath()));
            } else { // ImportLongRunningTask
                // reason 'global import' coming from Confluence sources
                eventPublisher.publish(new ClusterReindexRequiredEvent("global import"));
                indexManager.reIndex();
            }
        }

        return backupQueueBean;
    }

    // export helper methods

    Space getSpace(
            @Nullable final String spaceKey) {

        log.info("Trying to find space with key '{}'", spaceKey);
        if (StringUtils.isBlank(spaceKey)) {
            final String message = "No space key given for export";
            log.error(message);
            throw new BadRequestException(message);
        }

        final Optional<Space> optionalSpace = spaceService.find().withKeys(spaceKey).fetch();

        if (!optionalSpace.isPresent()) {
            final String message = String.format("Space with key %s does not exist", spaceKey);
            log.error(message);
            throw new BadRequestException(message);
        }

        return optionalSpace.get();
    }

    ExportContext createExportContext(
            @Nonnull final BackupBean backupBean) {

        final DefaultExportContext exportContext = new DefaultExportContext();

        exportContext.setUser(getUser());
        exportContext.setExportScope(ExportScope.SPACE);
        exportContext.setSpaceKey(backupBean.getKey());
        exportContext.setType(backupBean.getType());
        exportContext.setExportAttachments(backupBean.getBackupAttachments());
        exportContext.setExportComments(backupBean.getBackupComments());

        return exportContext;
    }

    ExportSpaceLongRunningTask createExportSpaceLongRunningTask(
            final ExportContext exportContext) {

        final DownloadGateKeeper gateKeeper = createDownloadGateKeeper();

        return new ExportSpaceLongRunningTask(
                getUser(),
                getServletRequest().getContextPath(),
                exportContext,
                Collections.emptySet(),
                Collections.emptySet(),
                gateKeeper,
                importExportManager,
                permissionManager,
                spaceManager,
                exportContext.getSpaceKeyOfSpaceExport(),
                exportContext.getType(),
                null);
    }

    private DownloadGateKeeper createDownloadGateKeeper() {
        return (DownloadGateKeeper) ContainerManager.getInstance()
                .getContainerContext().getComponent(COMPONENT_GATE_KEEPER);
        // configure gateKeeper to make export only accessible by creating user (soon)
    }

    // import helper methods

    ImportContext createImportContext(
            @Nonnull final File file) {

        return new DefaultImportContext(file.getAbsolutePath(), getUser());
    }

    ImportLongRunningTask createImportLongRunningTask(
            @Nonnull final ImportContext importContext) {

        return new ImportLongRunningTask(
                eventPublisher,
                indexManager,
                importExportManager,
                importContext
        );
    }

}
