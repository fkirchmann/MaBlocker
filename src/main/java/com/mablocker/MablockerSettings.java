/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MablockerSettings {
        private final Properties settings = new Properties();

        public MablockerSettings(final String filename) throws IOException {
            try(final FileInputStream in = new FileInputStream(filename)){
                settings.load(in);
            }
        }

        public String getRouterHostname() { return settings.getProperty("mablocker.router.hostname"); }
        public int getSshPort() { return Integer.parseInt(settings.getProperty("mablocker.router.ssh.port")); }
    public String getSshUser() { return settings.getProperty("mablocker.router.ssh.user"); }
        public String getSshKeyfilePath() { return settings.getProperty("mablocker.router.ssh.keyfile"); }
        public String getWebHost() { return settings.getProperty("mablocker.web.host"); }
        public int getWebPort() { return Integer.parseInt(settings.getProperty("mablocker.web.port")); }

}
