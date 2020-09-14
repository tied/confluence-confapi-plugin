package de.aservo.confapi.confluence.util;

import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.opensymphony.webwork.config.Configuration;
import de.aservo.confapi.commons.exception.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.atlassian.confluence.setup.ConfluenceBootstrapConstants.TEMP_DIR_PROP;

public class FilePartUtil {

    private static final Logger log = LoggerFactory.getLogger(FilePartUtil.class);

    public static File createFile(
            @Nonnull final FilePart filePart) {

        final File uploadDirectory = getUploadDirectory();

        if (!uploadDirectory.exists() && !uploadDirectory.mkdirs()) {
            throw new InternalServerErrorException(String.format(
                    "Failed to create directory %s", uploadDirectory.getAbsolutePath()));
        } else if (uploadDirectory.isFile()) {
            throw new InternalServerErrorException(String.format(
                    "Failed to create directory %s as a file of that name already exists", uploadDirectory.getAbsolutePath()));
        }

        log.info("Uploaded export file {}", filePart.getName());
        final File writtenFile;

        try {
            writtenFile = writeToFile(filePart.getInputStream(), uploadDirectory, filePart.getName());
        } catch (IOException e) {
            throw new InternalServerErrorException("Failed to write file to upload directory");
        }

        log.info("Uploaded export file written to {}", writtenFile);
        return writtenFile;
    }

    public static File getUploadDirectory() {
        final String uploadDirectoryPath = Configuration.getString(TEMP_DIR_PROP);
        return new File(uploadDirectoryPath);
    }

    static File writeToFile(
            @Nonnull final InputStream uploadedInputStream,
            @Nonnull final File directory,
            @Nonnull final String fileName) throws IOException {

        int read = 0;
        byte[] bytes = new byte[1024];

        final File file = new File(directory, fileName);
        try (OutputStream out = new FileOutputStream(file)) {
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
        }

        return file;
    }

    private FilePartUtil() {}

}
