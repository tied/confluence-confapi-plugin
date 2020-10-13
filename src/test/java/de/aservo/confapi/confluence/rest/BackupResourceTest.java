package de.aservo.confapi.confluence.rest;

import com.atlassian.plugins.rest.common.multipart.FilePart;
import de.aservo.confapi.confluence.model.BackupBean;
import de.aservo.confapi.confluence.model.BackupQueueBean;
import de.aservo.confapi.confluence.service.api.BackupService;
import de.aservo.confapi.confluence.util.FilePartUtil;
import de.aservo.confapi.confluence.util.HttpUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URI;
import java.util.UUID;

import static javax.ws.rs.core.Response.Status.*;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpUtil.class, FilePartUtil.class})
public class BackupResourceTest {

    private static final URI BACKUP_QUEUE_URI = URI.create("http://localhost:1990/confluence/rest/confapi/1/backup/queue/123");
    private static final UUID BACKUP_QUEUE_UUID = UUID.fromString("a0b1cdef-0a12-3bcd-45e6-0a1bcd2345ef");
    private static final String RESPONSE_METADATA_LOCATION = "Location";

    @Mock
    private BackupService backupService;

    private BackupResourceImpl backupResource;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        backupResource = new BackupResourceImpl(backupService);
    }

    @Test
    public void testGetExportAsynchronously() {
        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.isLongRunningTaskSupported()).andReturn(Boolean.TRUE);
        PowerMock.replay(HttpUtil.class);

        doReturn(BACKUP_QUEUE_URI).when(backupService).getExportAsynchronously(any(BackupBean.class));

        final Response response = backupResource.getExportByKey(false, "space");
        assertEquals(ACCEPTED.getStatusCode(), response.getStatus());
        assertNotNull(response.getMetadata().getFirst(RESPONSE_METADATA_LOCATION));
    }

    @Test
    public void testGetExportSynchronously() {
        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.isLongRunningTaskSupported()).andReturn(Boolean.FALSE);
        PowerMock.replay(HttpUtil.class);

        doReturn(BACKUP_QUEUE_URI).when(backupService).getExportSynchronously(any(BackupBean.class));

        final Response response = backupResource.getExportByKey(false, "space");
        assertEquals(CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getMetadata().getFirst(RESPONSE_METADATA_LOCATION));
    }

    @Test
    public void testGetExportSynchronouslyForced() {
        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.isLongRunningTaskSupported()).andReturn(Boolean.TRUE);
        PowerMock.replay(HttpUtil.class);

        doReturn(BACKUP_QUEUE_URI).when(backupService).getExportSynchronously(any(BackupBean.class));

        final Response response = backupResource.getExportByKey(true, "space");
        assertEquals(CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getMetadata().getFirst(RESPONSE_METADATA_LOCATION));
    }

    @Test
    public void testDoImportByUploadAsynchronously() {
        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.isLongRunningTaskSupported()).andReturn(Boolean.TRUE);
        PowerMock.replay(HttpUtil.class);

        final FilePart filePart = mock(FilePart.class);
        final File file = mock(File.class);

        PowerMock.mockStatic(FilePartUtil.class);
        expect(FilePartUtil.createFile(filePart)).andReturn(file);
        PowerMock.replay(FilePartUtil.class);

        doReturn(BACKUP_QUEUE_URI).when(backupService).doImportAsynchronously(file);

        final Response response = backupResource.doImportByFileUpload(filePart);
        assertEquals(ACCEPTED.getStatusCode(), response.getStatus());
        assertNotNull(response.getMetadata().getFirst(RESPONSE_METADATA_LOCATION));
    }

    @Test
    public void testDoImportByUploadSynchronously() {
        PowerMock.mockStatic(HttpUtil.class);
        expect(HttpUtil.isLongRunningTaskSupported()).andReturn(Boolean.FALSE);
        PowerMock.replay(HttpUtil.class);

        final FilePart filePart = mock(FilePart.class);
        final File file = mock(File.class);

        PowerMock.mockStatic(FilePartUtil.class);
        expect(FilePartUtil.createFile(filePart)).andReturn(file);
        PowerMock.replay(FilePartUtil.class);

        final Response response = backupResource.doImportByFileUpload(filePart);
        assertEquals(CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetQueueIncomplete() {
        final BackupQueueBean backupQueueBean = new BackupQueueBean();
        backupQueueBean.setPercentageComplete(50);
        doReturn(backupQueueBean).when(backupService).getQueue(any());

        final Response response = backupResource.getQueue(BACKUP_QUEUE_UUID);
        assertEquals(OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetQueueComplete() {
        final BackupQueueBean backupQueueBean = new BackupQueueBean();
        backupQueueBean.setPercentageComplete(100);
        backupQueueBean.setEntityUrl(BACKUP_QUEUE_URI);
        doReturn(backupQueueBean).when(backupService).getQueue(any());

        final Response response = backupResource.getQueue(BACKUP_QUEUE_UUID);
        assertEquals(CREATED.getStatusCode(), response.getStatus());
        assertNotNull(response.getMetadata().getFirst(RESPONSE_METADATA_LOCATION));
    }

    @Test
    public void testGetQueueCompleteWithoutLocation() {
        final BackupQueueBean backupQueueBean = new BackupQueueBean();
        backupQueueBean.setPercentageComplete(100);
        doReturn(backupQueueBean).when(backupService).getQueue(any());

        final Response response = backupResource.getQueue(BACKUP_QUEUE_UUID);
        assertEquals(CREATED.getStatusCode(), response.getStatus());
        assertNull(response.getMetadata().getFirst(RESPONSE_METADATA_LOCATION));
    }

    @Test
    public void testGetQueueUuidNotFound() {
        final Response response = backupResource.getQueue(BACKUP_QUEUE_UUID);
        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
    }

}
