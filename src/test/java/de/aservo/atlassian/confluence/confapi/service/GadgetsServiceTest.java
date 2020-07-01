package de.aservo.atlassian.confluence.confapi.service;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpec;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore;
import com.atlassian.gadgets.spec.GadgetSpec;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.sal.api.user.UserKey;
import de.aservo.confapi.commons.exception.BadRequestException;
import de.aservo.confapi.commons.model.GadgetBean;
import de.aservo.confapi.commons.model.GadgetsBean;
import de.aservo.confapi.commons.service.api.GadgetsService;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Locale;
import java.util.UUID;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

//power mockito required here for mocking static methods of AuthenticatedUserThreadLocal
@RunWith(PowerMockRunner.class)
@PrepareForTest(AuthenticatedUserThreadLocal.class)
public class GadgetsServiceTest {

    private ExternalGadgetSpecStore externalGadgetSpecStore;
    private GadgetSpecFactory gadgetSpecFactory;
    private LocaleManager localeManager;

    private GadgetsService gadgetsService;

    @Before
    public void setup() {
        externalGadgetSpecStore = mock(ExternalGadgetSpecStore.class);
        gadgetSpecFactory = mock(GadgetSpecFactory.class);
        localeManager = mock(LocaleManager.class);
        gadgetsService = new GadgetsServiceImpl(externalGadgetSpecStore, gadgetSpecFactory, localeManager);
    }

    @Test
    public void testGetGadgets() throws URISyntaxException {
        ExternalGadgetSpec externalGadgetSpec = createExternalGadgetSpec();
        doReturn(Collections.singletonList(externalGadgetSpec)).when(externalGadgetSpecStore).entries();

        GadgetsBean gadgetsBean = gadgetsService.getGadgets();

        assertEquals(externalGadgetSpec.getSpecUri().toString(), gadgetsBean.getGadgets().iterator().next().getUrl());
    }

    @Test(expected = BadRequestException.class)
    public void testAddGadgetWithNullUrl() {
        gadgetsService.addGadget(new GadgetBean());
    }

    @Test(expected = BadRequestException.class)
    public void testAddGadgetWithInvalidUrl() {
        GadgetBean gadgetBean = new GadgetBean();
        gadgetBean.setUrl("this is invalid uri format");
        gadgetsService.addGadget(gadgetBean);
    }

    @Test
    public void testAddGadget() throws URISyntaxException, IllegalAccessException {
        ExternalGadgetSpec externalGadgetSpec = createExternalGadgetSpec();
        doReturn(Collections.singletonList(externalGadgetSpec)).when(externalGadgetSpecStore).entries();
        doReturn(externalGadgetSpec).when(externalGadgetSpecStore).add(any());

        ConfluenceUser user = createConfluenceUser();
        String gadgetUrlToSet = externalGadgetSpec.getSpecUri().toString();
        GadgetBean gadgetBean = new GadgetBean();
        gadgetBean.setUrl(gadgetUrlToSet);

        GadgetSpec gadgetSpec = GadgetSpec.gadgetSpec(externalGadgetSpec.getSpecUri()).build();

        PowerMock.mockStatic(AuthenticatedUserThreadLocal.class);
        expect(AuthenticatedUserThreadLocal.get()).andReturn(user);
        PowerMock.replay(AuthenticatedUserThreadLocal.class);

        doReturn(Locale.GERMAN).when(localeManager).getLocale(user);
        doReturn(gadgetSpec).when(gadgetSpecFactory).getGadgetSpec(externalGadgetSpec.getSpecUri(), null);

        GadgetBean gadgetsBean = gadgetsService.addGadget(gadgetBean);
        assertEquals(gadgetUrlToSet, gadgetsBean.getUrl());
    }

    @Test
    public void testSetGadgets() throws URISyntaxException, IllegalAccessException {
        ExternalGadgetSpec externalGadgetSpec = createExternalGadgetSpec();
        doReturn(Collections.singletonList(externalGadgetSpec)).when(externalGadgetSpecStore).entries();
        doReturn(externalGadgetSpec).when(externalGadgetSpecStore).add(any());

        ConfluenceUser user = createConfluenceUser();
        String gadgetUrlToSet = externalGadgetSpec.getSpecUri().toString();
        GadgetBean gadgetBean = new GadgetBean();
        gadgetBean.setUrl(gadgetUrlToSet);
        GadgetsBean gadgetsBeanToSet = new GadgetsBean(Collections.singletonList(gadgetBean));

        GadgetSpec gadgetSpec = GadgetSpec.gadgetSpec(externalGadgetSpec.getSpecUri()).build();

        PowerMock.mockStatic(AuthenticatedUserThreadLocal.class);
        expect(AuthenticatedUserThreadLocal.get()).andReturn(user);
        PowerMock.replay(AuthenticatedUserThreadLocal.class);

        doReturn(Locale.GERMAN).when(localeManager).getLocale(user);
        doReturn(gadgetSpec).when(gadgetSpecFactory).getGadgetSpec(externalGadgetSpec.getSpecUri(), null);

        GadgetsBean gadgetsBean = gadgetsService.setGadgets(gadgetsBeanToSet);
        assertEquals(gadgetUrlToSet, gadgetsBean.getGadgets().iterator().next().getUrl());
    }

    private ExternalGadgetSpec createExternalGadgetSpec() throws URISyntaxException {
        ExternalGadgetSpecId id = ExternalGadgetSpecId.valueOf(UUID.randomUUID().toString());
        return new ExternalGadgetSpec(id, new URI("http://localhost"));
    }

    private ConfluenceUser createConfluenceUser() throws IllegalAccessException {
        ConfluenceUser user = new ConfluenceUserImpl("test", "test test", "test@test.de");
        FieldUtils.writeDeclaredField(user, "key", new UserKey(UUID.randomUUID().toString()), true);
        return user;
    }

}
