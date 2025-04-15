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
package org.apache.felix.configurator.impl.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class State extends AbstractState implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Serialization version. */
    private static final int VERSION = 1;

    public static final String FILE_NAME = "state.ser";

    private Map<Long, Long> bundlesLastModified = new ConcurrentHashMap<>();

    private Map<Long, Long> bundlesConfigAdminBundleId = new ConcurrentHashMap<>();

    private volatile Set<String> initialHashes;

    /**
     * Serialize the object
     * - write version id
     * - serialize fields
     * @param out Object output stream
     * @throws IOException
     */
    private void writeObject(final java.io.ObjectOutputStream out)
    throws IOException {
        out.writeInt(VERSION);
        out.writeObject(bundlesLastModified);
        out.writeObject(bundlesConfigAdminBundleId);
        out.writeObject(initialHashes);
    }

    /**
     * Deserialize the object
     * - read version id
     * - deserialize fields
     */
    @SuppressWarnings("unchecked")
    private void readObject(final java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        final int version = in.readInt();
        if ( version < 1 || version > VERSION ) {
            throw new ClassNotFoundException(this.getClass().getName());
        }
        this.bundlesLastModified =(Map<Long, Long>) in.readObject();
        this.bundlesConfigAdminBundleId = (Map<Long, Long>) in.readObject();
        initialHashes = (Set<String>) in.readObject();
    }

    public static State createOrReadState(final File f)
    throws ClassNotFoundException, IOException {
        if ( f == null || !f.exists() ) {
            return new State();
        }
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(f, "r")) {
			byte[] bytes = new byte[(int)randomAccessFile.length()];

			randomAccessFile.readFully(bytes);

			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));

			return (State) ois.readObject();
		}
    }

    public static void writeState(final File f, final State state)
    throws IOException {
        if ( f == null ) {
            // do nothing, no file system support
            return;
        }
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(f, "rw")) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);

			oos.writeObject(state);

			oos.close();

			randomAccessFile.write(byteArrayOutputStream.toByteArray());
		}
    }

    public Long getLastModified(final long bundleId) {
        return this.bundlesLastModified.get(bundleId);
    }

    public void setLastModified(final long bundleId, final long lastModified) {
        this.bundlesLastModified.put(bundleId, lastModified);
    }

    public void removeLastModified(final long bundleId) {
        this.bundlesLastModified.remove(bundleId);
    }

    public Long getConfigAdminBundleId(final long bundleId) {
        return this.bundlesConfigAdminBundleId.get(bundleId);
    }

    public void setConfigAdminBundleId(final long bundleId, final long lastModified) {
        this.bundlesConfigAdminBundleId.put(bundleId, lastModified);
    }

    public void removeConfigAdminBundleId(final long bundleId) {
        this.bundlesConfigAdminBundleId.remove(bundleId);
    }

    public Set<Long> getKnownBundleIds() {
        return this.bundlesLastModified.keySet();
    }

   public Set<String> getInitialHashes() {
        return this.initialHashes;
    }

    public void setInitialHashes(final Set<String> value) {
        this.initialHashes = value;
    }

    /**
     * Add all configurations for a pid
     * @param pid The pid
     * @param configs The list of configurations
     */
    public void addAll(final String pid, final ConfigList configs) {
        if ( configs != null ) {
            ConfigList list = this.getConfigurations().get(pid);
            if ( list == null ) {
                list = new ConfigList();
                this.getConfigurations().put(pid, list);
            }

            list.addAll(configs);
        }
    }

    /**
     * Mark all configurations from that bundle as changed to reprocess them
     * @param bundleId The bundle id
     */
    public void checkEnvironments(final long bundleId) {
        for(final String pid : this.getPids()) {
            final ConfigList configList = this.getConfigurations(pid);
            for(final Config cfg : configList) {
                if ( cfg.getBundleId() == bundleId ) {
                    configList.setHasChanges(true);
                    break;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "State [bundlesLastModified=" + bundlesLastModified +
                ", initialHashes=" + initialHashes +
                ", bundlesConfigAdminBundleId=" + bundlesConfigAdminBundleId + "]";
    }

    public Set<Long> getBundleIdsUsingConfigAdmin() {
        return new HashSet<>(this.bundlesConfigAdminBundleId.keySet());
    }
}
/* @generated */