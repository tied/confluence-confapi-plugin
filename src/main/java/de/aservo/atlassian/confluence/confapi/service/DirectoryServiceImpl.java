package de.aservo.atlassian.confluence.confapi.service;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.atlassian.confluence.confapi.model.util.DirectoryBeanUtil;
import de.aservo.confapi.commons.exception.InternalServerErrorException;
import de.aservo.confapi.commons.model.DirectoryBean;
import de.aservo.confapi.commons.service.api.DirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.aservo.confapi.commons.util.BeanValidationUtil.validate;

@Component
@ExportAsService(DirectoryService.class)
public class DirectoryServiceImpl implements DirectoryService {

    private static final Logger log = LoggerFactory.getLogger(DirectoryServiceImpl.class);

    private final CrowdDirectoryService crowdDirectoryService;

    @Inject
    public DirectoryServiceImpl(@ComponentImport CrowdDirectoryService crowdDirectoryService) {
        this.crowdDirectoryService = checkNotNull(crowdDirectoryService);
    }

    public List<DirectoryBean> getDirectories() {
        return crowdDirectoryService.findAllDirectories().stream().map(DirectoryBeanUtil::toDirectoryBean).collect(Collectors.toList());
    }

    public DirectoryBean setDirectory(DirectoryBean directoryBean, boolean testConnection) {

        //preps and validation
        validate(directoryBean);
        Directory directory = DirectoryBeanUtil.toDirectory(directoryBean);
        String directoryName = directoryBean.getName();
        if (testConnection) {
            log.debug("testing user directory connection for {}", directoryName);
            crowdDirectoryService.testConnection(directory);
        }

        //check if directory exists already and if yes, remove it
        Optional<Directory> presentDirectory = crowdDirectoryService.findAllDirectories().stream().filter(dir -> dir.getName().equals(directory.getName())).findFirst();
        if (presentDirectory.isPresent()) {
            Directory presentDir = presentDirectory.get();
            log.info("removing existing user directory configuration '{}' before adding new configuration '{}'", presentDir.getName(), directory.getName());
            try {
                crowdDirectoryService.removeDirectory(presentDir.getId());
            } catch (DirectoryCurrentlySynchronisingException e) {
                throw new InternalServerErrorException(e.getMessage());
            }
        }

        //add new directory
        return DirectoryBeanUtil.toDirectoryBean(crowdDirectoryService.addDirectory(directory));
    }

}
