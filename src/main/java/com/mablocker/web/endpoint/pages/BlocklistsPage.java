/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker.web.endpoint.pages;

import com.mablocker.Blocklist;
import com.mablocker.web.util.Mime;
import com.mablocker.web.MaBlockerWebLauncher;
import lombok.Getter;
import org.glassfish.jersey.server.mvc.Template;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.IDN;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Path("/")
public class BlocklistsPage {
    public enum Messages {
        no_hosts("warning", "Please enter one or more websites in the textbox, then click the green plus button."),
        no_new_hosts("danger", "All websites you entered are already in the list."),
        blocklist_not_found("danger", "This blocklist couldn't be found."),
        hosts_added("success", "The websites were successfully added to the blocklist."),
        blocklist_created("success", "The blocklist was created successfully."),
        blocklist_renamed("success", "The blocklist was renamed successfully."),
        blocklist_deleted("success", "The blocklist was deleted successfully."),
        blocklists_already_updated("success", "The current blocklists have already been applied."),
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
            throw new RedirectionException(Response.Status.SEE_OTHER, URI.create("/blocklists?message=" + this.name()));
        }
    }

    @GET
    @Path("/blocklists")
    @Template(name = "/blocklists.ftl")
    public Map<String, Object> listBlocklists(@QueryParam("message") String messageName) {
        Map<String, Object> model = new HashMap<>();

        model.put("blocklists", MaBlockerWebLauncher.getBlockingConfiguration().listBlocklists());
        model.put("idnDecoder", (Function<String, String>) IDN::toUnicode);
        if(messageName != null) { model.put("message", Messages.get(messageName)); }
        model.put("unappliedChanges", MaBlockerWebLauncher.getBlockingConfiguration().isUnappliedChanges());

        return model;
    }

    @POST
    @Path("/blocklists/add/preview")
    @Template(name = "/blocklist-add-host.ftl")
    @Consumes(Mime.HTML_FORM)
    public Map<String, Object> addHostsPreview(@FormParam("blocklist-id") long blocklistId,
                                             @FormParam("new-hosts") String hostsText) {
        Map<String, Object> model = new HashMap<>();

        Blocklist blocklist = getBlocklistById(blocklistId);

        List<String> hosts = Arrays.stream(hostsText.split(Pattern.quote("\n")))
                .map(host ->
                        host.replace("http://", "")
                            .replace("https://", "")
                            .replaceFirst("^/+", "")
                            .split(Pattern.quote("/"))[0]
                            .trim()
                )
                .map(IDN::toASCII)
                .filter(host -> !host.isEmpty())
                .collect(Collectors.toList());
        if(hosts.isEmpty()) { Messages.no_hosts.redirect(); }
        hosts.removeIf(blocklist::contains);
        if(hosts.isEmpty()) { Messages.no_new_hosts.redirect(); }

        model.put("blocklist", blocklist);
        model.put("hosts", hosts.stream().map(IDN::toUnicode).collect(Collectors.toList()));
        model.put("hostsBase64",
                Base64.getEncoder().encodeToString(String.join("/", hosts).getBytes(StandardCharsets.UTF_8)));
        return model;
    }

    @POST
    @Path("/blocklists/add")
    @Consumes(Mime.HTML_FORM)
    public void addHosts(@FormParam("blocklist-id") long blocklistId, @FormParam("hosts-base64") String hostsBase64) {
        Blocklist blocklist = getBlocklistById(blocklistId);

        blocklist.addHosts(Arrays.stream(
                new String(Base64.getDecoder().decode(hostsBase64), StandardCharsets.UTF_8)
                .split(Pattern.quote("/")))
                .collect(Collectors.toList()));

        Messages.hosts_added.redirect();
    }

    @POST
    @Path("/blocklist/{id}/delete")
    @Consumes(Mime.HTML_FORM)
    public void delete(@PathParam("id") long blocklistId) {
        getBlocklistById(blocklistId);
        MaBlockerWebLauncher.getBlockingConfiguration().removeBlocklist(blocklistId);
        Messages.blocklist_deleted.redirect();
    }

    public static Blocklist getBlocklistById(long id) {
        Optional<Blocklist> blocklistOptional = MaBlockerWebLauncher.getBlockingConfiguration().getBlocklist(id);
        if(!blocklistOptional.isPresent()) { Messages.blocklist_not_found.redirect(); }
        return blocklistOptional.get();
    }
}
