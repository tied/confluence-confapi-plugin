package de.aservo.confapi.confluence.service;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import de.aservo.confapi.commons.exception.BadRequestException;
import de.aservo.confapi.commons.exception.NotFoundException;
import de.aservo.confapi.commons.exception.ServiceUnavailableException;
import de.aservo.confapi.commons.model.AbstractDirectoryBean;
import de.aservo.confapi.commons.model.DirectoriesBean;
import de.aservo.confapi.commons.model.DirectoryCrowdBean;
import de.aservo.confapi.commons.model.DirectoryLdapBean;
import de.aservo.confapi.confluence.model.util.DirectoryBeanUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.atlassian.crowd.directory.RemoteCrowdDirectory.*;
import static com.atlassian.crowd.directory.SynchronisableDirectoryProperties.*;
import static com.atlassian.crowd.directory.SynchronisableDirectoryProperties.SyncGroupMembershipsAfterAuth.WHEN_AUTHENTICATION_CREATED_THE_USER;
import static com.atlassian.crowd.model.directory.DirectoryImpl.ATTRIBUTE_KEY_USE_NESTED_GROUPS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryServiceTest {

    @Mock
    private CrowdDirectoryService crowdDirectoryService;

    private DirectoryServiceImpl directoryService;

    @Before
    public void setup() {
        directoryService = new DirectoryServiceImpl(crowdDirectoryService);
    }

    @Test
    public void testGetDirectories() {
        Directory directory = createDirectory();
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();

        DirectoriesBean directories = directoryService.getDirectories();

        assertEquals(directories.getDirectories().iterator().next(), DirectoryBeanUtil.toDirectoryBean(directory));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDirectoriesUriException() {
        Directory directory = createDirectory("öäöää://uhveuehvde");
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();
        directoryService.getDirectories();
    }

    @Test
    public void testGetDirectory() {
        Directory directory = createDirectory();
        doReturn(directory).when(crowdDirectoryService).findDirectoryById(1L);

        AbstractDirectoryBean directoryBean = directoryService.getDirectory(1L);

        assertEquals(DirectoryBeanUtil.toDirectoryBean(directory), directoryBean);
    }

    @Test(expected = NotFoundException.class)
    public void testGetDirectoryNotExisting() {
        directoryService.getDirectory(1L);
    }

    @Test
    public void testSetDirectoriesWithoutExistingDirectory() {
        Directory directory = createDirectory();

        doReturn(directory).when(crowdDirectoryService).addDirectory(any());
        doReturn(Collections.emptyList()).when(crowdDirectoryService).findAllDirectories();

        DirectoryCrowdBean directoryBean = (DirectoryCrowdBean)DirectoryBeanUtil.toDirectoryBean(directory);
        directoryBean.getServer().setAppPassword("test");
        directoryService.setDirectories(new DirectoriesBean(Collections.singletonList(directoryBean)), false);

        assertTrue("Update Successful", true);
    }

    @Test
    public void testSetDirectoriesWithExistingDirectory() {
        Directory directory = createDirectory();

        doReturn(directory).when(crowdDirectoryService).findDirectoryById(1L);
        doReturn(directory).when(crowdDirectoryService).updateDirectory(any());
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();

        DirectoryCrowdBean directoryBean = (DirectoryCrowdBean)DirectoryBeanUtil.toDirectoryBean(directory);
        directoryBean.getServer().setAppPassword("test");
        DirectoriesBean directoryAdded = directoryService.setDirectories(new DirectoriesBean(Collections.singletonList(directoryBean)), false);

        assertEquals(directoryAdded.getDirectories().iterator().next().getName(), directoryBean.getName());
    }

    @Test
    public void testSetDirectoriesWithConnectionTest() {
        Directory directory = createDirectory();

        doReturn(directory).when(crowdDirectoryService).findDirectoryById(1L);
        doReturn(directory).when(crowdDirectoryService).updateDirectory(any());
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();

        DirectoryCrowdBean directoryBean = (DirectoryCrowdBean)DirectoryBeanUtil.toDirectoryBean(directory);
        directoryBean.getServer().setAppPassword("test");
        DirectoriesBean directoryAdded = directoryService.setDirectories(new DirectoriesBean(Collections.singletonList(directoryBean)), true);

        assertEquals(directoryAdded.getDirectories().iterator().next().getName(), directoryBean.getName());
    }

    @Test
    public void testSetDirectoryWithConnectionTest() {
        Directory directory = createDirectory();

        doReturn(directory).when(crowdDirectoryService).findDirectoryById(1L);
        doReturn(directory).when(crowdDirectoryService).updateDirectory(any());

        DirectoryCrowdBean directoryBean = (DirectoryCrowdBean)DirectoryBeanUtil.toDirectoryBean(directory);
        directoryBean.getServer().setAppPassword("test");
        AbstractDirectoryBean directoryAdded = directoryService.setDirectory(1L, directoryBean, true);

        assertEquals(directoryBean.getName(), directoryAdded.getName());
    }

    @Test(expected = BadRequestException.class)
    public void testSetDirectoryUnsupportedType() {
        directoryService.setDirectory(1L, new DirectoryLdapBean(), false);
    }

    @Test(expected = NotFoundException.class)
    public void testSetDirectoryNotExisting() {
        Directory directory = createDirectory();
        directoryService.setDirectory(1L, DirectoryBeanUtil.toDirectoryBean(directory), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddDirectoryUriException() {
        Directory responseDirectory = createDirectory("öäöää://uhveuehvde");
        doReturn(responseDirectory).when(crowdDirectoryService).addDirectory(any());

        Directory directory = createDirectory();
        DirectoryCrowdBean directoryBean = (DirectoryCrowdBean)DirectoryBeanUtil.toDirectoryBean(directory);

        directoryService.addDirectory(directoryBean, false);
    }

    @Test
    public void testAddDirectory() {
        Directory directory = createDirectory();
        doReturn(directory).when(crowdDirectoryService).addDirectory(any(Directory.class));

        DirectoryCrowdBean directoryBean = (DirectoryCrowdBean)DirectoryBeanUtil.toDirectoryBean(directory);
        directoryBean.getServer().setAppPassword("test");

        AbstractDirectoryBean directoryAdded = directoryService.addDirectory(directoryBean, false);
        assertEquals(directoryAdded.getName(), directoryBean.getName());
    }

    @Test(expected = BadRequestException.class)
    public void testAddDirectoryUnsupportedType() {
        directoryService.addDirectory(new DirectoryLdapBean(), false);
    }

    @Test(expected = BadRequestException.class)
    public void testDeleteDirectoriesWithoutForceParameter() {
        directoryService.deleteDirectories(false);
    }

    @Test
    public void testDeleteDirectories() throws DirectoryCurrentlySynchronisingException {
        Directory directory = createDirectory();
        doReturn(directory).when(crowdDirectoryService).findDirectoryById(1L);
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();

        directoryService.deleteDirectories(true);

        verify(crowdDirectoryService).removeDirectory(1L);
    }

    @Test
    public void testDeleteDirectoriesWithoutInternalDirectory() {
        Directory directory = createDirectory("http://localhost", DirectoryType.INTERNAL);
        doReturn(Collections.singletonList(directory)).when(crowdDirectoryService).findAllDirectories();

        directoryService.deleteDirectories(true);

        verify(crowdDirectoryService).findAllDirectories();
    }

    @Test
    public void testDeleteDirectory() throws DirectoryCurrentlySynchronisingException {
        doReturn(createDirectory()).when(crowdDirectoryService).findDirectoryById(1L);

        directoryService.deleteDirectory(1L);

        verify(crowdDirectoryService).removeDirectory(1L);
    }

    @Test(expected = NotFoundException.class)
    public void testDeleteDirectoryNotExisting() {
        directoryService.deleteDirectory(1L);
    }

    @Test(expected = ServiceUnavailableException.class)
    public void testDeleteDirectoryCurrentlySynchronisingException() throws DirectoryCurrentlySynchronisingException {
        doReturn(createDirectory()).when(crowdDirectoryService).findDirectoryById(1L);
        doThrow(new DirectoryCurrentlySynchronisingException(1L)).when(crowdDirectoryService).removeDirectory(1L);
        directoryService.deleteDirectory(1L);
    }

    private Directory createDirectory() {
        return createDirectory("http://localhost");
    }

    private Directory createDirectory(String url) {
        return createDirectory(url, DirectoryType.CROWD);
    }

    private Directory createDirectory(String url, DirectoryType directoryType) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put(CROWD_SERVER_URL, url);
        attributes.put(APPLICATION_PASSWORD, "test");
        attributes.put(APPLICATION_NAME, "confluence-client");
        attributes.put(CACHE_SYNCHRONISE_INTERVAL, "3600");
        attributes.put(ATTRIBUTE_KEY_USE_NESTED_GROUPS, "false");
        attributes.put(INCREMENTAL_SYNC_ENABLED, "true");
        attributes.put(SYNC_GROUP_MEMBERSHIP_AFTER_SUCCESSFUL_USER_AUTH_ENABLED, WHEN_AUTHENTICATION_CREATED_THE_USER.getValue());

        ImmutableDirectory immutableDirectory = ImmutableDirectory.builder("test", directoryType, "test.class")
                .setId(1L)
                .setCreatedDate(new Date())
                .setUpdatedDate(new Date())
                .setAttributes(attributes).build();
        return new DirectoryImpl(immutableDirectory);  //required because directory needs to be mutable throughout the test
    }

}
