package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import de.aservo.atlassian.confapi.model.DirectoryBean;
import de.aservo.atlassian.confapi.model.ErrorCollection;
import de.aservo.atlassian.confapi.service.api.DirectoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class DirectoriesResourceTest {

    @Mock
    private DirectoryService directoryService;

    private DirectoriesResourceImpl resource;

    @Before
    public void setup() {
        resource = new DirectoriesResourceImpl(directoryService);
    }

    @Test
    public void testGetDirectories() {
        Directory directory = createDirectory();
        DirectoryBean initialDirectoryBean = DirectoryBean.from(directory);

        doReturn(Collections.singletonList(initialDirectoryBean)).when(directoryService).getDirectories();

        final Response response = resource.getDirectories();
        assertEquals(200, response.getStatus());
        @SuppressWarnings("unchecked") final List<DirectoryBean> DirectoryBeans = (List<DirectoryBean>) response.getEntity();

        assertEquals(initialDirectoryBean, DirectoryBeans.get(0));
    }

    @Test
    public void testGetDirectoriesWithError() {
        doThrow(new RuntimeException()).when(directoryService).getDirectories();

        final Response response = resource.getDirectories();
        assertEquals(400, response.getStatus());

        assertNotNull(response.getEntity());
        assertEquals(ErrorCollection.class, response.getEntity().getClass());
    }

    @Test
    public void testAddDirectory() throws DirectoryCurrentlySynchronisingException {
        Directory directory = createDirectory();
        DirectoryBean directoryBean = DirectoryBean.from(directory);
        directoryBean.setCrowdUrl("http://localhost");
        directoryBean.setClientName("confluence-client");
        directoryBean.setAppPassword("test");

        doReturn(directoryBean).when(directoryService).addDirectory(directoryBean, false);

        final Response response = resource.addDirectory(Boolean.FALSE, directoryBean);
        assertEquals(200, response.getStatus());
        final DirectoryBean DirectoryBean = (DirectoryBean) response.getEntity();

        assertEquals(DirectoryBean.getName(), directoryBean.getName());
    }

    @Test
    public void testAddDirectoryWithError() throws DirectoryCurrentlySynchronisingException {
        Directory directory = createDirectory();
        DirectoryBean directoryBean = DirectoryBean.from(directory);
        directoryBean.setCrowdUrl("http://localhost");
        directoryBean.setClientName("confluence-client");
        directoryBean.setAppPassword("test");
        doThrow(new DirectoryCurrentlySynchronisingException(1L)).when(directoryService).addDirectory(directoryBean, false);

        final Response response = resource.addDirectory(Boolean.FALSE, directoryBean);
        assertEquals(400, response.getStatus());

        assertNotNull(response.getEntity());
        assertEquals(ErrorCollection.class, response.getEntity().getClass());
    }

    private Directory createDirectory() {
        return new DirectoryImpl("test", DirectoryType.CROWD, "test.class");
    }
}
