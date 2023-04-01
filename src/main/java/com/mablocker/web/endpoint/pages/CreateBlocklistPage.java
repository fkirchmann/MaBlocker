/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker.web.endpoint.pages;

import com.mablocker.web.util.Mime;
import com.mablocker.web.MaBlockerWebLauncher;
import lombok.Getter;
import org.glassfish.jersey.server.mvc.Template;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.*;

@Path("/blocklists/create")
public class CreateBlocklistPage {
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

        void redirect() {
            throw new RedirectionException(Response.Status.SEE_OTHER, URI.create("/blocklists/create?message=" + this.name()));
        }
    }

    @GET
    @Template(name = "/blocklist-create.ftl")
    public Map<String, Object> createBlocklist(@QueryParam("message") String messageName) {
        Map<String, Object> model = new HashMap<>();

        if(messageName != null) { model.put("message", Messages.get(messageName)); }

        return model;
    }

    @POST
    @Consumes(Mime.HTML_FORM)
    public void createBlocklistDoIt(@FormParam("blocklist-name") String blocklistName) {
        if(blocklistName == null || blocklistName.trim().isEmpty()) { Messages.empty_name.redirect(); }
        try {
            MaBlockerWebLauncher.getBlockingConfiguration().addBlocklist(blocklistName);
            BlocklistsPage.Messages.blocklist_created.redirect();
        } catch(IllegalArgumentException e) {
            Messages.exists.redirect();
        }
    }
}
