package de.aservo.confapi.confluence.model.util;

import com.atlassian.mail.server.PopMailServer;
import de.aservo.confapi.commons.model.MailServerPopBean;

import javax.annotation.Nullable;

public class MailServerPopBeanUtil {

    @Nullable
    public static MailServerPopBean toMailServerPopBean(
            @Nullable final PopMailServer popMailServer) {

        if (popMailServer == null) {
            return null;
        }

        final MailServerPopBean mailServerPopBean = new MailServerPopBean();
        mailServerPopBean.setName(popMailServer.getName());
        mailServerPopBean.setDescription(popMailServer.getDescription());
        mailServerPopBean.setProtocol(popMailServer.getMailProtocol().getProtocol());
        mailServerPopBean.setHost(popMailServer.getHostname());
        mailServerPopBean.setPort(popMailServer.getPort());
        mailServerPopBean.setTimeout(popMailServer.getTimeout());
        mailServerPopBean.setUsername(popMailServer.getUsername());
        return mailServerPopBean;
    }

    private MailServerPopBeanUtil() {
    }

}
