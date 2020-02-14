package com.atlassian.mail.server;

import com.atlassian.mail.MailProtocol;

public interface DefaultTestSmtpMailServer extends DefaultTestMailServer, SMTPMailServer {

    String FROM = "mail@aservo.com";
    String PREFIX = "[ASERVO]";
    boolean TLS_REQUIRED = false;
    MailProtocol PROTOCOL = MailProtocol.SMTP;
    String PORT = PROTOCOL.getDefaultPort();

}
