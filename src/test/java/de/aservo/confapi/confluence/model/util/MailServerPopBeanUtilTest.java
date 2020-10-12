package de.aservo.confapi.confluence.model.util;

import com.atlassian.mail.server.DefaultTestPopMailServerImpl;
import com.atlassian.mail.server.PopMailServer;
import de.aservo.confapi.commons.model.MailServerPopBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class MailServerPopBeanUtilTest {

    @Test
    public void testToMailServerPopBean() {
        final PopMailServer server = new DefaultTestPopMailServerImpl();
        final MailServerPopBean bean = MailServerPopBeanUtil.toMailServerPopBean(server);

        assertNotNull(bean);
        assertEquals(server.getName(), bean.getName());
        assertEquals(server.getDescription(), bean.getDescription());
        assertEquals(server.getMailProtocol().getProtocol(), bean.getProtocol());
        assertEquals(server.getHostname(), bean.getHost());
        assertEquals(Integer.valueOf(server.getPort()), bean.getPort());
        assertTrue(server.getTimeout() == bean.getTimeout());
        assertEquals(server.getUsername(), bean.getUsername());
        assertNull(bean.getPassword());
    }

    @Test
    public void testToMailServerPopBeanHideEmptyDescription() {
        final PopMailServer server = new DefaultTestPopMailServerImpl();
        server.setDescription("");
        final MailServerPopBean bean = MailServerPopBeanUtil.toMailServerPopBean(server);

        assertNotNull(bean);
        assertNull(bean.getDescription());
    }

}
