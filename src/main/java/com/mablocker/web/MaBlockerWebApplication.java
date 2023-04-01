/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker.web;

import com.mablocker.web.endpoint.RedirectEndpoint;
import com.mablocker.web.endpoint.pages.*;
import com.mablocker.web.util.ExceptionLogger;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateNameFormat;
import freemarker.template.Configuration;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerConfigurationFactory;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import static freemarker.template.Configuration.VERSION_2_3_23;

public class MaBlockerWebApplication extends ResourceConfig {
    @Context ServletContext servletContext;

    private final Configuration freemarkerConfig = new Configuration(VERSION_2_3_23);

    public MaBlockerWebApplication(final String templateBasePath) {
        this.freemarkerConfig.setTemplateLoader(new ClassTemplateLoader(this.getClassLoader(), ""));
        this.freemarkerConfig.setTemplateNameFormat(TemplateNameFormat.DEFAULT_2_4_0);
        this.freemarkerConfig.setDefaultEncoding("UTF-8");

        this.property(FreemarkerMvcFeature.TEMPLATE_OBJECT_FACTORY,
                (FreemarkerConfigurationFactory) () -> this.freemarkerConfig);
        this.property(FreemarkerMvcFeature.TEMPLATE_BASE_PATH, templateBasePath);
        this.register(FreemarkerMvcFeature.class);

        this.registerClasses(ExceptionLogger.class);
        this.registerInstances(
                new RedirectEndpoint(),
                // Human-browseable Pages
                new BlocklistsPage(),
                new CreateBlocklistPage(),
                new RenameBlocklistPage(),
                new EditBlocklistPage(),
                new ApplyBlocklistPage()
        );
    }
}
