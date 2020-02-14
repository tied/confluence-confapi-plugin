package de.aservo.atlassian.confluence.confapi.bean;

import com.atlassian.confluence.setup.settings.Settings;

import javax.xml.bind.annotation.*;
import java.util.Objects;

@XmlRootElement(name = "smtp")
@XmlAccessorType(XmlAccessType.FIELD)
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
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (this == o)
            return true;
        else if (!(o instanceof SettingsBean))
            return false;
        final SettingsBean other = (SettingsBean) o;
        return Objects.equals(getBaseurl(), other.getBaseurl())
                && Objects.equals(getTitle(), other.getTitle())
                ;
    }

}
