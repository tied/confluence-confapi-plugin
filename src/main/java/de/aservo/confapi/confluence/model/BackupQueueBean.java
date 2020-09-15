package de.aservo.confapi.confluence.model;

import de.aservo.confapi.commons.constants.ConfAPI;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;

@Data
@NoArgsConstructor
@XmlRootElement(name = ConfAPI.BACKUP + "-" + ConfAPI.BACKUP_QUEUE)
public class BackupQueueBean {

    @XmlElement
    private int percentageComplete;

    @XmlElement
    private long elapsedTimeInMillis;

    @XmlElement
    private long estimatedTimeRemainingInMillis;

    @XmlElement
    private URI entityUrl;

}
