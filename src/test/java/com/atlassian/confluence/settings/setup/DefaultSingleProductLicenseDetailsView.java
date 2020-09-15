package com.atlassian.confluence.settings.setup;

import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import de.aservo.confapi.commons.model.LicenseBean;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

public class DefaultSingleProductLicenseDetailsView implements SingleProductLicenseDetailsView {

    private LicenseBean licenseBean;

    public DefaultSingleProductLicenseDetailsView(LicenseBean licenseBean) {
        this.licenseBean = licenseBean;
    }

    @Override
    public boolean isEvaluationLicense() {
        return false;
    }

    @Nonnull
    @Override
    public String getLicenseTypeName() {
        return licenseBean.getType();
    }

    @Override
    public String getOrganisationName() {
        return licenseBean.getOrganization();
    }

    @Nullable
    @Override
    public String getSupportEntitlementNumber() {
        return null;
    }

    @Override
    public String getDescription() {
        return licenseBean.getDescription();
    }

    @Override
    public String getServerId() {
        return null;
    }

    @Override
    public boolean isPerpetualLicense() {
        return false;
    }

    @Nullable
    @Override
    public Date getLicenseExpiryDate() {
        return licenseBean.getExpiryDate();
    }

    @Nullable
    @Override
    public Date getMaintenanceExpiryDate() {
        return null;
    }

    @Override
    public boolean isDataCenter() {
        return false;
    }

    @Override
    public boolean isEnterpriseLicensingAgreement() {
        return false;
    }

    @Nonnull
    @Override
    public String getProductKey() {
        return null;
    }

    @Override
    public boolean isUnlimitedNumberOfUsers() {
        return false;
    }

    @Override
    public int getNumberOfUsers() {
        return licenseBean.getMaxUsers();
    }

    @Nonnull
    @Override
    public String getProductDisplayName() {
        return licenseBean.getProducts().iterator().next();
    }

    @Nullable
    @Override
    public String getProperty(@Nonnull String property) {
        return null;
    }
}
