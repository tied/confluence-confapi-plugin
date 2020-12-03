package de.aservo.confapi.confluence.model;

import de.aservo.confapi.commons.constants.ConfAPI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = ConfAPI.CACHES)
public class CachesBean {

    @XmlElement
    private Collection<CacheBean> caches;

}
