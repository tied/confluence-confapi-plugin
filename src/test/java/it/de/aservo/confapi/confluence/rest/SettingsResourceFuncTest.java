package it.de.aservo.confapi.confluence.rest;

import de.aservo.confapi.commons.model.SettingsBean;
import it.de.aservo.confapi.commons.rest.AbstractSettingsResourceFuncTest;

public class SettingsResourceFuncTest extends AbstractSettingsResourceFuncTest {

    @Override
    protected SettingsBean getExampleBean() {
        return SettingsBean.EXAMPLE_1_NO_MODE;
    }
}
