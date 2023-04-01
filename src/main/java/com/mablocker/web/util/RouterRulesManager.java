/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker.web.util;

import com.esotericsoftware.minlog.Log;
import com.mablocker.BlockingConfiguration;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.Synchronized;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RouterRulesManager
{
	private final RouterSSHClientThread thread			= new RouterSSHClientThread();
	private final Object				monitor			= new Object();
	private final BlockingConfiguration configuration;
	private volatile StringWriter log                   = new StringWriter();
	@Getter
	private volatile State state                        = State.IDLE;

	private final int port;
	private final String host, user;
	private final KeyProvider keyProvider;

	public enum State {
	    IDLE, RUNNING, ERROR, COMPLETE
    }

	public RouterRulesManager(BlockingConfiguration configuration, String host, int port, String user, KeyProvider keyProvider) {
        this.configuration = configuration;
	    this.host = host;
        this.port = port;
        this.user = user;
		this.keyProvider = keyProvider;

        thread.start();
	}
	
	public void stop()
	{
		thread.stopGracefully();
	}

	public String getLog() { return log.toString(); }
	
	private class RouterSSHClientThread extends Thread
	{
		private boolean run = true;
		
		private RouterSSHClientThread()
		{
			super();
			this.setDaemon(true);
			this.setName("RouterSSHClientThread");
		}
		
		private void stopGracefully()
		{
			synchronized (monitor)
			{
				run = false;
				monitor.notifyAll();
			}
		}
		
		@Override
		@SneakyThrows(InterruptedException.class)
		public void run()
		{
			while (run)
			{
				// Wait for a new rule to be put into the outgoing queue 
				synchronized (monitor)
				{
					do
					{
						monitor.wait();
						if (!run) { return; }
					}
                    while (!configuration.isUnappliedChanges());
				}
				// Initialize logging for this SSH Session
                log = new StringWriter();
                PrintWriter logw = new PrintWriter(log);
                state = RouterRulesManager.State.RUNNING;
                Set<String> blockedHosts = configuration.listBlockedHosts();
                configuration.setUnappliedChanges(false);

                logw.println("Beginning Blocklist upload at " + getTimeDate());
                // Initialize SSH Client
                SSHClient ssh = new SSHClient();
                ssh.setConnectTimeout(5000);
                ssh.setTimeout(10000);
                ssh.addHostKeyVerifier((host, port, key) -> true);
                Session session = null;
                try {
                    logw.println("Connecting...");
                    ssh.connect(host, port);
                    logw.println("Authenticating...");
                    ssh.authPublickey(user, keyProvider);
                    logw.println("Starting session...");
                    session = ssh.startSession();

                    StringBuilder sb = new StringBuilder(20000);

                    for(String hostname : blockedHosts) {
                        sb.append("address=/");
                        sb.append(hostname);
                        sb.append("/0.0.0.0\n");
                    }

                    logw.println("Uploading blocklists...");
                    ssh.newSCPFileTransfer().upload(new StringSourceFile("dnsmasq.blocklist.conf", sb.toString()),
                            "/etc/");
                    //logw.println("Waiting for upload to complete...");
                    //Thread.sleep(2000); // vroom vroom race condition
                    logw.println("Restarting dnsmasq...");
                    session.exec("/etc/init.d/dnsmasq restart").join(20, TimeUnit.SECONDS);
                    logw.println("Done.");
                    state = RouterRulesManager.State.COMPLETE;
                } catch (IOException e){
                    configuration.setUnappliedChanges(true);
                    logw.println("A fatal error occurred. Details:");
                    e.printStackTrace(logw);
                    Log.warn("SSH Problem", e);
                    state = RouterRulesManager.State.ERROR;
                } finally {
                    try {
                        if(session != null) { session.close(); }
                    } catch (IOException e) {}
                    try {
                        ssh.disconnect();
                    } catch (IOException e) {}
                    logw.println("Blocklist upload ended at " + getTimeDate());
                }
			}
		}
	}
	
	@Synchronized("monitor")
	public void apply() {
		monitor.notifyAll();
	}

    public String getTimeDate() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }
}
