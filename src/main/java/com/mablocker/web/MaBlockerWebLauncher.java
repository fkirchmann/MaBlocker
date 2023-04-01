/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker.web;

import com.esotericsoftware.minlog.Log;
import com.mablocker.BlockingConfiguration;
import com.mablocker.MablockerSettings;
import com.mablocker.web.util.RouterRulesManager;
import lombok.Getter;
import net.schmizz.sshj.userauth.keyprovider.FileKeyProvider;
import net.schmizz.sshj.userauth.keyprovider.PuTTYKeyFile;

import java.io.File;
import java.io.IOException;

public class MaBlockerWebLauncher {
    @Getter
    private static BlockingConfiguration blockingConfiguration;
    @Getter
    private static RouterRulesManager routerRulesManager;

    private static final String SETTINGS_FILE = "mablocker.properties";

    public static void main(final String[] args) {
        Log.set(Log.LEVEL_DEBUG);
        MablockerSettings settings;
        try {
             settings = new MablockerSettings(SETTINGS_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load settings file: " + SETTINGS_FILE, e);
        }

        blockingConfiguration = BlockingConfiguration.fromFile(new File("blocklists.xml"));

        final FileKeyProvider keyProvider = new PuTTYKeyFile();
        keyProvider.init(new File(settings.getSshKeyfilePath()));

        routerRulesManager = new RouterRulesManager(blockingConfiguration,
                settings.getRouterHostname(), settings.getSshPort(), settings.getSshUser(), keyProvider);

        new MaBlockerWebServer(settings.getWebHost(),
                settings.getWebPort(),
                "/com/mablocker/web/static/",
                "/com/mablocker/web/templates/"
        );
    }
}
