/*
 * Copyright (c) 2017-2023 Felix Kirchmann.
 * Distributed under the MIT License (license terms are at http://opensource.org/licenses/MIT).
 */

package com.mablocker.web.util;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

public class SimpleCache<T>  {
    private volatile T cached = null;
    private volatile boolean isPresent = false;
    private volatile Instant lastCalculated = null;
    private final Object lock = new Object();
    private final Supplier<T> supplier;
    private final Duration cacheValidity;

    public SimpleCache(final Duration cacheValidity, final Supplier<T> supplier) {
        this.cacheValidity = cacheValidity;
        this.supplier = supplier;
    }

    public T getCached() {
        synchronized (this.lock) {
            if(this.isPresent &&
                  Duration.between(this.lastCalculated, Instant.now()).compareTo(this.cacheValidity) < 0)  {
                return this.cached;
            }
            this.cached = this.supplier.get();
            this.lastCalculated = Instant.now();
            this.isPresent = true;
            return this.cached;
        }
    }
}
