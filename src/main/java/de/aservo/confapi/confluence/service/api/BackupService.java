package de.aservo.confapi.confluence.service.api;

import de.aservo.confapi.confluence.model.BackupBean;
import de.aservo.confapi.confluence.model.BackupQueueBean;

import java.io.File;
import java.net.URI;
import java.util.UUID;

public interface BackupService {

    URI getExportSynchronously(
            BackupBean backupBean);

    URI getExportAsynchronously(
            BackupBean backupBean);

    void doImportSynchronously(
            File filePart);

    URI doImportAsynchronously(
            File filePart);

    BackupQueueBean getQueue(
            UUID uuid);

}
