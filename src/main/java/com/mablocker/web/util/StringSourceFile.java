/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker.web.util;

import net.schmizz.sshj.xfer.InMemorySourceFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class StringSourceFile extends InMemorySourceFile {
    private final String name;
    private final byte[] contents;

    public StringSourceFile(String name, String contents) {
        this.name = name;
        this.contents = contents.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getLength() {
        return contents.length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(contents);
    }
}
