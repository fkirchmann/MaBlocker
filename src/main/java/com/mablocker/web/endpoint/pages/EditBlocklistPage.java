/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker.web.endpoint.pages;

import com.mablocker.Blocklist;
import com.mablocker.web.util.Mime;
import org.glassfish.jersey.server.mvc.Template;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.IDN;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Path("/blocklist/{id}/edit")
public class EditBlocklistPage {

    @GET
    @Template(name = "/blocklist-edit.ftl")
    public Map<String, Object> editBlocklist(@PathParam("id") long blocklistId,
                                             @QueryParam("deleted") String deleted) {
        Map<String, Object> model = new HashMap<>();

        Blocklist blocklist = BlocklistsPage.getBlocklistById(blocklistId);

        model.put("idnDecoder", (Function<String, String>) IDN::toUnicode);
        model.put("blocklist", blocklist);
        model.put("deleted", deleted);
        return model;
    }

    @POST
    @Consumes(Mime.HTML_FORM)
    public void editBlocklistDoIt(@PathParam("id") long blocklistId,
                                    @FormParam("delete") String hostToDelete) {
        Blocklist blocklist = BlocklistsPage.getBlocklistById(blocklistId);
        try {
            blocklist.removeHost(hostToDelete);
            throw new RedirectionException(Response.Status.SEE_OTHER,
                    URI.create("/blocklist/" + blocklist.getId() + "/edit?deleted=" + hostToDelete));
        } catch (IllegalArgumentException e) {}
    }
}
