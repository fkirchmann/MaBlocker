/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Blocklist {
    private BlockingConfiguration parent;

    @Getter
    private String name;

    private List<String> hosts = new ArrayList<>();

    @Getter
    private long id = Math.abs((new Random()).nextLong());

    protected Blocklist(BlockingConfiguration parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public List<String> listHosts() { return new ArrayList<>(hosts); }

    public boolean contains(String host) { return hosts.contains(host); }

    public void setName(String name) {
        if(name.trim().isEmpty()) { throw new IllegalArgumentException("Name must not be empty"); }
        if(parent.listBlocklists().stream().map(Blocklist::getName).anyMatch(otherName -> otherName.equals(name))) {
            throw new IllegalArgumentException("A Blocklist with this name already exists");
        }
        this.name = name;
        parent.save();
    }

    public void addHosts(List<String> hostnames) {
        for(String host : hostnames) {
            host = host.trim();
            if(!host.isEmpty() && !hosts.contains(host)) { hosts.add(host); }
        }
        parent.save();
        parent.setUnappliedChanges(true);
    }

    public void addHost(String hostname) {
        hostname = hostname.trim();
        if(hostname.isEmpty()) { throw new IllegalArgumentException("Hostname must not be empty"); }
        if(hosts.contains(hostname)) { throw new IllegalArgumentException("Host is already in list"); }
        hosts.add(hostname);
        parent.save();
        parent.setUnappliedChanges(true);
    }

    public void removeHost(String hostname) {
        if(!hosts.remove(hostname)) {
            throw new IllegalArgumentException("Host not found in blocklist");
        }
        parent.save();
        parent.setUnappliedChanges(true);
    }
}
