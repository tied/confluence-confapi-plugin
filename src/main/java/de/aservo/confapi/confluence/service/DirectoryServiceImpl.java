package de.aservo.confapi.confluence.service;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.confapi.commons.exception.InternalServerErrorException;
import de.aservo.confapi.commons.model.AbstractDirectoryBean;
import de.aservo.confapi.commons.model.DirectoriesBean;
import de.aservo.confapi.commons.model.DirectoryCrowdBean;
import de.aservo.confapi.commons.service.api.DirectoriesService;
import de.aservo.confapi.confluence.model.util.DirectoryBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.aservo.confapi.commons.util.BeanValidationUtil.validate;
import static java.lang.String.format;

@Component
@ExportAsService(DirectoriesService.class)
public class DirectoryServiceImpl implements DirectoriesService {

    private static final Logger log = LoggerFactory.getLogger(DirectoryServiceImpl.class);

    private final CrowdDirectoryService crowdDirectoryService;

    @Inject
    public DirectoryServiceImpl(@ComponentImport CrowdDirectoryService crowdDirectoryService) {
        this.crowdDirectoryService = checkNotNull(crowdDirectoryService);
    }

    @Override
    public DirectoriesBean getDirectories() {
        List<AbstractDirectoryBean> beans = new ArrayList<>();
        for (Directory directory : crowdDirectoryService.findAllDirectories()) {
            AbstractDirectoryBean crowdBean;
            crowdBean = DirectoryBeanUtil.toDirectoryBean(directory);
            beans.add(crowdBean);
        }
        return new DirectoriesBean(beans);
    }

    @Override
    public AbstractDirectoryBean getDirectory(long l) {
        return null;
    }

    @Override
    public DirectoriesBean setDirectories(DirectoriesBean directoriesBean, boolean testConnection) {
        directoriesBean.getDirectories().forEach(directoryBaseBean -> {
            if (directoryBaseBean instanceof DirectoryCrowdBean) {

                //preps and validation
                DirectoryCrowdBean crowdBean = (DirectoryCrowdBean)directoryBaseBean;
                Directory directory = validateAndCreateDirectoryConfig(crowdBean, testConnection);

                //check if directory exists already and if yes, remove it
                Optional<Directory> presentDirectory = crowdDirectoryService.findAllDirectories().stream()
                        .filter(dir -> dir.getName().equals(directory.getName())).findFirst();
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
                crowdDirectoryService.addDirectory(directory);
            } else {
                throw new InternalServerErrorException(format("Setting directory type '%s' is not supported (yet)", directoryBaseBean.getClass()));
            }
        });
        return getDirectories();
    }

    @Override
    public AbstractDirectoryBean setDirectory(long l, @NotNull AbstractDirectoryBean abstractDirectoryBean, boolean b) {
        return null;
    }

    @Override
    public AbstractDirectoryBean addDirectory(AbstractDirectoryBean abstractDirectoryBean, boolean testConnection) {
        if (abstractDirectoryBean instanceof DirectoryCrowdBean) {
            DirectoryCrowdBean crowdBean = (DirectoryCrowdBean)abstractDirectoryBean;
            Directory directory = validateAndCreateDirectoryConfig(crowdBean, testConnection);
            Directory addedDirectory = crowdDirectoryService.addDirectory(directory);
            return DirectoryBeanUtil.toDirectoryBean(addedDirectory);
        } else {
            throw new InternalServerErrorException(format("Adding directory type '%s' is not supported (yet)", abstractDirectoryBean.getClass()));
        }
    }

    @Override
    public void deleteDirectories(boolean b) {

    }

    @Override
    public void deleteDirectory(long l) {

    }

    private Directory validateAndCreateDirectoryConfig(DirectoryCrowdBean crowdBean, boolean testConnection) {
        validate(crowdBean);
        Directory directory = DirectoryBeanUtil.toDirectory(crowdBean);
        String directoryName = crowdBean.getName();
        if (testConnection) {
            log.debug("testing user directory connection for {}", directoryName);
            crowdDirectoryService.testConnection(directory);
        }
        return directory;
    }

}
