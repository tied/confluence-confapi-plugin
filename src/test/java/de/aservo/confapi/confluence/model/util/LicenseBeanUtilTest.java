package de.aservo.confapi.confluence.model.util;

import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import de.aservo.confapi.commons.model.LicenseBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class LicenseBeanUtilTest {

    @Test
    public void testToLicenseBean() {
        final SingleProductLicenseDetailsView license = mock(SingleProductLicenseDetailsView.class);
        final LicenseBean bean = LicenseBeanUtil.toLicenseBean(license);

        assertNotNull(bean);
        assertEquals(bean.getProducts().iterator().next(), license.getProductDisplayName());
        assertEquals(bean.getOrganization(), license.getOrganisationName());
        assertEquals(bean.getType(), license.getLicenseTypeName());
        assertEquals(bean.getDescription(), license.getDescription());
        assertEquals(bean.getExpiryDate(), license.getMaintenanceExpiryDate());
        assertEquals(bean.getMaxUsers(), license.getNumberOfUsers());
    }

}
