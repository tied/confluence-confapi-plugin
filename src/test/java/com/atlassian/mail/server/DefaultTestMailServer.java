package com.atlassian.mail.server;

public interface DefaultTestMailServer extends MailServer {

    String NAME = "ASERVO Mail";
    String DESCRIPTION = NAME + " Description";
    String HOST = "mail.aservo.com";
    long TIMEOUT = 0L;
    String USERNAME = "username";
    String PASSWORD = "p4$$w0rd";

}
