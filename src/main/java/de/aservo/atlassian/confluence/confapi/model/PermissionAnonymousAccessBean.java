package de.aservo.atlassian.confluence.confapi.model;

import de.aservo.atlassian.confapi.constants.ConfAPI;
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
}