package de.aservo.atlassian.confluence.confapi.model;

import com.atlassian.confluence.setup.settings.Settings;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Bean for general settings in REST requests.
 */
@XmlRootElement(name = "settings")
public class SettingsBean {

    @XmlElement
    private final String baseurl;

    @XmlElement
    private final String title;

    /**
     * The default constructor is needed for JSON request deserialization.
     */
    public SettingsBean() {
        this.baseurl = null;
        this.title = null;
    }

    public SettingsBean(
            final String baseurl,
            final String title) {

        this.baseurl = baseurl;
        this.title = title;
    }

    public String getBaseurl() {
        return baseurl;
    }

    public String getTitle() {
        return title;
    }

    public static SettingsBean from(
            final Settings settings) {

        return new SettingsBean(
                settings.getBaseUrl(),
                settings.getSiteTitle()
        );
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

}
