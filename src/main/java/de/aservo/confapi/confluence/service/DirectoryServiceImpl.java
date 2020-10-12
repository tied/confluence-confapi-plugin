package de.aservo.confapi.confluence.service;

import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.impl.ImmutableDirectory;
import com.atlassian.crowd.exception.DirectoryCurrentlySynchronisingException;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.aservo.confapi.commons.exception.BadRequestException;
import de.aservo.confapi.commons.exception.NotFoundException;
import de.aservo.confapi.commons.exception.ServiceUnavailableException;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

@Component
@ExportAsService(DirectoriesService.class)
public class DirectoryServiceImpl implements DirectoriesService {

    private static final Logger log = LoggerFactory.getLogger(DirectoryServiceImpl.class);
    public static final int RETRY_AFTER_IN_SECONDS = 5;

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
    public AbstractDirectoryBean getDirectory(long id) {
        Directory directory = findDirectory(id);
        return DirectoryBeanUtil.toDirectoryBean(directory);
    }

    @Override
    public DirectoriesBean setDirectories(DirectoriesBean directoriesBean, boolean testConnection) {

        final Map<String, Directory> existingDirectoriesByName = crowdDirectoryService.findAllDirectories().stream()
                .collect(Collectors.toMap(Directory::getName, Function.identity()));

        directoriesBean.getDirectories().forEach(directoryRequestBean -> {
            if (directoryRequestBean instanceof DirectoryCrowdBean) {
                DirectoryCrowdBean crowdRequestBean = (DirectoryCrowdBean)directoryRequestBean;

                if (existingDirectoriesByName.containsKey(crowdRequestBean.getName())) {
                    setDirectory(existingDirectoriesByName.get(crowdRequestBean.getName()).getId(), crowdRequestBean, testConnection);
                } else {
                    addDirectory(crowdRequestBean, testConnection);
                }
            } else {
                throw new BadRequestException(format("Updating directory type '%s' is not supported (yet)", directoryRequestBean.getClass()));
            }
        });
        return getDirectories();
    }

    @Override
    public AbstractDirectoryBean setDirectory(long id, @NotNull AbstractDirectoryBean abstractDirectoryBean, boolean testConnection) {
        if (abstractDirectoryBean instanceof DirectoryCrowdBean) {
            return setDirectoryCrowd(id, (DirectoryCrowdBean) abstractDirectoryBean, testConnection);
        } else {
            throw new BadRequestException(format("Setting directory type '%s' is not supported (yet)", abstractDirectoryBean.getClass()));
        }
    }

    private AbstractDirectoryBean setDirectoryCrowd(long id, @NotNull DirectoryCrowdBean crowdBean, boolean testConnection) {
        Directory existingDirectory = findDirectory(id);
        Directory directory = validateAndCreateDirectoryConfig(crowdBean, testConnection);

        ImmutableDirectory.Builder directoryBuilder = ImmutableDirectory.newBuilder(existingDirectory);
        directoryBuilder.setAttributes(directory.getAttributes());
        directoryBuilder.setDescription(directory.getDescription());
        directoryBuilder.setName(directory.getName());
        directoryBuilder.setActive(directory.isActive());
        Directory updatedDirectory = directoryBuilder.toDirectory();

        Directory responseDirectory = crowdDirectoryService.updateDirectory(updatedDirectory);
        return DirectoryBeanUtil.toDirectoryBean(responseDirectory);
    }

    @Override
    public AbstractDirectoryBean addDirectory(AbstractDirectoryBean abstractDirectoryBean, boolean testConnection) {
        if (abstractDirectoryBean instanceof DirectoryCrowdBean) {
            DirectoryCrowdBean crowdBean = (DirectoryCrowdBean)abstractDirectoryBean;
            Directory directory = validateAndCreateDirectoryConfig(crowdBean, testConnection);
            Directory addedDirectory = crowdDirectoryService.addDirectory(directory);
            return DirectoryBeanUtil.toDirectoryBean(addedDirectory);
        } else {
            throw new BadRequestException(format("Adding directory type '%s' is not supported (yet)", abstractDirectoryBean.getClass()));
        }
    }

    @Override
    public void deleteDirectories(boolean force) {
        if (!force) {
            throw new BadRequestException("'force = true' must be supplied to delete all entries");
        } else {
            for (Directory directory : crowdDirectoryService.findAllDirectories()) {

                //do not remove the internal directory
                if (!DirectoryType.INTERNAL.equals(directory.getType())) {
                    deleteDirectory(directory.getId());
                }
            }
        }
    }

    @Override
    public void deleteDirectory(long id) {

        //ensure the directory exists
        findDirectory(id);

        //delete the directory
        try {
            crowdDirectoryService.removeDirectory(id);
        } catch (DirectoryCurrentlySynchronisingException e) {
            throw new ServiceUnavailableException(e, RETRY_AFTER_IN_SECONDS);
        }
    }

    private Directory findDirectory(long id) {
        Directory directory = crowdDirectoryService.findDirectoryById(id);
        if (directory == null) {
            throw new NotFoundException(String.format("directory with id '%s' was not found!", id));
        }
        return directory;
    }

    private Directory validateAndCreateDirectoryConfig(DirectoryCrowdBean crowdBean, boolean testConnection) {
        Directory directory = DirectoryBeanUtil.toDirectory(crowdBean);
        String directoryName = crowdBean.getName();
        if (testConnection) {
            log.debug("testing user directory connection for {}", directoryName);
            crowdDirectoryService.testConnection(directory);
        }
        return directory;
    }

}
