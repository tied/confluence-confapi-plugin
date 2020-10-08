package de.aservo.confapi.confluence.service;

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
import de.aservo.confapi.commons.exception.NotFoundException;
import de.aservo.confapi.commons.model.GadgetBean;
import de.aservo.confapi.commons.model.GadgetsBean;
import de.aservo.confapi.commons.service.api.GadgetsService;
import org.apache.commons.lang.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

//power mockito required here for mocking static methods of AuthenticatedUserThreadLocal
@RunWith(PowerMockRunner.class)
@PrepareForTest(AuthenticatedUserThreadLocal.class)
public class GadgetsServiceTest {

    @Mock
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

        assertEquals(externalGadgetSpec.getSpecUri(), gadgetsBean.getGadgets().iterator().next().getUrl());
    }

    @Test
    public void testGetGadget() throws URISyntaxException {
        ExternalGadgetSpec externalGadgetSpec = createExternalGadgetSpec();
        doReturn(Collections.singletonList(externalGadgetSpec)).when(externalGadgetSpecStore).entries();

        GadgetBean gadgetBean = gadgetsService.getGadget(1L);

        assertEquals(externalGadgetSpec.getSpecUri(), gadgetBean.getUrl());
    }

    @Test(expected = NotFoundException.class)
    public void testGetGadgetIdNotExisting() {
        gadgetsService.getGadget(0L);
    }

    @Test
    public void testAddGadget() throws URISyntaxException, IllegalAccessException {
        ExternalGadgetSpec externalGadgetSpec = createExternalGadgetSpec();
        doReturn(Collections.singletonList(externalGadgetSpec)).when(externalGadgetSpecStore).entries();
        doReturn(externalGadgetSpec).when(externalGadgetSpecStore).add(any());

        ConfluenceUser user = createConfluenceUser();
        GadgetBean gadgetBean = new GadgetBean();
        gadgetBean.setUrl(externalGadgetSpec.getSpecUri());

        GadgetSpec gadgetSpec = GadgetSpec.gadgetSpec(externalGadgetSpec.getSpecUri()).build();

        PowerMock.mockStatic(AuthenticatedUserThreadLocal.class);
        expect(AuthenticatedUserThreadLocal.get()).andReturn(user);
        PowerMock.replay(AuthenticatedUserThreadLocal.class);

        doReturn(Locale.GERMAN).when(localeManager).getLocale(user);
        doReturn(gadgetSpec).when(gadgetSpecFactory).getGadgetSpec(externalGadgetSpec.getSpecUri(), null);

        GadgetBean gadgetsBean = gadgetsService.addGadget(gadgetBean);
        assertEquals(externalGadgetSpec.getSpecUri(), gadgetsBean.getUrl());
    }

    @Test
    public void testSetGadgets() throws URISyntaxException, IllegalAccessException {
        ExternalGadgetSpec externalGadgetSpec = createExternalGadgetSpec();
        doReturn(Collections.singletonList(externalGadgetSpec)).when(externalGadgetSpecStore).entries();
        doReturn(externalGadgetSpec).when(externalGadgetSpecStore).add(any());

        ConfluenceUser user = createConfluenceUser();
        GadgetBean gadgetBean = new GadgetBean();
        gadgetBean.setUrl(externalGadgetSpec.getSpecUri());
        GadgetsBean gadgetsBeanToSet = new GadgetsBean(Collections.singletonList(gadgetBean));

        GadgetSpec gadgetSpec = GadgetSpec.gadgetSpec(externalGadgetSpec.getSpecUri()).build();

        PowerMock.mockStatic(AuthenticatedUserThreadLocal.class);
        expect(AuthenticatedUserThreadLocal.get()).andReturn(user);
        PowerMock.replay(AuthenticatedUserThreadLocal.class);

        doReturn(Locale.GERMAN).when(localeManager).getLocale(user);
        doReturn(gadgetSpec).when(gadgetSpecFactory).getGadgetSpec(externalGadgetSpec.getSpecUri(), null);

        GadgetsBean gadgetsBean = gadgetsService.setGadgets(gadgetsBeanToSet);
        assertEquals(externalGadgetSpec.getSpecUri(), gadgetsBean.getGadgets().iterator().next().getUrl());
    }

    @Test
    public void testSetGadget() throws URISyntaxException, IllegalAccessException {
        ExternalGadgetSpec externalGadgetSpec = createExternalGadgetSpec();
        doReturn(Collections.singletonList(externalGadgetSpec)).when(externalGadgetSpecStore).entries();
        doReturn(externalGadgetSpec).when(externalGadgetSpecStore).add(any());

        ConfluenceUser user = createConfluenceUser();
        GadgetBean gadgetBean = new GadgetBean();
        gadgetBean.setId(1L);
        gadgetBean.setUrl(externalGadgetSpec.getSpecUri());
        GadgetSpec gadgetSpec = GadgetSpec.gadgetSpec(externalGadgetSpec.getSpecUri()).build();

        PowerMock.mockStatic(AuthenticatedUserThreadLocal.class);
        expect(AuthenticatedUserThreadLocal.get()).andReturn(user);
        PowerMock.replay(AuthenticatedUserThreadLocal.class);

        doReturn(Locale.GERMAN).when(localeManager).getLocale(user);
        doReturn(gadgetSpec).when(gadgetSpecFactory).getGadgetSpec(externalGadgetSpec.getSpecUri(), null);

        GadgetBean responseGadgetBean = gadgetsService.setGadget(1L, gadgetBean);
        assertEquals(externalGadgetSpec.getSpecUri(), responseGadgetBean.getUrl());
    }

    @Test
    public void testDeleteGadgets() {
        gadgetsService.deleteGadgets(true);
        assertTrue("Delete Successful", true);
    }

    @Test(expected = BadRequestException.class)
    public void testDeleteGadgetsWithoutForceParameter() {
        gadgetsService.deleteGadgets(false);
    }

    @Test
    public void testDeleteGadget() throws URISyntaxException {
        ExternalGadgetSpec externalGadgetSpec = createExternalGadgetSpec();
        doReturn(Collections.singletonList(externalGadgetSpec)).when(externalGadgetSpecStore).entries();

        gadgetsService.deleteGadget(1L);
        assertTrue("Delete Successful", true);
    }

    @Test(expected = NotFoundException.class)
    public void testDeleteGadgetsIdNotExisting() {
        gadgetsService.deleteGadget(0L);
    }

    private ExternalGadgetSpec createExternalGadgetSpec() throws URISyntaxException {
        ExternalGadgetSpecId id = ExternalGadgetSpecId.valueOf("1");
        return new ExternalGadgetSpec(id, new URI("http://localhost"));
    }

    private ConfluenceUser createConfluenceUser() throws IllegalAccessException {
        ConfluenceUser user = new ConfluenceUserImpl("test", "test test", "test@test.de");
        FieldUtils.writeDeclaredField(user, "key", new UserKey(UUID.randomUUID().toString()), true);
        return user;
    }

}
