package de.aservo.atlassian.confluence.confapi.model.util;

import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import de.aservo.confapi.commons.model.LicenseBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

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
        assertEquals(bean.getLicenseType(), license.getLicenseTypeName());
        assertEquals(bean.getDescription(), license.getDescription());
        assertEquals(bean.getExpiryDate(), license.getMaintenanceExpiryDate());
        assertEquals(bean.getNumUsers(), license.getNumberOfUsers());
    }

}
