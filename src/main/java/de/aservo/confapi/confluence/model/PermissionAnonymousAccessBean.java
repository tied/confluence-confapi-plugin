package de.aservo.confapi.confluence.model;

import de.aservo.confapi.commons.constants.ConfAPI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Bean for anonymous access infos in REST requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = ConfAPI.PERMISSION_ANONYMOUS_ACCESS)
public class PermissionAnonymousAccessBean {

    @XmlElement
    private Boolean allowForPages;

    @XmlElement
    private Boolean allowForUserProfiles;

    public static final PermissionAnonymousAccessBean EXAMPLE_1;

    static {
        EXAMPLE_1 = new PermissionAnonymousAccessBean();
        EXAMPLE_1.setAllowForPages(true);
        EXAMPLE_1.setAllowForUserProfiles(true);
    }
}
