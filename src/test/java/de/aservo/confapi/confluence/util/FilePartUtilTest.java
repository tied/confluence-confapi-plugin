package de.aservo.confapi.confluence.util;

import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.opensymphony.webwork.config.Configuration;
import de.aservo.confapi.commons.exception.InternalServerErrorException;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import static com.atlassian.confluence.setup.ConfluenceBootstrapConstants.TEMP_DIR_PROP;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class, FilePartUtil.class})
public class FilePartUtilTest {

    public static final String EXPORT_FILE_NAME = "confluence-export.zip";
    public static final String DOWNLOAD_PATH = "/path/to/download";

    @Test
    public void testCreateFile() throws IOException, NoSuchMethodException {
        final FilePart filePart = mock(FilePart.class);
        doReturn(EXPORT_FILE_NAME).when(filePart).getName();
        doReturn(mock(InputStream.class)).when(filePart).getInputStream();

        final File uploadDirectory = spy(new File(DOWNLOAD_PATH));
        doReturn(true).when(uploadDirectory).exists();
        doReturn(true).when(uploadDirectory).mkdirs();

        final File writtenFile = new File(uploadDirectory, EXPORT_FILE_NAME);

        final Method getUploadDirectoryMethod = FilePartUtil.class.getDeclaredMethod("getUploadDirectory");
        final Method writeToFileMethod = FilePartUtil.class.getDeclaredMethod("writeToFile", InputStream.class, File.class, String.class);

        PowerMock.mockStatic(FilePartUtil.class, getUploadDirectoryMethod, writeToFileMethod);
        expect(FilePartUtil.getUploadDirectory()).andReturn(uploadDirectory).anyTimes();
        expect(FilePartUtil.writeToFile(EasyMock.anyObject(), EasyMock.anyObject(), EasyMock.anyString())).andReturn(writtenFile).anyTimes();
        PowerMock.replay(FilePartUtil.class);

        assertNotNull(FilePartUtil.createFile(filePart));
    }

    @Test(expected = InternalServerErrorException.class)
    public void testCreateFileCannotCreateUploadDirectory() throws IOException, NoSuchMethodException {
        final FilePart filePart = mock(FilePart.class);
        doReturn(EXPORT_FILE_NAME).when(filePart).getName();
        doReturn(mock(InputStream.class)).when(filePart).getInputStream();

        final File uploadDirectory = spy(new File(DOWNLOAD_PATH));
        doReturn(false).when(uploadDirectory).exists();
        doReturn(false).when(uploadDirectory).mkdirs();

        final Method getUploadDirectoryMethod = FilePartUtil.class.getDeclaredMethod("getUploadDirectory");

        PowerMock.mockStatic(FilePartUtil.class, getUploadDirectoryMethod);
        expect(FilePartUtil.getUploadDirectory()).andReturn(uploadDirectory).anyTimes();
        PowerMock.replay(FilePartUtil.class);

        FilePartUtil.createFile(filePart);
    }

    @Test(expected = InternalServerErrorException.class)
    public void testCreateFileUploadPathIsFile() throws IOException, NoSuchMethodException {
        final FilePart filePart = mock(FilePart.class);
        doReturn(EXPORT_FILE_NAME).when(filePart).getName();
        doReturn(mock(InputStream.class)).when(filePart).getInputStream();

        final File uploadDirectory = spy(new File(DOWNLOAD_PATH));
        doReturn(true).when(uploadDirectory).exists();
        doReturn(true).when(uploadDirectory).isFile();

        final Method getUploadDirectoryMethod = FilePartUtil.class.getDeclaredMethod("getUploadDirectory");

        PowerMock.mockStatic(FilePartUtil.class, getUploadDirectoryMethod);
        expect(FilePartUtil.getUploadDirectory()).andReturn(uploadDirectory).anyTimes();
        PowerMock.replay(FilePartUtil.class);

        FilePartUtil.createFile(filePart);
    }

    @Test
    public void testGetUploadDirectory() {
        PowerMock.mockStatic(Configuration.class);
        expect(Configuration.getString(TEMP_DIR_PROP)).andReturn(DOWNLOAD_PATH).anyTimes();
        PowerMock.replay(Configuration.class);

        final File uploadDirectory = FilePartUtil.getUploadDirectory();
        assertNotNull(uploadDirectory);
        //assertEquals(DOWNLOAD_PATH, uploadDirectory.getAbsolutePath());
    }

}
