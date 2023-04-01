/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker.web;

import lombok.NonNull;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;

import java.net.URI;

public class MaBlockerWebServer {
    public MaBlockerWebServer(@NonNull final String host,
                              final int port,
                              @NonNull final String staticResourcesClasspath,
                              @NonNull final String templateBasePath)
    {
        URI uri = UriBuilder.fromUri("http://" + host + "/").port(port).build();

        ResourceConfig config = new MaBlockerWebApplication(templateBasePath);

        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(uri, config);
        HttpHandler httpHandler = new CLStaticHttpHandler(MaBlockerWebServer.class.getClassLoader(),staticResourcesClasspath);
        httpServer.getServerConfiguration().addHttpHandler(httpHandler, "/static/");
    }
}
