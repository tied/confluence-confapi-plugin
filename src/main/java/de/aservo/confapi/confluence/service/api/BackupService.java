package de.aservo.confapi.confluence.service.api;

import de.aservo.confapi.confluence.model.BackupBean;
import de.aservo.confapi.confluence.model.BackupQueueBean;

import java.net.URI;

public interface BackupService {

    URI getExportSynchronously(
            BackupBean backupBean);

    URI getExportAsynchronously(
            BackupBean backupBean);

    BackupQueueBean getQueue(
            String uuid);

}
