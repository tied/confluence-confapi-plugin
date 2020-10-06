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
import de.aservo.confapi.commons.exception.NotFoundException;
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
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static de.aservo.confapi.commons.constants.ConfAPI.BACKUP;
import static de.aservo.confapi.commons.constants.ConfAPI.BACKUP_QUEUE;
import static de.aservo.confapi.confluence.util.HttpUtil.*;

@Component
@ExportAsService(BackupService.class)
public class BackupServiceImpl implements BackupService {

    private static final Logger log = LoggerFactory.getLogger(BackupServiceImpl.class);

    private static final String COMPONENT_GATE_KEEPER = "gateKeeper";

    public static final String FILE_ENTITIES_XML = "entities.xml";
    public static final String FILE_EXPORT_DESCRIPTOR_PROPERTIES = "exportDescriptor.properties";

    public static final String PROPERTY_EXPORT_TYPE = "exportType";
    public static final String PROPERTY_EXPORT_TYPE_SPACE = "space";
    public static final String PROPERTY_SPACE_KEY = "spaceKey";

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

        validateImportFile(file);

        final ImportContext importContext = createImportContext(file);
        final ImportLongRunningTask task = createImportLongRunningTask(importContext);

        log.info("Starting synchronous import; long-running tasks not enabled");
        // run with call runInternal which executes reindex as well
        task.run();
    }

    @Override
    public URI doImportAsynchronously(
            final File file) {

        validateImportFile(file);

        final ImportContext importContext = createImportContext(file);
        final ImportLongRunningTask task = createImportLongRunningTask(importContext);

        final LongRunningTaskId taskId = longRunningTaskManager.startLongRunningTask(getUser(), task);
        final String taskUuid = taskId.toString();
        log.info("Started asynchronous task {} for import", taskUuid);

        return createRestUri(BACKUP, BACKUP_QUEUE, taskUuid);
    }

    @Override
    public BackupQueueBean getQueue(
            final UUID uuid) {

        final LongRunningTaskId taskId = LongRunningTaskId.valueOf(uuid.toString());
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
                backupQueueBean.setEntityUrl(createUri(exportTask.getDownloadPath()));
            } else { // ImportLongRunningTask
                // reason 'global import' coming from Confluence sources
                eventPublisher.publish(new ClusterReindexRequiredEvent("global import"));
                indexManager.reIndex();
            }
        }

        return backupQueueBean;
    }

    // export helper methods

    @Nonnull
    Space getSpace(
            @Nullable final String spaceKey) {

        if (StringUtils.isBlank(spaceKey)) {
            throw new BadRequestException("No space key given for export");
        }

        final Space space = findSpace(spaceKey);

        if (space == null) {
            throw new NotFoundException(String.format("Space with key '%s' does not exist", spaceKey));
        }

        return space;
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

    void validateImportFile(
            final File file) {

        final Properties properties = getExportFileProperties(file);

        final String exportType = properties.getProperty(PROPERTY_EXPORT_TYPE);
        final String spaceKey = properties.getProperty(PROPERTY_SPACE_KEY);

        if (StringUtils.isBlank(exportType) || !exportType.equalsIgnoreCase(PROPERTY_EXPORT_TYPE_SPACE)) {
            throw new BadRequestException("Given export file is not a space export");
        }

        if (StringUtils.isBlank(spaceKey)) {
            throw new BadRequestException("Given export file does not contain a space key");
        }

        if (findSpace(spaceKey) != null) {
            throw new BadRequestException(String.format("The export file's space key '%s' already exists", spaceKey));
        }
    }

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

    // helper methods

    @Nullable
    protected Space findSpace(
            @Nonnull final String spaceKey) {

        log.info("Trying to find space with key '{}'", spaceKey);

        return spaceService.find()
                .withKeys(spaceKey)
                .fetch()
                .orElse(null);
    }

    protected Properties getExportFileProperties(
            final File file) {

        try (final ZipFile zipFile = new ZipFile(file)) {
            return getExportZipFileProperties(zipFile);
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    protected Properties getExportZipFileProperties(
            final ZipFile zipFile) {

        final Properties properties = new Properties();

        boolean hasEntitiesXml = false;
        boolean hasExportDescriptorProperties = false;

        try {
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();

                if (entry.getName().equals(FILE_EXPORT_DESCRIPTOR_PROPERTIES)) {
                    hasExportDescriptorProperties = true;
                    properties.load(zipFile.getInputStream(entry));
                } else if (entry.getName().equals(FILE_ENTITIES_XML)) {
                    hasEntitiesXml = true;
                }
            }
        } catch (IOException e) {
            throw new InternalServerErrorException(e);
        }

        if (!hasEntitiesXml || !hasExportDescriptorProperties) {
            throw new BadRequestException(String.format(
                    "Given file '%s' does not seem to be a Confluence export", zipFile.getName()));
        }

        return properties;
    }

}
