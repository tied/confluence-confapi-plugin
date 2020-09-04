package de.aservo.confapi.confluence.model;

import de.aservo.confapi.commons.constants.ConfAPI;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static com.atlassian.confluence.importexport.ImportExportManager.*;

@Data
@NoArgsConstructor
@XmlRootElement(name = ConfAPI.BACKUP)
public class BackupBean {

    @NotNull
    @XmlElement
    private String key;

    @XmlElement
    private String type;

    @XmlElement
    private Boolean backupAttachments;

    @XmlElement
    private Boolean backupComments;

    public String getType() {
        if (type != null) {
            if (type.equalsIgnoreCase("html")) {
                return TYPE_HTML;
            } else if (type.equalsIgnoreCase("pdf")) {
                return TYPE_PDF;
            }
        }

        // default type is XML
        return TYPE_XML;
    }

    public boolean getBackupAttachments() {
        return Boolean.TRUE.equals(backupAttachments);
    }

    public boolean getBackupComments() {
        return Boolean.TRUE.equals(backupComments);
    }

}
