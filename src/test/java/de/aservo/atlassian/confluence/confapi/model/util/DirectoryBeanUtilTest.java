package de.aservo.atlassian.confluence.confapi.model.util;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.model.directory.DirectoryImpl;
import de.aservo.confapi.commons.model.DirectoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static com.atlassian.crowd.directory.RemoteCrowdDirectory.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryBeanUtilTest {

    @Test
    public void testToDirectory() {
        final DirectoryBean bean = DirectoryBean.EXAMPLE_1;
        final Directory directory = DirectoryBeanUtil.toDirectory(bean);

        assertNotNull(directory);
        assertEquals(directory.getName(), bean.getName());
        assertEquals(directory.getType().name(), bean.getType().toUpperCase());
        assertEquals(directory.getImplementationClass(), bean.getImplClass());

        final Map<String, String> attributes = directory.getAttributes();
        assertEquals(attributes.get(CROWD_SERVER_URL), bean.getCrowdUrl());
        assertEquals(attributes.get(APPLICATION_PASSWORD), bean.getAppPassword());
        assertEquals(attributes.get(CROWD_HTTP_PROXY_HOST), bean.getProxyHost());
        assertEquals(attributes.get(CROWD_HTTP_PROXY_PORT), bean.getProxyPort());
        assertEquals(attributes.get(CROWD_HTTP_PROXY_USERNAME), bean.getProxyUsername());
        assertEquals(attributes.get(CROWD_HTTP_PROXY_PASSWORD), bean.getProxyPassword());
    }

    @Test
    public void testToDirectoryBean() {
        final DirectoryImpl directory = new DirectoryImpl("test", DirectoryType.CROWD, "test.class");
        directory.setAttribute(CROWD_SERVER_URL, "http://localhost");
        directory.setAttribute(APPLICATION_PASSWORD, "test");
        directory.setAttribute(APPLICATION_NAME, "confluence-client");
        directory.setAttribute(CROWD_HTTP_PROXY_HOST, "http://localhost/proxy");
        directory.setAttribute(CROWD_HTTP_PROXY_PORT, "8080");
        directory.setAttribute(CROWD_HTTP_PROXY_USERNAME, "user");
        directory.setAttribute(CROWD_HTTP_PROXY_PASSWORD, "pass");

        final DirectoryBean directoryBean = DirectoryBeanUtil.toDirectoryBean(directory);

        assertNotNull(directoryBean);
        assertEquals(directory.getName(), directoryBean.getName());
        assertEquals(directory.getType().name(), directoryBean.getType());
        assertEquals(directory.getImplementationClass(), directoryBean.getImplClass());

        final Map<String, String> attributes = directory.getAttributes();
        assertEquals(attributes.get(CROWD_SERVER_URL), directoryBean.getCrowdUrl());
        assertEquals(attributes.get(APPLICATION_NAME), directoryBean.getClientName());
        assertNull(directoryBean.getAppPassword());
        assertEquals(attributes.get(CROWD_HTTP_PROXY_HOST), directoryBean.getProxyHost());
        assertEquals(attributes.get(CROWD_HTTP_PROXY_PORT), directoryBean.getProxyPort());
        assertEquals(attributes.get(CROWD_HTTP_PROXY_USERNAME), directoryBean.getProxyUsername());
        assertNull(directoryBean.getProxyPassword());
    }

}
