package de.aservo.confapi.confluence.util;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.filters.ServletContextThreadLocal;
import de.aservo.confapi.commons.exception.InternalServerErrorException;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class HttpUtil {

    public static final String SERVLET_CONTEXT_INIT_PARAM_EXPORT_TASK = "unsupportedContainersForExportLongRunningTask";

    public static ConfluenceUser getUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    public static HttpServletRequest getServletRequest() {
        return ServletContextThreadLocal.getRequest();
    }

    public static ServletContext getServletContext() {
        return ServletContextThreadLocal.getContext();
    }

    /**
     * Determine the base URL from a static (request) context (without Confluence API like ApplicationProperties).
     *
     * @return base URL
     */
    public static URI getBaseUrl() {
        final StringBuffer requestURL = getServletRequest().getRequestURL();
        final StringBuilder pathBuilder = new StringBuilder();
        int baseUrlIndex = requestURL.length();

        if (StringUtils.isNotBlank(getServletRequest().getServletPath())) {
            pathBuilder.append(getServletRequest().getServletPath());

            if (StringUtils.isNotBlank(getServletRequest().getPathInfo())) {
                pathBuilder.append(getServletRequest().getPathInfo());
            }

            baseUrlIndex = requestURL.lastIndexOf(pathBuilder.toString());
        }

        return URI.create(requestURL.substring(0, baseUrlIndex));
    }

    public static URI getRestUrl() {
        final String servletPath = getServletRequest().getServletPath(); // should always be '/rest'

        if (servletPath == null || !servletPath.equals("/rest")) {
            throw new InternalServerErrorException("Tried to get REST URL from a non-REST context");
        }

        final String pathInfo = getServletRequest().getPathInfo(); // should look like '/confapi/1/endpoint/method'
        String[] splitPathInfo = pathInfo.split("/"); // should always have length >= 3 or even > 3

        final UriBuilder uriBuilder = UriBuilder.fromUri(getBaseUrl());
        uriBuilder.path(servletPath);
        uriBuilder.path(splitPathInfo[1]); // e.g. 'confapi'
        uriBuilder.path(splitPathInfo[2]); // e.g. '1' or 'latest'

        return uriBuilder.build();
    }

    /**
     * Create an URI that is based on the request's base URL.
     *
     * E.g. http://localhost:1990/confluence/servlet/example has been called.
     *
     * If "download", "file.zip" are passed as paths, the new URI will look like this:
     *
     * http://localhost:1990/confluence/download/file.zip
     *
     * @param paths the paths
     * @return base URL with new paths
     */
    public static URI createUri(
            @Nonnull final String ... paths) {

        final UriBuilder uriBuilder = UriBuilder.fromUri(getBaseUrl());
        uriBuilder.path(String.join("/", paths));
        return uriBuilder.build();
    }

    /**
     * Create a REST URI that is based on the initial REST request.
     *
     * E.g. http://localhost:1990/confluence/rest/confapi/1/settings has been called.
     *
     * If only "mail-server" is passed as paths, the new URI will look like this:
     *
     * http://localhost:1990/confluence/rest/confapi/1/mail-server
     *
     * @param paths the paths
     * @return REST URL with new paths
     */
    public static URI createRestUri(
            @Nonnull final String ... paths) {

        final UriBuilder uriBuilder = UriBuilder.fromUri(getRestUrl());
        uriBuilder.path(String.join("/", paths));
        return uriBuilder.build();
    }

    public static boolean isLongRunningTaskSupported() {
        final String serverInfo = getServletContext().getServerInfo().toLowerCase();
        final String unsupportedContainers = getServletContext()
                .getInitParameter(SERVLET_CONTEXT_INIT_PARAM_EXPORT_TASK);

        if (StringUtils.isNotBlank(unsupportedContainers)) {
            for (String unsupportedContainer : unsupportedContainers.split(",")) {
                if (serverInfo.contains(unsupportedContainer.trim())) {
                    return false;
                }
            }
        }

        return true;
    }

    private HttpUtil() {}

}
