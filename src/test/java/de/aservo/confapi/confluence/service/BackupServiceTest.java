package de.aservo.confapi.confluence.service;

import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.event.events.cluster.ClusterReindexRequiredEvent;
import com.atlassian.confluence.importexport.ExportContext;
import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.importexport.actions.ExportSpaceLongRunningTask;
import com.atlassian.confluence.importexport.actions.ImportLongRunningTask;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.event.api.EventPublisher;
import de.aservo.confapi.commons.exception.BadRequestException;
import de.aservo.confapi.commons.exception.InternalServerErrorException;
import de.aservo.confapi.confluence.model.BackupBean;
import de.aservo.confapi.confluence.model.BackupQueueBean;
import de.aservo.confapi.confluence.util.HttpUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.net.URI;
import java.util.Optional;

import static de.aservo.confapi.commons.constants.ConfAPI.BACKUP;
import static de.aservo.confapi.commons.constants.ConfAPI.BACKUP_QUEUE;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpUtil.class)
public class BackupServiceTest {

    private static final String BASE_URL = "http://localhost:1990/confluence";
    private static final String SPACE_KEY = "space";

    private static final String EXPORT_ZIP_PATH = "space-export.zip";
    private static final URI EXPORT_ZIP_URI = URI.create(BASE_URL + "/" + EXPORT_ZIP_PATH);

    private static final String BACKUP_QUEUE_UUID = "a0b1cdef-0a12-3bcd-45e6-0a1bcd2345ef";
    private static final URI BACKUP_QUEUE_URI = URI.create(BASE_URL + "/rest/confapi/1/backup/queue/" + BACKUP_QUEUE_UUID);

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private ImportExportManager importExportManager;

    @Mock
    private IndexManager indexManager;

    @Mock
    private LongRunningTaskManager longRunningTaskManager;

    @Mock
    private PermissionManager permissionManager;

    @Mock
    private SpaceManager spaceManager;

    @Mock
    private SpaceService spaceService;

