package de.aservo.confapi.confluence.model.util;

import com.atlassian.mail.server.DefaultTestSmtpMailServerImpl;
import com.atlassian.mail.server.SMTPMailServer;
import de.aservo.confapi.commons.model.MailServerSmtpBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MailServerSmtpBeanUtilTest {

    @Test
    public void testToMailServerSmtpBean() {
        final SMTPMailServer server = new DefaultTestSmtpMailServerImpl();
        final MailServerSmtpBean bean = MailServerSmtpBeanUtil.toMailServerSmtpBean(server);

        assertNotNull(bean);
        assertEquals(server.getName(), bean.getName());
        assertEquals(server.getDescription(), bean.getDescription());
        assertEquals(server.getDefaultFrom(), bean.getFrom());
        assertEquals(server.getPrefix(), bean.getPrefix());
        assertEquals(server.getMailProtocol().getProtocol(), bean.getProtocol());
        assertEquals(server.getHostname(), bean.getHost());
        assertEquals(Integer.valueOf(server.getPort()), bean.getPort());
        assertEquals(server.isTlsRequired(), bean.getUseTls());
        assertTrue(server.getTimeout() == bean.getTimeout());
        assertEquals(server.getUsername(), bean.getUsername());
        assertNull(bean.getPassword());
    }

    @Test
    public void testToMailServerSmtpBeanHideEmptyDescription() {
        final SMTPMailServer server = new DefaultTestSmtpMailServerImpl();
        server.setDescription("");
        final MailServerSmtpBean bean = MailServerSmtpBeanUtil.toMailServerSmtpBean(server);

        assertNotNull(bean);
        assertNull(bean.getDescription());
    }

}
