package de.aservo.confapi.confluence.model.util;

import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import de.aservo.confapi.commons.model.LicenseBean;

import javax.annotation.Nonnull;
import java.util.Collections;

public class LicenseBeanUtil {

    /**
     * Instantiates a new License bean.
     *
     * @param productLicense the product license
     */
    @Nonnull
    public static LicenseBean toLicenseBean(
            @Nonnull final SingleProductLicenseDetailsView productLicense) {

        final LicenseBean licenseBean = new LicenseBean();
        licenseBean.setProducts(Collections.singletonList(productLicense.getProductDisplayName()));
        licenseBean.setType(productLicense.getLicenseTypeName());
        licenseBean.setOrganization(productLicense.getOrganisationName());
        licenseBean.setDescription(productLicense.getDescription());
        licenseBean.setExpiryDate(productLicense.getMaintenanceExpiryDate());
        licenseBean.setMaxUsers(productLicense.getNumberOfUsers());
        return licenseBean;
    }

    private LicenseBeanUtil() {
    }

}
