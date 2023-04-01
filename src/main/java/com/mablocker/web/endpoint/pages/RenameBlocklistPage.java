/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker.web.endpoint.pages;

import com.mablocker.Blocklist;
import com.mablocker.web.util.Mime;
import lombok.Getter;
import org.glassfish.jersey.server.mvc.Template;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Path("/blocklist/{id}/rename")
public class RenameBlocklistPage {
    public enum Messages {
        exists("danger", "A blocklist with this name already exists!"),
        empty_name("warning", "Enter a name for the blocklist and press ze green button"),
        unspecified("danger", "An unknown error occurred.");

        @Getter
        public final String cssClass, text;

        Messages(String cssClass, String text) {
            this.cssClass = cssClass;
            this.text = text;
        }

        static Messages get(String name) {
            return Arrays.stream(Messages.values())
                    .filter(msg -> msg.name().equals(name))
                    .findFirst()
                    .orElse(unspecified);
        }

        void redirect(long id) {
            throw new RedirectionException(Response.Status.SEE_OTHER,
                    URI.create("/blocklist/" + id + "/rename?message=" + this.name()));
        }
    }

    @GET
    @Template(name = "/blocklist-rename.ftl")
    public Map<String, Object> renameBlocklist(@PathParam("id") long blocklistId,
                                               @QueryParam("message") String messageName) {
        Map<String, Object> model = new HashMap<>();

        Blocklist blocklist = BlocklistsPage.getBlocklistById(blocklistId);

        if(messageName != null) { model.put("message", Messages.get(messageName)); }

        model.put("blocklist", blocklist);
        return model;
    }

    @POST
    @Consumes(Mime.HTML_FORM)
    public void renameBlocklistDoIt(@PathParam("id") long blocklistId,
                                    @FormParam("new-name") String newName) {
        Blocklist blocklist = BlocklistsPage.getBlocklistById(blocklistId);
        if(newName == null || newName.trim().isEmpty()) { Messages.empty_name.redirect(blocklistId); }
        try {
            blocklist.setName(newName);
            BlocklistsPage.Messages.blocklist_renamed.redirect();
        } catch (IllegalArgumentException e) {
            Messages.exists.redirect(blocklistId);
        }
    }
}
