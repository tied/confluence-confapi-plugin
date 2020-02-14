package com.atlassian.mail.server;

import com.atlassian.mail.MailProtocol;

public interface DefaultTestPopMailServer extends DefaultTestMailServer, PopMailServer {

    MailProtocol PROTOCOL = MailProtocol.POP;
    String PORT = PROTOCOL.getDefaultPort();

}
