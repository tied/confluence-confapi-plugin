package de.aservo.atlassian.confluence.confapi.model;

import com.atlassian.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Bean for licence infos in REST requests.
 */
@Data
@NoArgsConstructor
@XmlRootElement(name = "user")
public class UserBean {

    @XmlElement
    @NotNull
    @Size(min = 1)
    private String userName;

    @XmlElement
    private String fullName;

    @XmlElement
    private String email;

    @XmlElement
    private String password;

    /**
     * Instantiates a new User bean.
     *
     * @param confluenceUser the confluence user
     */
    public UserBean(User confluenceUser) {
        userName = confluenceUser.getName();
        fullName = confluenceUser.getFullName();
        email = confluenceUser.getEmail();
    }
}
