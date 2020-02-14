package de.aservo.atlassian.confluence.confapi.model;

import com.atlassian.mail.server.DefaultTestSmtpMailServerImpl;
import com.atlassian.mail.server.SMTPMailServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static de.aservo.atlassian.confluence.confapi.model.PopMailServerBean.DEFAULT_TIMEOUT;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SmtpMailServerBeanTest {

    @Test
    public void testDefaultConstructor() {
        final SmtpMailServerBean bean = new SmtpMailServerBean();

        assertNull(bean.getName());
        assertNull(bean.getDescription());
        assertNull(bean.getFrom());
        assertNull(bean.getPrefix());
        assertNull(bean.getProtocol());
        assertNull(bean.getHost());
        assertNull(bean.getPort());
        assertFalse(bean.isTls());
        assertEquals(DEFAULT_TIMEOUT, bean.getTimeout());
        assertNull(bean.getUsername());
        assertNull(bean.getPassword());
    }

    @Test
    public void testFromConstructor() throws Exception {
        final SMTPMailServer server = new DefaultTestSmtpMailServerImpl();
        final SmtpMailServerBean bean = SmtpMailServerBean.from(server);

        assertEquals(bean.getName(), server.getName());
        assertEquals(bean.getDescription(), server.getDescription());
        assertEquals(bean.getFrom(), server.getDefaultFrom());
        assertEquals(bean.getPrefix(), server.getPrefix());
        assertEquals(bean.getProtocol(), server.getMailProtocol().getProtocol());
        assertEquals(bean.getHost(), server.getHostname());
        assertEquals(bean.getPort(), Integer.valueOf(server.getPort()));
        assertEquals(bean.isTls(), server.isTlsRequired());
        assertEquals(bean.getTimeout(), server.getTimeout());
        assertEquals(bean.getUsername(), server.getUsername());
        // assertEquals(bean.getPassword(), server.getPassword());
    }

    @Test
    public void testFromConstructorHideEmptyDescription() throws Exception {
        final SMTPMailServer server = new DefaultTestSmtpMailServerImpl();
        server.setDescription("");
        final SmtpMailServerBean bean = SmtpMailServerBean.from(server);

        assertNull(bean.getDescription());
    }

    @Test
    public void testEquals() throws Exception {
        final SMTPMailServer server = new DefaultTestSmtpMailServerImpl();
        final SmtpMailServerBean bean1 = SmtpMailServerBean.from(server);
        final SmtpMailServerBean bean2 = SmtpMailServerBean.from(server);
        assertEquals(bean1, bean2);
    }

    @Test
    public void testEqualsNull() {
        final SmtpMailServerBean bean = new SmtpMailServerBean();
        assertFalse(bean.equals(null));
    }

    @Test
    public void testEqualsSameInstance() {
        final SmtpMailServerBean bean = new SmtpMailServerBean();
        assertEquals(bean, bean);
    }

    @Test
    public void testEqualsOtherType() {
        final SmtpMailServerBean bean = new SmtpMailServerBean();
        assertFalse(bean.equals(new Object()));
    }

}
