/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker.web.endpoint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/")
public class RedirectEndpoint {
    @GET
    public Response getRoot() {
        return Response.temporaryRedirect(URI.create("/blocklists")).build();
    }

    @GET
    @Path("favicon.ico")
    public Response getFavicon() {
        return Response.temporaryRedirect(URI.create("/static/favicon.ico")).build();
    }
}
