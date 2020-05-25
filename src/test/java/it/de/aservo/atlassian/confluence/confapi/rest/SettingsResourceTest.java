package it.de.aservo.atlassian.confluence.confapi.rest;

import de.aservo.atlassian.confapi.model.SettingsBean;
import it.de.aservo.atlassian.confapi.rest.AbstractSettingsResourceFuncTest;

public class SettingsResourceTest extends AbstractSettingsResourceFuncTest {

    @Override
    protected SettingsBean getExampleBean() {
        return SettingsBean.EXAMPLE_1_NO_MODE;
    }
}
