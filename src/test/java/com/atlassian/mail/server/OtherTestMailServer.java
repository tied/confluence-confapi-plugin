package com.atlassian.mail.server;

public interface OtherTestMailServer extends MailServer {

    String NAME = "OTHER Mail";
    String DESCRIPTION = NAME + " Description";
    String HOST = "other.aservo.com";
    long TIMEOUT = 10000L;
    String USERNAME = "otheruser";
    String PASSWORD = "otherpass";

}
