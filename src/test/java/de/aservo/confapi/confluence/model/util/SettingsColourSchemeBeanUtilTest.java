package de.aservo.confapi.confluence.model.util;

import com.atlassian.confluence.themes.BaseColourScheme;
import com.atlassian.confluence.themes.ColourScheme;
import de.aservo.confapi.commons.model.SettingsBrandingColorSchemeBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static de.aservo.confapi.confluence.model.util.SettingsBrandingColorSchemeBeanUtil.toGlobalColorScheme;
import static de.aservo.confapi.confluence.model.util.SettingsBrandingColorSchemeBeanUtil.toSettingsBrandingColorSchemeBean;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SettingsColourSchemeBeanUtilTest {

    @Test
    public void testToGlobalColorScheme() {

        SettingsBrandingColorSchemeBean schemeBean = SettingsBrandingColorSchemeBean.EXAMPLE_1;
        BaseColourScheme baseColourScheme = toGlobalColorScheme(schemeBean, false, null);

        assertNotNull(baseColourScheme);
        assertEquals(schemeBean.getTopBar(), baseColourScheme.get(ColourScheme.TOP_BAR));
        assertEquals(schemeBean.getHeaderButtonText(), baseColourScheme.get(ColourScheme.HEADER_BUTTON_TEXT));
        assertEquals(schemeBean.getBordersAndDividers(), baseColourScheme.get(ColourScheme.BORDER));
    }

    @Test
    public void testToGlobalColorSchemeSetNullValues() {

        SettingsBrandingColorSchemeBean schemeBean = new SettingsBrandingColorSchemeBean();
        BaseColourScheme baseColourScheme = toGlobalColorScheme(schemeBean, true, null);

        assertNotNull(baseColourScheme);
        assertNull(baseColourScheme.get(ColourScheme.TOP_BAR));
        assertNull(baseColourScheme.get(ColourScheme.HEADER_BUTTON_TEXT));
        assertNull(baseColourScheme.get(ColourScheme.BORDER));
    }

    @Test
    public void testToSettingsBrandingColorSchemeBean() {

        final BaseColourScheme colourScheme = getDummyBaseColourScheme();

        SettingsBrandingColorSchemeBean schemeBean = toSettingsBrandingColorSchemeBean(colourScheme);

        assertNotNull(schemeBean);
        assertEquals(colourScheme.get(ColourScheme.TOP_BAR), schemeBean.getTopBar());
        assertEquals(colourScheme.get(ColourScheme.HEADER_BUTTON_TEXT), schemeBean.getHeaderButtonText());
        assertEquals(colourScheme.get(ColourScheme.BORDER), schemeBean.getBordersAndDividers());
    }

    public static BaseColourScheme getDummyBaseColourScheme() {
        final BaseColourScheme colourScheme = new BaseColourScheme();
        colourScheme.set(ColourScheme.TOP_BAR, "#0049B0");
        colourScheme.set(ColourScheme.HEADER_BUTTON_TEXT, "#0049B0");
        colourScheme.set(ColourScheme.BORDER, "#0049B0");
        return colourScheme;
    }
}
