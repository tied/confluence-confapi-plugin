package de.aservo.atlassian.confluence.confapi.model.util;

import com.atlassian.mail.server.SMTPMailServer;
import de.aservo.confapi.commons.model.MailServerSmtpBean;

import javax.annotation.Nullable;

public class MailServerSmtpBeanUtil {

    @Nullable
    public static MailServerSmtpBean toMailServerSmtpBean(
            @Nullable final SMTPMailServer smtpMailServer) {

        if (smtpMailServer == null) {
            return null;
        }

        final MailServerSmtpBean mailServerSmtpBean = new MailServerSmtpBean();
        mailServerSmtpBean.setName(smtpMailServer.getName());
        mailServerSmtpBean.setDescription(smtpMailServer.getDescription());
        mailServerSmtpBean.setFrom(smtpMailServer.getDefaultFrom());
        mailServerSmtpBean.setPrefix(smtpMailServer.getPrefix());
        mailServerSmtpBean.setProtocol(smtpMailServer.getMailProtocol().getProtocol());
        mailServerSmtpBean.setHost(smtpMailServer.getHostname());
        mailServerSmtpBean.setPort(smtpMailServer.getPort());
        mailServerSmtpBean.setTls(smtpMailServer.isTlsRequired());
        mailServerSmtpBean.setTimeout(smtpMailServer.getTimeout());
        mailServerSmtpBean.setUsername(smtpMailServer.getUsername());
        return mailServerSmtpBean;
    }

    private MailServerSmtpBeanUtil() {
    }

}
