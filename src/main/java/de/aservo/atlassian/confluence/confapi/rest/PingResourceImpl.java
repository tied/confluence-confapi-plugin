package de.aservo.atlassian.confluence.confapi.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import de.aservo.confapi.commons.constants.ConfAPI;
import de.aservo.confapi.commons.rest.AbstractPingResourceImpl;

import javax.ws.rs.Path;

@Path(ConfAPI.PING)
@AnonymousAllowed
public class PingResourceImpl extends AbstractPingResourceImpl {

    // Completely inhering the implementation of AbstractPingResourceImpl

}
