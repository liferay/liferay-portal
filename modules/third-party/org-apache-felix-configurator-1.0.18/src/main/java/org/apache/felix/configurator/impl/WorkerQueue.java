/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.felix.configurator.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

import org.apache.felix.configurator.impl.logger.SystemLogger;

public class WorkerQueue implements Runnable {

    private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();

    private final Thread backgroundThread;

    private volatile boolean stopped = false;

    public WorkerQueue() {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        this.backgroundThread = threadFactory.newThread(this);
        this.backgroundThread.setDaemon(true);
        this.backgroundThread.setName("Apache Felix Configurator Worker Thread");
        this.backgroundThread.start();
    }

    public void stop() {
        this.stopped = true;

        this.backgroundThread.interrupt();
    }

    public void enqueue(final Runnable r) {
        if ( !this.stopped ) {
            this.tasks.add(r);
        }
    }

    @Override
    public void run() {
        Runnable r;
        do {
            r = null;

            if ( !this.stopped ) {
                try {
                    r = this.tasks.take();
                }
                catch (InterruptedException interruptedException) {
                    return;
                }
            }

            if ( r != null ) {
                try {
                    r.run();
                } catch ( final Throwable t) {
                    // just to be sure our loop never dies
                    SystemLogger.error("Error processing task" + t.getMessage(), t);
                }
            }
        } while ( true );
    }
}
/* @generated */