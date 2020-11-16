package de.aservo.confapi.confluence.model;

import de.aservo.confapi.commons.constants.ConfAPI;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Data
@NoArgsConstructor
@XmlRootElement(name = ConfAPI.CACHE)
public class CacheBean {

    @NotNull
    @XmlElement
    private String name;

    @XmlElement
    private Integer maxObjectCount;

    @XmlElement
    private Long currentHeapSizeInByte;

    @XmlElement
    private Double effectivenessInPercent;

    @XmlElement
    private Double utilisationInPercent;

    @XmlElement
    private Boolean flushable;

}
