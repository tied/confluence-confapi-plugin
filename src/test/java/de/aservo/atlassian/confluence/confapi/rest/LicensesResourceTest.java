package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.sal.api.i18n.InvalidOperationException;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import de.aservo.atlassian.confapi.model.ErrorCollection;
import de.aservo.atlassian.confapi.model.LicensesBean;
import de.aservo.atlassian.confluence.confapi.model.util.LicenseBeanUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static com.atlassian.confluence.setup.ConfluenceBootstrapConstants.DEFAULT_LICENSE_REGISTRY_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LicensesResourceTest {

    private LicenseHandler licenseHandler;
    private LicencesResourceImpl resource;

    @Before
    public void setup() {
        licenseHandler = mock(LicenseHandler.class);
        resource = new LicencesResourceImpl(licenseHandler);
    }

    @Test
    public void testGetLicense() {
        SingleProductLicenseDetailsView view = mock(SingleProductLicenseDetailsView.class);

        doReturn(view).when(licenseHandler).getProductLicenseDetails(DEFAULT_LICENSE_REGISTRY_KEY);

        final Response response = resource.getLicenses();
        assertEquals(200, response.getStatus());
        final LicensesBean licensesBean = (LicensesBean) response.getEntity();

        assertEquals(licensesBean.getLicenses().iterator().next(), LicenseBeanUtil.toLicenseBean(view));
    }

    @Test
    public void testGetLicenseWithError() {
        doThrow(new RuntimeException()).when(licenseHandler).getProductLicenseDetails(DEFAULT_LICENSE_REGISTRY_KEY);

        final Response response = resource.getLicenses();
        assertEquals(400, response.getStatus());

        assertNotNull(response.getEntity());
        assertEquals(ErrorCollection.class, response.getEntity().getClass());
    }

    @Test
    public void testSetLicense() {
        SingleProductLicenseDetailsView view = mock(SingleProductLicenseDetailsView.class);

        doReturn(view).when(licenseHandler).getProductLicenseDetails(DEFAULT_LICENSE_REGISTRY_KEY);

        final Response response = resource.setLicense(false, "ABCDEFG");
        assertEquals(200, response.getStatus());
    }

    @Test
    public void testSetLicenseWithError() throws InvalidOperationException {
        doThrow(new RuntimeException()).when(licenseHandler).addProductLicense(any(String.class), any(String.class));

        final Response response = resource.setLicense(false,"ABCDEFG");
        assertEquals(400, response.getStatus());

        assertNotNull(response.getEntity());
        assertEquals(ErrorCollection.class, response.getEntity().getClass());
    }
}
