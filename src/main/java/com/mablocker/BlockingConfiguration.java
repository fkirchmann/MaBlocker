/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker;

import com.esotericsoftware.minlog.Log;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class BlockingConfiguration {
    @XStreamOmitField
    private static XStream xstream = new XStream(new StaxDriver());
    static { xstream.processAnnotations(BlockingConfiguration.class); }
    @XStreamOmitField
    private File file;

    private List<Blocklist> blocklists = new ArrayList<>();
    @Getter
    @Setter
    private boolean unappliedChanges = false;

    public static BlockingConfiguration fromFile(File file) {
        BlockingConfiguration configuration;
        if(file.exists()) {
            configuration = (BlockingConfiguration) xstream.fromXML(file);
        } else {
            configuration = new BlockingConfiguration();
        }
        configuration.file = file;
        return configuration;
    }

    private BlockingConfiguration() {}

    public Blocklist addBlocklist(String name) {
        if(name.trim().isEmpty()) { throw new IllegalArgumentException("Name must not be empty"); }
        if(blocklists.stream().map(Blocklist::getName).anyMatch(otherName -> otherName.equals(name))) {
            throw new IllegalArgumentException("A Blocklist with this name already exists");
        }
        Blocklist blocklist = new Blocklist(this, name);
        blocklists.add(blocklist);
        this.save();
        return blocklist;
    }

    public void removeBlocklist(long id) {
        if(!blocklists.removeIf(blocklist -> blocklist.getId() == id)) {
            throw new IllegalArgumentException("Blocklist not found");
        }
        this.save();
        this.setUnappliedChanges(true);
    }

    public List<Blocklist> listBlocklists() { return new ArrayList<>(blocklists); }

    public Set<String> listBlockedHosts() {
        return listBlocklists().stream()
                .flatMap(blocklist -> blocklist.listHosts().stream())
                .collect(Collectors.toSet());
    }

    public Optional<Blocklist> getBlocklist(long id) {
        return blocklists.stream().filter(list -> list.getId() == id).findFirst();
    }

    protected void save() {
        try(FileWriter fw = new FileWriter(file)) {
            xstream.toXML(this, fw);
        } catch (IOException e) {
            Log.error("Couldn't save!!!", e);
        }
    }
}
