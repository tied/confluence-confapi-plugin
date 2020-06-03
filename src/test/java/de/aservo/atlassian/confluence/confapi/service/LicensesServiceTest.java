package de.aservo.atlassian.confluence.confapi.service;

import com.atlassian.confluence.settings.setup.DefaultSingleProductLicenseDetailsView;
import com.atlassian.sal.api.i18n.InvalidOperationException;
import com.atlassian.sal.api.license.LicenseHandler;
import de.aservo.confapi.commons.exception.BadRequestException;
import de.aservo.confapi.commons.exception.InternalServerErrorException;
import de.aservo.confapi.commons.model.LicenseBean;
import de.aservo.confapi.commons.model.LicensesBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.atlassian.confluence.setup.ConfluenceBootstrapConstants.DEFAULT_LICENSE_REGISTRY_KEY;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@RunWith(MockitoJUnitRunner.class)
public class LicensesServiceTest {

    @Mock
    private LicenseHandler licenseHandler;

    private LicensesServiceImpl licenseService;

    @Before
    public void setup() {
        licenseService = new LicensesServiceImpl(licenseHandler);
    }

    @Test
    public void testGetLicense() {
        DefaultSingleProductLicenseDetailsView testLicense = new DefaultSingleProductLicenseDetailsView(LicenseBean.EXAMPLE_1);
        doReturn(testLicense).when(licenseHandler).getProductLicenseDetails(DEFAULT_LICENSE_REGISTRY_KEY);

        LicensesBean licenses = licenseService.getLicenses();

        assertEquals(testLicense.getDescription(), licenses.getLicenses().iterator().next().getDescription());
    }

    @Test(expected = InternalServerErrorException.class)
    public void testSetLicensesWithError() throws InvalidOperationException {
        LicensesBean licensesBean = LicensesBean.EXAMPLE_1;
        DefaultSingleProductLicenseDetailsView testLicense = new DefaultSingleProductLicenseDetailsView(licensesBean.getLicenses().iterator().next());
        doReturn(true).when(licenseHandler).hostAllowsMultipleLicenses();
        doReturn(testLicense).when(licenseHandler).getProductLicenseDetails(DEFAULT_LICENSE_REGISTRY_KEY);
        doThrow(new InvalidOperationException("", "")).when(licenseHandler).removeProductLicense(any(String.class));

        licenseService.setLicenses(licensesBean);
    }

    @Test
    public void testSetLicenses() {
        LicensesBean licensesBean = LicensesBean.EXAMPLE_1;
        DefaultSingleProductLicenseDetailsView testLicense = new DefaultSingleProductLicenseDetailsView(licensesBean.getLicenses().iterator().next());
        doReturn(false).when(licenseHandler).hostAllowsMultipleLicenses();
        doReturn(testLicense).when(licenseHandler).getProductLicenseDetails(DEFAULT_LICENSE_REGISTRY_KEY);

        LicensesBean updatedLicensesBean = licenseService.setLicenses(licensesBean);

        assertEquals(testLicense.getDescription(), updatedLicensesBean.getLicenses().iterator().next().getDescription());
    }

    @Test
    public void testSetLicense() {
        LicenseBean licenseBean = LicenseBean.EXAMPLE_1;
        DefaultSingleProductLicenseDetailsView testLicense = new DefaultSingleProductLicenseDetailsView(licenseBean);
        doReturn(false).when(licenseHandler).hostAllowsMultipleLicenses();
        doReturn(testLicense).when(licenseHandler).getProductLicenseDetails(DEFAULT_LICENSE_REGISTRY_KEY);

        LicensesBean updatedLicensesBean = licenseService.setLicense(licenseBean);

        assertEquals(testLicense.getDescription(), updatedLicensesBean.getLicenses().iterator().next().getDescription());
    }

    @Test(expected = BadRequestException.class)
    public void testSetLicenseWithError() throws InvalidOperationException {
        LicenseBean licenseBean = LicenseBean.EXAMPLE_1;
        DefaultSingleProductLicenseDetailsView testLicense = new DefaultSingleProductLicenseDetailsView(licenseBean);
        doReturn(false).when(licenseHandler).hostAllowsMultipleLicenses();
        doReturn(testLicense).when(licenseHandler).getProductLicenseDetails(DEFAULT_LICENSE_REGISTRY_KEY);
        doThrow(new InvalidOperationException("", "")).when(licenseHandler).addProductLicense(any(String.class), any(String.class));

        licenseService.setLicense(licenseBean);
    }
}
