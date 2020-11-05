package de.aservo.confapi.confluence.service;

import com.atlassian.confluence.plugins.lookandfeel.SiteLogo;
import com.atlassian.confluence.plugins.lookandfeel.SiteLogoManager;
import com.atlassian.confluence.themes.BaseColourScheme;
import com.atlassian.confluence.themes.ColourSchemeManager;
import com.atlassian.favicon.core.FaviconManager;
import com.atlassian.favicon.core.ImageType;
import com.atlassian.favicon.core.StoredFavicon;
import com.atlassian.favicon.core.exceptions.ImageStorageException;
import com.atlassian.favicon.core.exceptions.InvalidImageDataException;
import com.atlassian.favicon.core.exceptions.UnsupportedImageTypeException;
import de.aservo.confapi.commons.exception.BadRequestException;
import de.aservo.confapi.commons.model.SettingsBrandingColorSchemeBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static de.aservo.confapi.confluence.model.util.SettingsBrandingColorSchemeBeanUtil.toGlobalColorScheme;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ImageType.class)
public class SettingsBrandingServiceTest {

    private ColourSchemeManager colourSchemeManager;
    private FaviconManager faviconManager;
    private SiteLogoManager siteLogoManager;
    private SettingsBrandingServiceImpl settingsBrandingService;

    @Before
    public void setup() {
        //when using powermock we cannot initialize with @Mock or @InjectMocks unfortunately
        colourSchemeManager = mock(ColourSchemeManager.class);
        siteLogoManager = mock(SiteLogoManager.class);
        faviconManager = mock(FaviconManager.class);
        settingsBrandingService = new SettingsBrandingServiceImpl(colourSchemeManager, siteLogoManager, faviconManager);
    }

    @Test
    public void testGetColourScheme() {

        BaseColourScheme dummyBaseColourScheme = toGlobalColorScheme(SettingsBrandingColorSchemeBean.EXAMPLE_1, false, null);
        doReturn(dummyBaseColourScheme).when(colourSchemeManager).getGlobalColourScheme();

        SettingsBrandingColorSchemeBean colourScheme = settingsBrandingService.getColourScheme();

        assertEquals(SettingsBrandingColorSchemeBean.EXAMPLE_1.getTopBar(), colourScheme.getTopBar());
    }

    @Test
    public void testSetColourScheme() {

        SettingsBrandingColorSchemeBean schemeBean = SettingsBrandingColorSchemeBean.EXAMPLE_1;
        BaseColourScheme dummyBaseColourScheme = toGlobalColorScheme(schemeBean, false,null);
        doReturn(dummyBaseColourScheme).when(colourSchemeManager).getGlobalColourScheme();

        SettingsBrandingColorSchemeBean colourScheme = settingsBrandingService.setColourScheme(schemeBean);
        verify(colourSchemeManager).saveGlobalColourScheme(any());

        assertEquals(schemeBean.getTopBar(), colourScheme.getTopBar());
    }

    @Test
    public void testGetLogo() {

        InputStream is = new ByteArrayInputStream("".getBytes());
        SiteLogo siteLogo = new SiteLogo("", is);
        doReturn(siteLogo).when(siteLogoManager).getCurrent();

        InputStream logoImage = settingsBrandingService.getLogo();

        assertNotNull(logoImage);
    }

    @Test
    public void testSetLogo() throws IOException {

        InputStream is = new ByteArrayInputStream("".getBytes());
        settingsBrandingService.setLogo(is);

        verify(siteLogoManager).uploadLogo(any(), any());
    }

    @Test
    public void testGetFavicon() {

        InputStream is = new ByteArrayInputStream("".getBytes());
        StoredFavicon storedFavicon = new StoredFavicon(is, "img/png", 100);
        doReturn(true).when(faviconManager).isFaviconConfigured();
        doReturn(Optional.of(storedFavicon)).when(faviconManager).getFavicon(any(), any());

        InputStream favImage = settingsBrandingService.getFavicon();

        assertNotNull(favImage);
    }

    @Test
    public void testSetFavicon() throws InvalidImageDataException, UnsupportedImageTypeException, ImageStorageException {

        InputStream is = new ByteArrayInputStream("".getBytes());

        PowerMock.mockStatic(ImageType.class);
        expect(ImageType.parseFromContentType("content/unknown")).andReturn(Optional.of(ImageType.PNG));
        PowerMock.replay(ImageType.class);

        settingsBrandingService.setFavicon(is);

        verify(faviconManager).setFavicon(any());
    }

    @Test(expected = BadRequestException.class)
    public void testSetFaviconNoParseableImageType() {

        InputStream is = new ByteArrayInputStream("".getBytes());
        settingsBrandingService.setFavicon(is);
    }

}