    private BackupServiceImpl backupService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        backupService = new BackupServiceImpl(
                eventPublisher,
                importExportManager,
                indexManager,
                longRunningTaskManager,
                permissionManager,
                spaceManager,
                spaceService
        );
    }

    // export methods

    @Test
    public void testGetExportSynchronously() {
        final BackupServiceImpl spy = spy(backupService);
        final URI downloadUri = UriBuilder.fromUri(BASE_URL).path(EXPORT_ZIP_PATH).build();

        final Space space = Space.builder().key(SPACE_KEY).build();
        doReturn(space).when(spy).getSpace(anyString());
        final ExportContext exportContext = mock(ExportContext.class);
        doReturn(exportContext).when(spy).createExportContext(any(BackupBean.class));
        final ExportSpaceLongRunningTask task = mock(ExportSpaceLongRunningTask.class);
        doReturn(EXPORT_ZIP_PATH).when(task).getDownloadPath();
        doReturn(task).when(spy).createExportSpaceLongRunningTask(any(ExportContext.class));

        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.createUri(EXPORT_ZIP_PATH)).andReturn(downloadUri);
        PowerMock.replay(HttpUtil.class);

        final BackupBean backupBean = new BackupBean();
        backupBean.setKey(SPACE_KEY);
        assertEquals(downloadUri, spy.getExportSynchronously(backupBean));
    }

    @Test
    public void testGetExportAsynchronously() {
        final BackupServiceImpl spy = spy(backupService);

        final Space space = Space.builder().key(SPACE_KEY).build();
        doReturn(space).when(spy).getSpace(anyString());
        final ExportContext exportContext = mock(ExportContext.class);
        doReturn(exportContext).when(spy).createExportContext(any(BackupBean.class));
        final ExportSpaceLongRunningTask task = mock(ExportSpaceLongRunningTask.class);
        doReturn(task).when(spy).createExportSpaceLongRunningTask(any(ExportContext.class));

        final ConfluenceUser user = mock(ConfluenceUser.class);
        final LongRunningTaskId longRunningTaskId = LongRunningTaskId.valueOf(BACKUP_QUEUE_UUID);
        doReturn(longRunningTaskId).when(longRunningTaskManager).startLongRunningTask(user, task);

        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.getUser()).andReturn(user);
        expect(HttpUtil.isLongRunningTaskSupported()).andReturn(Boolean.TRUE);
        expect(HttpUtil.createRestUri(BACKUP, BACKUP_QUEUE, BACKUP_QUEUE_UUID)).andReturn(BACKUP_QUEUE_URI);
        PowerMock.replay(HttpUtil.class);

        final BackupBean backupBean = new BackupBean();
        backupBean.setKey(SPACE_KEY);
        assertEquals(BACKUP_QUEUE_URI, spy.getExportAsynchronously(backupBean));
    }

    // import methods

    @Test
    public void testDoImportSynchronously() {
        final BackupServiceImpl spy = spy(backupService);

        final File file = mock(File.class);
        final ImportContext importContext = mock(ImportContext.class);
        doReturn(importContext).when(spy).createImportContext(file);
        final ImportLongRunningTask task = mock(ImportLongRunningTask.class);
        doReturn(task).when(spy).createImportLongRunningTask(importContext);

        spy.doImportSynchronously(file);

        verify(task).run();
    }

    @Test
    public void testDoImportAsynchronously() {
        final BackupServiceImpl spy = spy(backupService);

        final File file = mock(File.class);
        final ImportContext importContext = mock(ImportContext.class);
        doReturn(importContext).when(spy).createImportContext(file);
        final ImportLongRunningTask task = mock(ImportLongRunningTask.class);
        doReturn(task).when(spy).createImportLongRunningTask(importContext);

        final ConfluenceUser user = mock(ConfluenceUser.class);
        final LongRunningTaskId longRunningTaskId = LongRunningTaskId.valueOf(BACKUP_QUEUE_UUID);
        doReturn(longRunningTaskId).when(longRunningTaskManager).startLongRunningTask(user, task);

        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.getUser()).andReturn(user);
        expect(HttpUtil.createRestUri(BACKUP, BACKUP_QUEUE, BACKUP_QUEUE_UUID)).andReturn(BACKUP_QUEUE_URI);
        PowerMock.replay(HttpUtil.class);

        assertEquals(BACKUP_QUEUE_URI, spy.doImportAsynchronously(file));
    }

    // queue methods

    @Test
    public void testGetQueueExportIncomplete() {
        final LongRunningTaskId longRunningTaskId = LongRunningTaskId.valueOf(BACKUP_QUEUE_UUID);
        final ConfluenceUser user = mock(ConfluenceUser.class);

        final ExportSpaceLongRunningTask task = mock(ExportSpaceLongRunningTask.class);
        doReturn(task).when(longRunningTaskManager).getLongRunningTask(user, longRunningTaskId);

        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.getUser()).andReturn(user);
        PowerMock.replay(HttpUtil.class);

        final BackupQueueBean backupQueueBean = backupService.getQueue(BACKUP_QUEUE_UUID);
        assertNotNull(backupQueueBean);
        assertNull(backupQueueBean.getEntityUri());
    }

    @Test
    public void testGetQueueExportCompleteAndSuccessful() {
        final LongRunningTaskId longRunningTaskId = LongRunningTaskId.valueOf(BACKUP_QUEUE_UUID);
        final ConfluenceUser user = mock(ConfluenceUser.class);

        final ExportSpaceLongRunningTask task = mock(ExportSpaceLongRunningTask.class);
        doReturn(true).when(task).isComplete();
        doReturn(true).when(task).isSuccessful();
        doReturn(100).when(task).getPercentageComplete();
        doReturn(2000L).when(task).getElapsedTime();
        doReturn(EXPORT_ZIP_PATH).when(task).getDownloadPath();
        doReturn(task).when(longRunningTaskManager).getLongRunningTask(user, longRunningTaskId);

        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.getUser()).andReturn(user);
        expect(HttpUtil.createUri(task.getDownloadPath())).andReturn(EXPORT_ZIP_URI);
        PowerMock.replay(HttpUtil.class);

        final BackupQueueBean backupQueueBean = backupService.getQueue(BACKUP_QUEUE_UUID);
        assertNotNull(backupQueueBean);
        assertNotNull(backupQueueBean.getEntityUri());
    }

    @Test
    public void testGetQueueImportIncomplete() {
        final LongRunningTaskId longRunningTaskId = LongRunningTaskId.valueOf(BACKUP_QUEUE_UUID);
        final ConfluenceUser user = mock(ConfluenceUser.class);

        final ImportLongRunningTask task = mock(ImportLongRunningTask.class);
        doReturn(task).when(longRunningTaskManager).getLongRunningTask(user, longRunningTaskId);

        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.getUser()).andReturn(user);
        PowerMock.replay(HttpUtil.class);

        final BackupQueueBean backupQueueBean = backupService.getQueue(BACKUP_QUEUE_UUID);
        assertNotNull(backupQueueBean);
        assertNull(backupQueueBean.getEntityUri());
    }

    @Test
    public void testGetQueueImportCompleteAndSuccessful() {
        final LongRunningTaskId longRunningTaskId = LongRunningTaskId.valueOf(BACKUP_QUEUE_UUID);
        final ConfluenceUser user = mock(ConfluenceUser.class);

        final ImportLongRunningTask task = mock(ImportLongRunningTask.class);
        doReturn(true).when(task).isComplete();
        doReturn(true).when(task).isSuccessful();
        doReturn(100).when(task).getPercentageComplete();
        doReturn(2000L).when(task).getElapsedTime();
        doReturn(task).when(longRunningTaskManager).getLongRunningTask(user, longRunningTaskId);

        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.getUser()).andReturn(user);
        PowerMock.replay(HttpUtil.class);

        final BackupQueueBean backupQueueBean = backupService.getQueue(BACKUP_QUEUE_UUID);
        assertNotNull(backupQueueBean);
        assertNull(backupQueueBean.getEntityUri());

        verify(eventPublisher).publish(any(ClusterReindexRequiredEvent.class));
        verify(indexManager).reIndex();
    }

    @Test
    public void testGetQueueTaskNull() {
        final ConfluenceUser user = mock(ConfluenceUser.class);

        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.getUser()).andReturn(user);
        PowerMock.replay(HttpUtil.class);

        assertNull(backupService.getQueue(BACKUP_QUEUE_UUID));
    }

    @Test(expected = BadRequestException.class)
    public void testGetQueueNotExportOrImportTask() {
        final LongRunningTaskId longRunningTaskId = LongRunningTaskId.valueOf(BACKUP_QUEUE_UUID);
        final ConfluenceUser user = mock(ConfluenceUser.class);

        final LongRunningTask task = mock(LongRunningTask.class);
        doReturn(task).when(longRunningTaskManager).getLongRunningTask(user, longRunningTaskId);

        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.getUser()).andReturn(user);
        PowerMock.replay(HttpUtil.class);

        backupService.getQueue(BACKUP_QUEUE_UUID);
    }

    @Test(expected = InternalServerErrorException.class)
    public void testGetQueueCompleteButUnsuccessful() {
        final LongRunningTaskId longRunningTaskId = LongRunningTaskId.valueOf(BACKUP_QUEUE_UUID);
        final ConfluenceUser user = mock(ConfluenceUser.class);

        final ExportSpaceLongRunningTask task = mock(ExportSpaceLongRunningTask.class);
        doReturn(true).when(task).isComplete();
        doReturn(task).when(longRunningTaskManager).getLongRunningTask(user, longRunningTaskId);

        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.getUser()).andReturn(user);
        PowerMock.replay(HttpUtil.class);

        backupService.getQueue(BACKUP_QUEUE_UUID);
    }

    // export helper methods

    @Test
    public void testGetSpace() {
        final Space space = Space.builder().key(SPACE_KEY).build();

        final SpaceService.SpaceFinder spaceFinder = mock(SpaceService.SpaceFinder.class);
        doReturn(spaceFinder).when(spaceFinder).withKeys(SPACE_KEY);
        doReturn(Optional.of(space)).when(spaceFinder).fetch();
        doReturn(spaceFinder).when(spaceService).find();

        assertNotNull(backupService.getSpace(SPACE_KEY));
    }

    @Test(expected = BadRequestException.class)
    public void testGetSpaceKeyIsNull() {
        backupService.getSpace(null);
    }

    @Test(expected = BadRequestException.class)
    public void testGetSpaceKeyIsEmpty() {
        backupService.getSpace("");
    }

    @Test(expected = BadRequestException.class)
    public void testGetSpaceNotExists() {
        final SpaceService.SpaceFinder spaceFinder = mock(SpaceService.SpaceFinder.class);
        doReturn(spaceFinder).when(spaceFinder).withKeys(SPACE_KEY);
        doReturn(Optional.empty()).when(spaceFinder).fetch();
        doReturn(spaceFinder).when(spaceService).find();

        backupService.getSpace(SPACE_KEY);
    }

    @Test
    public void testCreateExportContext() {
        final BackupBean backupBean = new BackupBean();
        backupBean.setKey(SPACE_KEY);
        backupBean.setBackupAttachments(true);
        backupBean.setBackupComments(true);

        final ConfluenceUser user = mock(ConfluenceUser.class);

        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.getUser()).andReturn(user);
        PowerMock.replay(HttpUtil.class);

        final ExportContext exportContext = backupService.createExportContext(backupBean);

        assertEquals(user, exportContext.getUser());
        assertEquals(ExportScope.SPACE, exportContext.getExportScope());
        assertEquals(backupBean.getKey(), exportContext.getSpaceKeyOfSpaceExport());
        assertEquals(backupBean.getType(), exportContext.getType());
        assertEquals(backupBean.getBackupAttachments(), exportContext.isExportAttachments());
        assertEquals(backupBean.getBackupComments(), exportContext.isExportComments());
    }

    // import helper methods

    @Test
    public void testCreateImportContext() {
        final String filePath = "/path/to/File";
        final File file = mock(File.class);
        doReturn(filePath).when(file).getAbsolutePath();

        final ConfluenceUser user = mock(ConfluenceUser.class);

        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.getUser()).andReturn(user);
        PowerMock.replay(HttpUtil.class);

        final ImportContext importContext = backupService.createImportContext(file);

        assertEquals(user, importContext.getUser());
        assertEquals(filePath, importContext.getWorkingFile());
    }

}
