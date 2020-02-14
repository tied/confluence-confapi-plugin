package de.aservo.atlassian.confluence.confapi.model;

import com.atlassian.mail.server.DefaultTestPopMailServerImpl;
import com.atlassian.mail.server.PopMailServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static de.aservo.atlassian.confluence.confapi.model.PopMailServerBean.DEFAULT_TIMEOUT;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PopMailServerBeanTest {

    @Test
    public void testDefaultConstructor() {
        final PopMailServerBean bean = new PopMailServerBean();

        assertNull(bean.getName());
        assertNull(bean.getDescription());
        assertNull(bean.getProtocol());
        assertNull(bean.getHost());
        assertNull(bean.getPort());
        assertEquals(DEFAULT_TIMEOUT, bean.getTimeout());
        assertNull(bean.getUsername());
        assertNull(bean.getPassword());
    }

    @Test
    public void testFromConstructor() throws Exception {
        final PopMailServer server = new DefaultTestPopMailServerImpl();
        final PopMailServerBean bean = PopMailServerBean.from(server);

        assertEquals(bean.getName(), server.getName());
        assertEquals(bean.getDescription(), server.getDescription());
        assertEquals(bean.getProtocol(), server.getMailProtocol().getProtocol());
        assertEquals(bean.getHost(), server.getHostname());
        assertEquals(bean.getPort(), Integer.valueOf(server.getPort()));
        assertEquals(bean.getTimeout(), server.getTimeout());
        assertEquals(bean.getUsername(), server.getUsername());
        // assertEquals(bean.getPassword(), server.getPassword());
    }

    @Test
    public void testFromConstructorHideEmptyDescription() throws Exception {
        final PopMailServer server = new DefaultTestPopMailServerImpl();
        server.setDescription("");
        final PopMailServerBean bean = PopMailServerBean.from(server);

        assertNull(bean.getDescription());
    }

    @Test
    public void testEquals() throws Exception {
        final PopMailServer server = new DefaultTestPopMailServerImpl();
        final PopMailServerBean bean1 = PopMailServerBean.from(server);
        final PopMailServerBean bean2 = PopMailServerBean.from(server);
        assertEquals(bean1, bean2);
    }

    @Test
    public void testEqualsNull() {
        final PopMailServerBean bean = new PopMailServerBean();
        assertFalse(bean.equals(null));
    }

    @Test
    public void testEqualsSameInstance() {
        final PopMailServerBean bean = new PopMailServerBean();
        assertEquals(bean, bean);
    }

    @Test
    public void testEqualsOtherType() {
        final PopMailServerBean bean = new PopMailServerBean();
        assertFalse(bean.equals(new Object()));
    }

}
