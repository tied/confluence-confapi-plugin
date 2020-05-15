package de.aservo.atlassian.confluence.confapi.model.type;

import de.aservo.atlassian.confluence.confapi.model.DefaultAuthenticationScenario;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DefaultAuthenticationScenarioTest {

    @Test
    public void testDefaultReturnValues() {
        DefaultAuthenticationScenario scenario = new DefaultAuthenticationScenario();
        assertTrue(scenario.isCommonUserBase());
        assertTrue(scenario.isTrusted());
    }

}
