/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker.web.endpoint.pages;

import com.mablocker.web.MaBlockerWebLauncher;
import com.mablocker.web.util.Mime;
import org.glassfish.jersey.server.mvc.Template;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Path("/apply")
public class ApplyBlocklistPage {

    @GET
    @Path("/status")
    @Template(name = "/blocklist-apply.ftl")
    public Map<String, Object> editBlocklist() {
        Map<String, Object> model = new HashMap<>();
        model.put("unappliedChanges", MaBlockerWebLauncher.getBlockingConfiguration().isUnappliedChanges());
        model.put("state", MaBlockerWebLauncher.getRouterRulesManager().getState().name());
        model.put("log", MaBlockerWebLauncher.getRouterRulesManager().getLog());
        return model;
    }

    @POST
    @Consumes(Mime.HTML_FORM)
    public void applyBlocklists() {
        if(!MaBlockerWebLauncher.getBlockingConfiguration().isUnappliedChanges()) {
            BlocklistsPage.Messages.blocklists_already_updated.redirect();
        }
        MaBlockerWebLauncher.getRouterRulesManager().apply();

        throw new RedirectionException(Response.Status.SEE_OTHER, URI.create("/apply/status"));
    }
}
