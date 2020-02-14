package de.aservo.atlassian.confluence.confapi.bean;

import com.atlassian.mail.server.SMTPMailServer;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Objects;

/**
 * Bean for SMTP mail server in REST requests.
 */
@XmlRootElement(name = "smtp")
public class SmtpMailServerBean {

    public static final long DEFAULT_TIMEOUT = 10000L;

    @XmlElement
    private final String name;

    @XmlElement
    private final String description;

    @XmlElement
    private final String from;

    @XmlElement
    private final String prefix;

    @XmlElement
    private final String protocol;

    @XmlElement
    private final String host;

    @XmlElement
    private final Integer port;

    @XmlElement
    private final boolean tls;

    @XmlElement
    private final long timeout;

    @XmlElement
    private final String username;

    @XmlElement
    private final String password;

    /**
     * The default constructor is needed for JSON request deserialization.
     */
    public SmtpMailServerBean() {
        this.name = null;
        this.description = null;
        this.from = null;
        this.prefix = null;
        this.protocol = null;
        this.host = null;
        this.port = null;
        this.tls = false;
        this.timeout = DEFAULT_TIMEOUT;
        this.username = null;
        this.password = null;
    }

    private SmtpMailServerBean(
            final String name,
            final String description,
            final String from,
            final String prefix,
            final String protocol,
            final String host,
            final Integer port,
            final boolean tls,
            final long timeout,
            final String username,
            final String password) {

        this.name = name;
        this.description = StringUtils.isNoneBlank(description)
                ? description
                : null;
        this.from = from;
        this.prefix = prefix;
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.tls = tls;
        this.timeout = timeout;
        this.username = username;
        this.password = StringUtils.isNotBlank(password)
                ? "<HIDDEN>"
                : null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFrom() {
        return from;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getProtocol() {
        return protocol != null ? protocol.toLowerCase() : null;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public boolean isTls() {
        return tls;
    }

    public long getTimeout() {
        return timeout;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // TODO: More specific exception
    public static SmtpMailServerBean from(
            final SMTPMailServer smtpMailServer) throws Exception {

        if (smtpMailServer == null) {
            throw new Exception("No mail server defined");
        }

        Integer port = null;

        if (StringUtils.isNotBlank(smtpMailServer.getPort())) {
            port = Integer.parseInt(smtpMailServer.getPort());
        }

        return new SmtpMailServerBean(
                smtpMailServer.getName(),
                smtpMailServer.getDescription(),
                smtpMailServer.getDefaultFrom(),
                smtpMailServer.getPrefix(),
                smtpMailServer.getMailProtocol().getProtocol(),
                smtpMailServer.getHostname(),
                port,
                smtpMailServer.isTlsRequired(),
                smtpMailServer.getTimeout(),
                smtpMailServer.getUsername(),
                smtpMailServer.getPassword());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (this == o)
            return true;
        else if (!(o instanceof SmtpMailServerBean))
            return false;
        final SmtpMailServerBean other = (SmtpMailServerBean) o;
        return Objects.equals(getName(), other.getName())
                && Objects.equals(getDescription(), other.getDescription())
                && Objects.equals(getFrom(), other.getFrom())
                && Objects.equals(getPrefix(), other.getPrefix())
                && Objects.equals(getProtocol(), other.getProtocol())
                && Objects.equals(getHost(), other.getHost())
                && Objects.equals(getPort(), other.getPort())
                && Objects.equals(isTls(), other.isTls())
                && Objects.equals(getUsername(), other.getUsername())
                // && Objects.equals(getPassword(), other.getPassword())
                && Objects.equals(getTimeout(), other.getTimeout())
                ;
    }

}
