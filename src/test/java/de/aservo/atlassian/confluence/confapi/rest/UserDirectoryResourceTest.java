package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import de.aservo.atlassian.confapi.model.ErrorCollection;
import de.aservo.atlassian.confapi.model.UserDirectoryBean;
import de.aservo.atlassian.confapi.service.UserDirectoryService;
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
public class UserDirectoryResourceTest {

    @Mock
    private UserDirectoryService userDirectoryService;
    private UserDirectoryResource resource;

    @Before
    public void setup() {
        resource = new UserDirectoryResource(userDirectoryService);
    }

    @Test
    public void testGetDirectories() {
        Directory directory = createDirectory();
        UserDirectoryBean initialDirectoryBean = UserDirectoryBean.from(directory);

        doReturn(Collections.singletonList(initialDirectoryBean)).when(userDirectoryService).getDirectories();

        final Response response = resource.getDirectories();
        assertEquals(200, response.getStatus());
        @SuppressWarnings("unchecked") final List<UserDirectoryBean> userDirectoryBeans = (List<UserDirectoryBean>) response.getEntity();

        assertEquals(initialDirectoryBean, userDirectoryBeans.get(0));
    }

    @Test
    public void testGetDirectoriesWithError() {
        doThrow(new RuntimeException()).when(userDirectoryService).getDirectories();

        final Response response = resource.getDirectories();
        assertEquals(400, response.getStatus());

        assertNotNull(response.getEntity());
        assertEquals(ErrorCollection.class, response.getEntity().getClass());
    }

    @Test
    public void testAddDirectory() throws DirectoryCurrentlySynchronisingException {
        Directory directory = createDirectory();
        UserDirectoryBean directoryBean = UserDirectoryBean.from(directory);
        directoryBean.setCrowdUrl("http://localhost");
        directoryBean.setClientName("confluence-client");
        directoryBean.setAppPassword("test");

        doReturn(directoryBean).when(userDirectoryService).addDirectory(directoryBean, false);

        final Response response = resource.addDirectory(Boolean.FALSE, directoryBean);
        assertEquals(200, response.getStatus());
        final UserDirectoryBean userDirectoryBean = (UserDirectoryBean) response.getEntity();

        assertEquals(userDirectoryBean.getName(), directoryBean.getName());
    }

    @Test
    public void testAddDirectoryWithError() throws DirectoryCurrentlySynchronisingException {
        Directory directory = createDirectory();
        UserDirectoryBean directoryBean = UserDirectoryBean.from(directory);
        directoryBean.setCrowdUrl("http://localhost");
        directoryBean.setClientName("confluence-client");
        directoryBean.setAppPassword("test");
        doThrow(new DirectoryCurrentlySynchronisingException(1L)).when(userDirectoryService).addDirectory(directoryBean, false);

        final Response response = resource.addDirectory(Boolean.FALSE, directoryBean);
        assertEquals(400, response.getStatus());

        assertNotNull(response.getEntity());
        assertEquals(ErrorCollection.class, response.getEntity().getClass());
    }

    private Directory createDirectory() {
        return new DirectoryImpl("test", DirectoryType.CROWD, "test.class");
    }
}
