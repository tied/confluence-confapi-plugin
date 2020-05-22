package de.aservo.atlassian.confluence.confapi.service;

import com.atlassian.mail.MailException;
import com.atlassian.mail.MailProtocol;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.mail.server.SMTPMailServer;
import com.atlassian.mail.server.impl.PopMailServerImpl;
import com.atlassian.mail.server.impl.SMTPMailServerImpl;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.atlassian.confapi.exception.BadRequestException;
import de.aservo.atlassian.confapi.model.MailServerPopBean;
import de.aservo.atlassian.confapi.model.MailServerSmtpBean;
import de.aservo.atlassian.confapi.service.api.MailServerService;
import de.aservo.atlassian.confluence.confapi.model.util.MailServerPopBeanUtil;
import de.aservo.atlassian.confluence.confapi.model.util.MailServerSmtpBeanUtil;
import de.aservo.atlassian.confluence.confapi.util.MailProtocolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@ExportAsService(MailServerService.class)
public class MailServerServiceImpl implements MailServerService {

    private final MailServerManager mailServerManager;

    @Inject
    public MailServerServiceImpl(@ComponentImport final MailServerManager mailServerManager) {
        this.mailServerManager = mailServerManager;
    }

    @Override
    public MailServerSmtpBean getMailServerSmtp() {
        final SMTPMailServer smtpMailServer = mailServerManager.getDefaultSMTPMailServer();
        return MailServerSmtpBeanUtil.toMailServerSmtpBean(smtpMailServer);
    }

    @Override
    public MailServerSmtpBean setMailServerSmtp(MailServerSmtpBean mailServerSmtpBean) {
        final SMTPMailServer smtpMailServer = mailServerManager.isDefaultSMTPMailServerDefined()
                ? mailServerManager.getDefaultSMTPMailServer()
                : new SMTPMailServerImpl();

        assert smtpMailServer != null;

        if (StringUtils.isNotBlank(mailServerSmtpBean.getName())) {
            smtpMailServer.setName(mailServerSmtpBean.getName());
        }

        if (StringUtils.isNotBlank(mailServerSmtpBean.getDescription())) {
            smtpMailServer.setDescription(mailServerSmtpBean.getDescription());
        }

        if (StringUtils.isNotBlank(mailServerSmtpBean.getFrom())) {
            smtpMailServer.setDefaultFrom(mailServerSmtpBean.getFrom());
        }

        if (StringUtils.isNotBlank(mailServerSmtpBean.getPrefix())) {
            smtpMailServer.setPrefix(mailServerSmtpBean.getPrefix());
        }

        smtpMailServer.setMailProtocol(MailProtocolUtil.find(mailServerSmtpBean.getProtocol(), MailProtocol.SMTP));

        if (StringUtils.isNotBlank(mailServerSmtpBean.getHost())) {
            smtpMailServer.setHostname(mailServerSmtpBean.getHost());
        }

        if (mailServerSmtpBean.getPort() != null) {
            smtpMailServer.setPort(String.valueOf(mailServerSmtpBean.getPort()));
        } else {
            smtpMailServer.setPort(smtpMailServer.getMailProtocol().getDefaultPort());
        }

        smtpMailServer.setTlsRequired(mailServerSmtpBean.isTls());

        if (StringUtils.isNotBlank(mailServerSmtpBean.getUsername())) {
            smtpMailServer.setUsername(mailServerSmtpBean.getUsername());
        }

        smtpMailServer.setTimeout(mailServerSmtpBean.getTimeout());

        try {
            if (mailServerManager.isDefaultSMTPMailServerDefined()) {
                mailServerManager.update(smtpMailServer);
            } else {
                smtpMailServer.setId(mailServerManager.create(smtpMailServer));
            }
        } catch (MailException e) {
            throw new BadRequestException(e);
        }

        return getMailServerSmtp();
    }

    @Override
    public MailServerPopBean getMailServerPop() {
        final PopMailServer popMailServer = mailServerManager.getDefaultPopMailServer();
        return MailServerPopBeanUtil.toMailServerPopBean(popMailServer);
    }

    @Override
    public MailServerPopBean setMailServerPop(MailServerPopBean mailServerPopBean) {
        final PopMailServer popMailServer = mailServerManager.getDefaultPopMailServer() != null
                ? mailServerManager.getDefaultPopMailServer()
                : new PopMailServerImpl();

        assert popMailServer != null;

        if (StringUtils.isNotBlank(mailServerPopBean.getName())) {
            popMailServer.setName(mailServerPopBean.getName());
        }

        if (StringUtils.isNotBlank(mailServerPopBean.getDescription())) {
            popMailServer.setDescription(mailServerPopBean.getDescription());
        }

        popMailServer.setMailProtocol(MailProtocolUtil.find(mailServerPopBean.getProtocol(), MailProtocol.POP));

        if (StringUtils.isNotBlank(mailServerPopBean.getHost())) {
            popMailServer.setHostname(mailServerPopBean.getHost());
        }

        if (mailServerPopBean.getPort() != null) {
            popMailServer.setPort(String.valueOf(mailServerPopBean.getPort()));
        } else {
            popMailServer.setPort(popMailServer.getMailProtocol().getDefaultPort());
        }

        if (StringUtils.isNotBlank(mailServerPopBean.getUsername())) {
            popMailServer.setUsername(mailServerPopBean.getUsername());
        }

        popMailServer.setTimeout(mailServerPopBean.getTimeout());

        try {
            if (mailServerManager.getDefaultPopMailServer() != null) {
                mailServerManager.update(popMailServer);
            } else {
                popMailServer.setId(mailServerManager.create(popMailServer));
            }
        } catch (MailException e) {
            throw new BadRequestException(e);
        }

        return getMailServerPop();
    }
}
