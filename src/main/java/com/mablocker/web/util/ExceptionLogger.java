/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker.web.util;

import com.esotericsoftware.minlog.Log;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.RedirectionException;

public class ExceptionLogger implements ApplicationEventListener, RequestEventListener {

    @Override
    public void onEvent(final ApplicationEvent applicationEvent) {
    }

    @Override
    public RequestEventListener onRequest(final RequestEvent requestEvent) {
        return this;
    }

    @Override
    public void onEvent(RequestEvent paramRequestEvent) {

        if(paramRequestEvent.getType() == RequestEvent.Type.ON_EXCEPTION) {
            // Don't log 404 errors that aren't the result of an application exception
            if((paramRequestEvent.getException() instanceof NotFoundException
                    && paramRequestEvent.getException().getCause() == null)
                // Also don't log redirects
                || paramRequestEvent.getException() instanceof RedirectionException) {
                return;
            }
            Log.error("Problem in webapp", paramRequestEvent.getException().getCause());
            paramRequestEvent.getException().printStackTrace();
        }
    }
}