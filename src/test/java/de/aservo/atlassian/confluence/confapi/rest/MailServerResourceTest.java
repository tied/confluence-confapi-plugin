package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.mail.MailException;
import com.atlassian.mail.server.DefaultTestPopMailServerImpl;
import com.atlassian.mail.server.DefaultTestSmtpMailServerImpl;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.OtherTestPopMailServerImpl;
import com.atlassian.mail.server.OtherTestSmtpMailServerImpl;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.mail.server.SMTPMailServer;
import de.aservo.atlassian.confluence.confapi.model.PopMailServerBean;
import de.aservo.atlassian.confluence.confapi.model.SmtpMailServerBean;
import de.aservo.atlassian.confluence.confapi.model.ErrorCollection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MailServerResourceTest {

    @Mock
    private MailServerManager mailServerManager;

    @Test
    public void testGetSmtpMailServer() {
        final SMTPMailServer smtpMailServer = new DefaultTestSmtpMailServerImpl();
        doReturn(true).when(mailServerManager).isDefaultSMTPMailServerDefined();
        doReturn(smtpMailServer).when(mailServerManager).getDefaultSMTPMailServer();

        final MailServerResource resource = new MailServerResource(mailServerManager);
        final Response response = resource.getSmtpMailServer();
        final SmtpMailServerBean bean = (SmtpMailServerBean) response.getEntity();

        assertEquals(smtpMailServer.getName(), bean.getName());
        assertEquals(smtpMailServer.getDescription(), bean.getDescription());
        assertEquals(smtpMailServer.getHostname(), bean.getHost());
        assertEquals(smtpMailServer.getTimeout(), bean.getTimeout());
        assertEquals(smtpMailServer.getUsername(), bean.getUsername());
        assertEquals("<HIDDEN>", bean.getPassword());
        assertEquals(smtpMailServer.getDefaultFrom(), bean.getFrom());
        assertEquals(smtpMailServer.getPrefix(), bean.getPrefix());
        assertEquals(smtpMailServer.isTlsRequired(), bean.isTls());
        assertEquals(smtpMailServer.getMailProtocol().getProtocol(), bean.getProtocol());
        assertEquals(smtpMailServer.getPort(), String.valueOf(bean.getPort()));
    }

    @Test
    public void testGetSmtpMailServerNotFound() {
        doReturn(false).when(mailServerManager).isDefaultSMTPMailServerDefined();
        doReturn(null).when(mailServerManager).getDefaultSMTPMailServer();

        final MailServerResource resource = new MailServerResource(mailServerManager);
        final Response response = resource.getSmtpMailServer();
        final ErrorCollection bean = (ErrorCollection) response.getEntity();

        assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
        assertTrue(bean.hasAnyErrors());
    }

    @Test
    public void testPutSmtpMaiLServerUpdate() throws Exception {
        final SMTPMailServer defaultSmtpMailServer = new DefaultTestSmtpMailServerImpl();
        doReturn(true).when(mailServerManager).isDefaultSMTPMailServerDefined();
        doReturn(defaultSmtpMailServer).when(mailServerManager).getDefaultSMTPMailServer();

        final SMTPMailServer updateSmtpMailServer = new OtherTestSmtpMailServerImpl();
        final MailServerResource resource = new MailServerResource(mailServerManager);
        final SmtpMailServerBean requestSmtpMailServerBean = SmtpMailServerBean.from(updateSmtpMailServer);
        final Response response = resource.putSmtpMailServer(requestSmtpMailServerBean);
        final SmtpMailServerBean responseSmtpMailServerBean = (SmtpMailServerBean) response.getEntity();

        final ArgumentCaptor<SMTPMailServer> smtpMailServerCaptor = ArgumentCaptor.forClass(SMTPMailServer.class);
        verify(mailServerManager).update(smtpMailServerCaptor.capture());
        final SMTPMailServer smtpMailServer = smtpMailServerCaptor.getValue();

        assertEquals(SmtpMailServerBean.from(updateSmtpMailServer), SmtpMailServerBean.from(smtpMailServer));
        assertEquals(requestSmtpMailServerBean, responseSmtpMailServerBean);
    }

    @Test
    public void testPutSmtpMaiLServerCreate() throws Exception {
        doReturn(false).when(mailServerManager).isDefaultSMTPMailServerDefined();
        doReturn(null).when(mailServerManager).getDefaultSMTPMailServer();

        final SMTPMailServer createSmtpMailServer = new DefaultTestSmtpMailServerImpl();
        final MailServerResource resource = new MailServerResource(mailServerManager);
        final SmtpMailServerBean requestSmtpMailServerBean = SmtpMailServerBean.from(createSmtpMailServer);
        final Response response = resource.putSmtpMailServer(requestSmtpMailServerBean);
        final SmtpMailServerBean responseSmtpMailServerBean = (SmtpMailServerBean) response.getEntity();

        final ArgumentCaptor<SMTPMailServer> smtpMailServerCaptor = ArgumentCaptor.forClass(SMTPMailServer.class);
        verify(mailServerManager).create(smtpMailServerCaptor.capture());
        final SMTPMailServer smtpMailServer = smtpMailServerCaptor.getValue();

        assertEquals(SmtpMailServerBean.from(createSmtpMailServer), SmtpMailServerBean.from(smtpMailServer));
        assertEquals(requestSmtpMailServerBean, responseSmtpMailServerBean);
    }

    @Test
    public void testPutSmtpMaiLServerWithoutPort() throws Exception {
        doReturn(false).when(mailServerManager).isDefaultSMTPMailServerDefined();
        doReturn(null).when(mailServerManager).getDefaultSMTPMailServer();

        final SMTPMailServer createSmtpMailServer = new DefaultTestSmtpMailServerImpl();
        createSmtpMailServer.setPort(null);
        final MailServerResource resource = new MailServerResource(mailServerManager);
        final SmtpMailServerBean requestSmtpMailServerBean = SmtpMailServerBean.from(createSmtpMailServer);
        final Response response = resource.putSmtpMailServer(requestSmtpMailServerBean);
        final SmtpMailServerBean responseSmtpMailServerBean = (SmtpMailServerBean) response.getEntity();

        final ArgumentCaptor<SMTPMailServer> smtpMailServerCaptor = ArgumentCaptor.forClass(SMTPMailServer.class);
        verify(mailServerManager).create(smtpMailServerCaptor.capture());
        final SMTPMailServer smtpMailServer = smtpMailServerCaptor.getValue();

        assertEquals(createSmtpMailServer.getMailProtocol().getDefaultPort(), smtpMailServer.getPort());
    }

    @Test
    public void testPutSmtpMaiLServerException() throws Exception {
        doReturn(false).when(mailServerManager).isDefaultSMTPMailServerDefined();
        doReturn(null).when(mailServerManager).getDefaultSMTPMailServer();
        doThrow(new MailException("SMTP test exception")).when(mailServerManager).create(any());

        final SMTPMailServer createSmtpMailServer = new DefaultTestSmtpMailServerImpl();
        final MailServerResource resource = new MailServerResource(mailServerManager);
        final SmtpMailServerBean requestSmtpMailServerBean = SmtpMailServerBean.from(createSmtpMailServer);
        final Response response = resource.putSmtpMailServer(requestSmtpMailServerBean);
        final ErrorCollection responseErrorCollection = (ErrorCollection) response.getEntity();

        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        assertTrue(responseErrorCollection.hasAnyErrors());
    }

    @Test
    public void testGetPopMailServer() {
        final PopMailServer popMailServer = new DefaultTestPopMailServerImpl();
        doReturn(popMailServer).when(mailServerManager).getDefaultPopMailServer();

        final MailServerResource resource = new MailServerResource(mailServerManager);
        final Response response = resource.getPopMailServer();
        final PopMailServerBean bean = (PopMailServerBean) response.getEntity();

        assertEquals(popMailServer.getName(), bean.getName());
        assertEquals(popMailServer.getDescription(), bean.getDescription());
        assertEquals(popMailServer.getHostname(), bean.getHost());
        assertEquals(popMailServer.getTimeout(), bean.getTimeout());
        assertEquals(popMailServer.getUsername(), bean.getUsername());
        assertEquals("<HIDDEN>", bean.getPassword());
        assertEquals(popMailServer.getMailProtocol().getProtocol(), bean.getProtocol());
        assertEquals(popMailServer.getPort(), String.valueOf(bean.getPort()));
    }

    @Test
    public void testGetPopMailServerNotFound() {
        doReturn(null).when(mailServerManager).getDefaultPopMailServer();

        final MailServerResource resource = new MailServerResource(mailServerManager);
        final Response response = resource.getPopMailServer();
        final ErrorCollection responseErrorCollection = (ErrorCollection) response.getEntity();

        assertEquals(response.getStatus(), Response.Status.NOT_FOUND.getStatusCode());
        assertTrue(responseErrorCollection.hasAnyErrors());
    }

    @Test
    public void testPutPopMaiLServerUpdate() throws Exception {
        final PopMailServer defaultPopMailServer = new DefaultTestPopMailServerImpl();
        doReturn(defaultPopMailServer).when(mailServerManager).getDefaultPopMailServer();

        final PopMailServer updatePopMailServer = new OtherTestPopMailServerImpl();
        final MailServerResource resource = new MailServerResource(mailServerManager);
        final PopMailServerBean requestPopMailServerBean = PopMailServerBean.from(updatePopMailServer);
        final Response response = resource.putPopMailServer(requestPopMailServerBean);
        final PopMailServerBean responsePopMailServerBean = (PopMailServerBean) response.getEntity();

        final ArgumentCaptor<PopMailServer> popMailServerCaptor = ArgumentCaptor.forClass(PopMailServer.class);
        verify(mailServerManager).update(popMailServerCaptor.capture());
        final PopMailServer popMailServer = popMailServerCaptor.getValue();

        assertEquals(PopMailServerBean.from(updatePopMailServer), PopMailServerBean.from(popMailServer));
        assertEquals(requestPopMailServerBean, responsePopMailServerBean);
    }

    @Test
    public void testPutPopMaiLServerCreate() throws Exception {
        doReturn(null).when(mailServerManager).getDefaultPopMailServer();

        final PopMailServer createPopMailServer = new DefaultTestPopMailServerImpl();
        final MailServerResource resource = new MailServerResource(mailServerManager);
        final PopMailServerBean requestPopMailServerBean = PopMailServerBean.from(createPopMailServer);
        final Response response = resource.putPopMailServer(requestPopMailServerBean);
        final PopMailServerBean responsePopMailServerBean = (PopMailServerBean) response.getEntity();

        final ArgumentCaptor<PopMailServer> popMailServerCaptor = ArgumentCaptor.forClass(PopMailServer.class);
        verify(mailServerManager).create(popMailServerCaptor.capture());
        final PopMailServer popMailServer = popMailServerCaptor.getValue();

        PopMailServerBean from1 = PopMailServerBean.from(createPopMailServer);
        PopMailServerBean from2 = PopMailServerBean.from(popMailServer);

        assertEquals(from1, from2);
        assertEquals(requestPopMailServerBean, responsePopMailServerBean);
    }

    @Test
    public void testPutPopMaiLServerWithoutPort() throws Exception {
        doReturn(null).when(mailServerManager).getDefaultPopMailServer();

        final PopMailServer createPopMailServer = new DefaultTestPopMailServerImpl();
        createPopMailServer.setPort(null);
        final MailServerResource resource = new MailServerResource(mailServerManager);
        final PopMailServerBean requestPopMailServerBean = PopMailServerBean.from(createPopMailServer);
        final Response response = resource.putPopMailServer(requestPopMailServerBean);
        final PopMailServerBean responsePopMailServerBean = (PopMailServerBean) response.getEntity();

        final ArgumentCaptor<PopMailServer> popMailServerCaptor = ArgumentCaptor.forClass(PopMailServer.class);
        verify(mailServerManager).create(popMailServerCaptor.capture());
        final PopMailServer popMailServer = popMailServerCaptor.getValue();

        assertEquals(createPopMailServer.getMailProtocol().getDefaultPort(), popMailServer.getPort());
    }

    @Test
    public void testPutPopMaiLServerException() throws Exception {
        doReturn(null).when(mailServerManager).getDefaultPopMailServer();
        doThrow(new MailException("POP test exception")).when(mailServerManager).create(any());

        final PopMailServer createPopMailServer = new DefaultTestPopMailServerImpl();
        final MailServerResource resource = new MailServerResource(mailServerManager);
        final PopMailServerBean requestPopMailServerBean = PopMailServerBean.from(createPopMailServer);
        final Response response = resource.putPopMailServer(requestPopMailServerBean);
        final ErrorCollection responseErrorCollection = (ErrorCollection) response.getEntity();

        assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        assertTrue(responseErrorCollection.hasAnyErrors());
    }

}
