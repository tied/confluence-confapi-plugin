package de.aservo.confapi.confluence.model;

import org.junit.Test;

import static com.atlassian.confluence.importexport.ImportExportManager.*;
import static org.junit.Assert.*;

public class BackupBeanTest {

    @Test
    public void testGetType() {
        final BackupBean backupBean = new BackupBean();

        assertEquals("The default type for no input should be XML", TYPE_XML, backupBean.getType());

        backupBean.setType("ASERVO");
        assertEquals("The default type for invalid input should still be XML", TYPE_XML, backupBean.getType());

        backupBean.setType("html");
        assertEquals("The lowercase input 'html' should be mapped to TYPE_HTML", TYPE_HTML, backupBean.getType());

        backupBean.setType("PDF");
        assertEquals("The uppercase input 'PDF' should be mapped to TYPE_PDF", TYPE_PDF, backupBean.getType());
    }

    @Test
    public void testGetBackupAttachments() {
        final BackupBean backupBean = new BackupBean();

        assertFalse("The default setting for backing up attachments should be false", backupBean.getBackupAttachments());

        backupBean.setBackupAttachments(false);
        assertFalse("The setting for backing up attachments when set to false should be false", backupBean.getBackupAttachments());

        backupBean.setBackupAttachments(true);
        assertTrue("The setting for backing up attachments when set to true should be true", backupBean.getBackupAttachments());
    }

    @Test
    public void testGetBackupComments() {
        final BackupBean backupBean = new BackupBean();

        assertFalse("The default setting for backing up comments should be false", backupBean.getBackupComments());

        backupBean.setBackupComments(false);
        assertFalse("The setting for backing up comments when set to false should be false", backupBean.getBackupComments());

        backupBean.setBackupComments(true);
        assertTrue("The setting for backing up comments when set to true should be true", backupBean.getBackupComments());
    }

}
