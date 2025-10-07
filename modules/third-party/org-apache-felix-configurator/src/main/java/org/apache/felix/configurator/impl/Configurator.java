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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.felix.configurator.impl.json.BinUtil;
import org.apache.felix.configurator.impl.json.BinaryManager;
import org.apache.felix.configurator.impl.json.JSONUtil;
import org.apache.felix.configurator.impl.logger.SystemLogger;
import org.apache.felix.configurator.impl.model.BundleState;
import org.apache.felix.configurator.impl.model.Config;
import org.apache.felix.configurator.impl.model.ConfigList;
import org.apache.felix.configurator.impl.model.ConfigPolicy;
import org.apache.felix.configurator.impl.model.ConfigState;
import org.apache.felix.configurator.impl.model.ConfigurationFile;
import org.apache.felix.configurator.impl.model.State;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServicePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.configurator.ConfiguratorConstants;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * The main class of the configurator.
 *
 */
public class Configurator {

    private final BundleContext bundleContext;

    private final State state;

    private final org.osgi.util.tracker.BundleTracker<Bundle> tracker;

    private volatile boolean active = true;

    private volatile Object coordinator;

    private final WorkerQueue queue;

    private final List<ServiceReference<ConfigurationAdmin>> configAdminReferences;

    /**
     * Create a new configurator and start it
     *
     * @param bc The bundle context
     * @param configAdminReferences Dynamic list of references to the configuration admin service visible to the configurator
     */
    public Configurator(final BundleContext bc, final List<ServiceReference<ConfigurationAdmin>> configAdminReferences) {
        this.queue = new WorkerQueue();
        this.bundleContext = bc;
        this.configAdminReferences = configAdminReferences;
        State s = null;
        try {
            s = State.createOrReadState(bundleContext.getDataFile(State.FILE_NAME));
        } catch ( final ClassNotFoundException | IOException e ) {
            SystemLogger.error("Unable to read persisted state from " + State.FILE_NAME, e);
            s = new State();
        }
        this.state = s;
        this.tracker = new org.osgi.util.tracker.BundleTracker<>(this.bundleContext,
                Bundle.ACTIVE|Bundle.STARTING|Bundle.STOPPING|Bundle.RESOLVED|Bundle.INSTALLED,

                new BundleTrackerCustomizer<Bundle>() {

            @Override
            public Bundle addingBundle(final Bundle bundle, final BundleEvent event) {
                final int state = bundle.getState();
                if ( active &&
                    (state == Bundle.ACTIVE || state == Bundle.STARTING) ) {
                    SystemLogger.debug("Adding bundle " + getBundleIdentity(bundle) + " : " + getBundleState(state));
                    queue.enqueue(new Runnable() {

                        @Override
                        public void run() {
                            if ( processAddBundle(bundle) ) {
                                process();
                            }
                        }
                    });
                }
                return bundle;
            }

            @Override
            public void modifiedBundle(final Bundle bundle, final BundleEvent event, final Bundle object) {
                this.addingBundle(bundle, event);
            }

            @Override
            public void removedBundle(final Bundle bundle, final BundleEvent event, final Bundle object) {
                final int state = bundle.getState();
                if ( active && state == Bundle.UNINSTALLED ) {
                    SystemLogger.debug("Removing bundle " + getBundleIdentity(bundle) + " : " + getBundleState(state));
                    queue.enqueue(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if ( processRemoveBundle(bundle.getBundleId()) ) {
                                    process();
                                }
                            } catch ( final IllegalStateException ise) {
                                SystemLogger.error("Error processing bundle " + getBundleIdentity(bundle), ise);
                            }
                        }
                    });
                }
            }

        });
    }

    public void configAdminAdded() {
        queue.enqueue(new Runnable() {

            @Override
            public void run() {
                process();
            }
        });
    }

    private String getBundleIdentity(final Bundle bundle) {
        if ( bundle.getSymbolicName() == null ) {
            return bundle.getBundleId() + " (" + bundle.getLocation() + ")";
        } else {
            return bundle.getSymbolicName() + ":" + bundle.getVersion() + " (" + bundle.getBundleId() + ")";
        }
    }

    private String getBundleState(int state) {
        switch ( state ) {
            case Bundle.ACTIVE : return "active";
            case Bundle.INSTALLED : return "installed";
            case Bundle.RESOLVED : return "resolved";
            case Bundle.STARTING : return "starting";
            case Bundle.STOPPING : return "stopping";
            case Bundle.UNINSTALLED : return "uninstalled";
        }
        return String.valueOf(state);
    }

    /**
     * Shut down the configurator
     */
    public void shutdown() {
        this.active = false;
        this.queue.stop();
        this.tracker.close();
    }

    /**
     * Start the configurator.
     */
    public void start() {
        // get the directory for storing binaries
        String dirPath = this.bundleContext.getProperty(ConfiguratorConstants.CONFIGURATOR_BINARIES);
        if ( dirPath != null ) {
            final File dir = new File(dirPath);
            if ( dir.exists() && dir.isDirectory() ) {
                BinUtil.binDirectory = dir;
            } else if ( dir.exists() ) {
                SystemLogger.error("Directory property is pointing at a file not a dir: " + dirPath + ". Using default path.");
            } else {
                try {
                    if ( dir.mkdirs() ) {
                        BinUtil.binDirectory = dir;
                    }
                } catch ( final SecurityException se ) {
                    // ignore
                }
                if ( BinUtil.binDirectory == null ) {
                    SystemLogger.error("Unable to create a directory at: " + dirPath + ". Using default path.");
                }
            }
        }
        if ( BinUtil.binDirectory == null ) {
            BinUtil.binDirectory = this.bundleContext.getDataFile("binaries" + File.separatorChar + ".check");
            BinUtil.binDirectory = BinUtil.binDirectory.getParentFile();
            BinUtil.binDirectory.mkdirs();
        }

        // before we start the tracker we process all available bundles and initial configuration
        final String initial = this.bundleContext.getProperty(ConfiguratorConstants.CONFIGURATOR_INITIAL);
        if ( initial == null ) {
            this.processRemoveBundle(-1);
        } else {
            // JSON or URLs ?
            final Set<String> hashes = new HashSet<>();
            final Map<String, String> files = new TreeMap<>();

            if ( !initial.trim().startsWith("{") ) {
                // URLs
                final String[] urls = initial.trim().split(",");
                for(final String urlString : urls) {
                    URL url = null;
                    try {
                        url = new URL(urlString);
                    } catch (final MalformedURLException e) {
                    }
                    if ( url != null ) {
                        try {
                            final String contents = JSONUtil.getResource(urlString, url);
                            files.put(urlString, contents);
                            hashes.add(Util.getSHA256(contents.trim()));
                        } catch ( final IOException ioe ) {
                            SystemLogger.error("Unable to read " + urlString, ioe);
                        }
                    }
                }
            } else {
                // JSON
                hashes.add(Util.getSHA256(initial.trim()));
                files.put(ConfiguratorConstants.CONFIGURATOR_INITIAL, initial);
            }
            if ( state.getInitialHashes() == null || !state.getInitialHashes().equals(hashes)) {
                if ( state.getInitialHashes() != null ) {
                    processRemoveBundle(-1);
                }
                final JSONUtil.Report report = new JSONUtil.Report();
                final BinaryManager converter = new BinaryManager(null, report);
                final List<ConfigurationFile> allFiles = new ArrayList<>();
                for(final Map.Entry<String, String> entry : files.entrySet()) {
                    final ConfigurationFile file = JSONUtil.readJSON(converter, entry.getKey(), null, -1, entry.getValue(), report);
                    if ( file != null ) {
                        allFiles.add(file);
                    }
                }
                for(final String w : report.warnings) {
                    SystemLogger.warning(w);
                }
                for(final String e : report.errors) {
                    SystemLogger.error(e);
                }
                final BundleState bState = new BundleState();
                bState.addFiles(allFiles);
                for(final String pid : bState.getPids()) {
                    state.addAll(pid, bState.getConfigurations(pid));
                }
                state.setInitialHashes(hashes);
            }

        }

        final Bundle[] bundles = this.bundleContext.getBundles();
        final Set<Long> ids = new HashSet<>();
        for(final Bundle b : bundles) {
            ids.add(b.getBundleId());
            final int state = b.getState();
            if ( state == Bundle.ACTIVE || state == Bundle.STARTING ) {
                processAddBundle(b);
            }
        }
        for(final long id : new HashSet<>(state.getKnownBundleIds())) {
            if ( !ids.contains(id) ) {
                processRemoveBundle(id);
            }
        }
        this.process();
        this.tracker.open();
    }

    public boolean processAddBundle(final Bundle bundle) {
        final long bundleId = bundle.getBundleId();
        final long bundleLastModified = bundle.getLastModified();
        final Object liferayConfiguratorPolicy = bundle.getHeaders().get("Liferay-Configurator-Policy");

        final Long lastModified = state.getLastModified(bundleId);
        if ( lastModified != null && lastModified.longValue() == bundleLastModified && !("always".equals(liferayConfiguratorPolicy))) {
            // no changes, nothing to do
            return false;
        }

        BundleState config = null;
        try {
            final Set<String> paths = Util.isConfigurerBundle(bundle, this.bundleContext.getBundle().getBundleId());
            if ( paths != null ) {
                final JSONUtil.Report report = new JSONUtil.Report();
                config = JSONUtil.readConfigurationsFromBundle(new BinUtil.ResourceProvider() {

                    @Override
                    public String getIdentifier() {
                        return bundle.toString();
                    }

                    @Override
                    public URL getEntry(String path) {
                        return bundle.getEntry(path);
                    }

                    @Override
                    public long getBundleId() {
                        return bundle.getBundleId();
                    }

                    @Override
                    public Enumeration<URL> findEntries(String path, String filePattern) {
                        return bundle.findEntries(path, filePattern, false);
                    }
                }, paths, report);
                for(final String w : report.warnings) {
                    SystemLogger.warning(w);
                }
                for(final String e : report.errors) {
                    SystemLogger.error(e);
                }
            }
        } catch ( final IllegalStateException ise) {
            SystemLogger.error("Error processing bundle " + getBundleIdentity(bundle), ise);
        }
        if ( lastModified != null ) {
            processRemoveBundle(bundleId);
        }
        if ( config != null ) {
            for(final String pid : config.getPids()) {
                state.addAll(pid, config.getConfigurations(pid));
            }
            state.setLastModified(bundleId, bundleLastModified);
            return true;
        }
        return lastModified != null;
    }

    public boolean processRemoveBundle(final long bundleId) {
        if ( state.getLastModified(bundleId) != null ) {
            state.removeLastModified(bundleId);
            for(final String pid : state.getPids()) {
                final ConfigList configList = state.getConfigurations(pid);
                configList.uninstall(bundleId);
            }
            return true;
        }
        return false;
    }

    /**
     * Set or unset the coordinator service
     * @param coordinator The coordinator service or {@code null}
     */
    public void setCoordinator(final Object coordinator) {
        this.coordinator = coordinator;
    }

    /**
     * Process the state to activate/deactivate configurations
     */
    public void process() {
        final Object localCoordinator = this.coordinator;
        Object coordination = null;
        if ( localCoordinator != null ) {
            coordination = CoordinatorUtil.getCoordination(localCoordinator);
        }

        boolean retry = false;
        try {
            for(final String pid : state.getPids()) {
                final ConfigList configList = state.getConfigurations(pid);

                if ( configList.hasChanges() ) {
                    if ( process(configList) ) {
                        try {
                            State.writeState(this.bundleContext.getDataFile(State.FILE_NAME), state);
                        } catch ( final IOException ioe) {
                            SystemLogger.error("Unable to persist state to " + State.FILE_NAME, ioe);
                        }
                    } else {
                        retry = true;
                    }
                }
            }

        } finally {
            if ( coordination != null ) {
                CoordinatorUtil.endCoordination(coordination);
            }
        }
        if ( !retry ) {
            // check whether there is a stale config admin bundle id
            boolean changed = false;
            for(final Long bundleId : this.state.getBundleIdsUsingConfigAdmin()) {
                if ( this.state.getLastModified(bundleId) == null ) {
                    this.state.removeConfigAdminBundleId(bundleId);
                    changed = true;
                }
            }
            if ( changed ) {
                try {
                    State.writeState(this.bundleContext.getDataFile(State.FILE_NAME), state);
                } catch ( final IOException ioe) {
                    SystemLogger.error("Unable to persist state to " + State.FILE_NAME, ioe);
                }
            }
        }
    }

    /**
     * Process changes to a pid.
     * @param configList The config list
     * @return {@code true} if the change has been processed, {@code false} if a retry is required
     */
    public boolean process(final ConfigList configList) {
        Config toActivate = null;
        Config toDeactivate = null;

        for(final Config cfg : configList) {
            switch ( cfg.getState() ) {
                case INSTALL     : // activate if first found
                    if ( toActivate == null ) {
                        toActivate = cfg;
                    }
                    break;

                case IGNORED     : // same as installed
                case INSTALLED   : // check if we have to uninstall
                    if ( toActivate == null ) {
                        toActivate = cfg;
                    } else {
                        cfg.setState(ConfigState.INSTALL);
                    }
                    break;

                case UNINSTALL   : // deactivate if first found (we should only find one anyway)
                    if ( toDeactivate == null ) {
                       toDeactivate = cfg;
                    }
                    break;

                case UNINSTALLED : // nothing to do
                    break;
            }

        }
        // if there is a configuration to activate, we can directly activate it
        // without deactivating (reducing the changes of the configuration from two
        // to one)
        boolean noRetryNeeded = true;
        if ( toActivate != null && toActivate.getState() == ConfigState.INSTALL ) {
            noRetryNeeded = activate(configList, toActivate);
        }
        if ( toActivate == null && toDeactivate != null ) {
            noRetryNeeded = deactivate(configList, toDeactivate);
        }

        if ( noRetryNeeded ) {
            // remove all uninstall(ed) configurations
            final Iterator<Config> iter = configList.iterator();
            boolean foundInstalled = false;
            while ( iter.hasNext() ) {
                final Config cfg = iter.next();
                if ( cfg.getState() == ConfigState.UNINSTALL || cfg.getState() == ConfigState.UNINSTALLED ) {
                    if ( cfg.getFiles() != null ) {
                        for(final File f : cfg.getFiles()) {
                            f.delete();
                        }
                    }
                    iter.remove();
                } else if ( cfg.getState() == ConfigState.INSTALLED ) {
                    if ( foundInstalled ) {
                        cfg.setState(ConfigState.INSTALL);
                    } else {
                        foundInstalled = true;
                    }
                }
            }

            // mark as processed
            configList.setHasChanges(false);
        }
        return noRetryNeeded;
    }

    private ConfigurationAdmin getConfigurationAdmin(final long configAdminServiceBundleId) {
        ServiceReference<ConfigurationAdmin> ref = null;
        synchronized ( this.configAdminReferences ) {
            for(final ServiceReference<ConfigurationAdmin> r : this.configAdminReferences ) {
                final Bundle bundle = r.getBundle();
                if ( bundle != null && bundle.getBundleId() == configAdminServiceBundleId) {
                    ref = r;
                    break;
                }
            }
        }
        if ( ref != null ) {
            return this.bundleContext.getService(ref);
        }
        return null;
    }

    /**
     * Try to activate a configuration
     * Check policy and change count
     * @param configList The configuration list
     * @param cfg The configuration to activate
     * @return {@code true} if activation was successful
     */
    public boolean activate(final ConfigList configList, final Config cfg) {
        // check for configuration admin
        Long configAdminServiceBundleId = this.state.getConfigAdminBundleId(cfg.getBundleId());
        if ( configAdminServiceBundleId == null ) {
            final Bundle configBundle = cfg.getBundleId() == -1 ? this.bundleContext.getBundle() : this.bundleContext.getBundle(Constants.SYSTEM_BUNDLE_LOCATION).getBundleContext().getBundle(cfg.getBundleId());
            // we check the state again, just to be sure (to avoid race conditions)
            if ( configBundle != null
                 && (configBundle.getState() == Bundle.STARTING || configBundle.getState() == Bundle.ACTIVE)) {
                if ( System.getSecurityManager() == null
                     || configBundle.hasPermission( new ServicePermission(ConfigurationAdmin.class.getName(), ServicePermission.GET)) ) {
                    try {
                        final BundleContext ctx = configBundle.getBundleContext();
                        if ( ctx != null ) {
                            final Collection<ServiceReference<ConfigurationAdmin>> refs = ctx.getServiceReferences(ConfigurationAdmin.class, null);
                            final List<ServiceReference<ConfigurationAdmin>> sortedRefs = new ArrayList<>(refs);
                            Collections.sort(sortedRefs);
                            for(int i=sortedRefs.size();i>0;i--) {
                                final ServiceReference<ConfigurationAdmin> r = sortedRefs.get(i-1);
                                synchronized ( this.configAdminReferences ) {
                                    if ( this.configAdminReferences.contains(r) ) {
                                        configAdminServiceBundleId = r.getBundle().getBundleId();
                                        break;
                                    }
                                }
                            }
                        }
                    } catch ( final IllegalStateException e) {
                        // this might happen if the config admin bundle gets deactivated while we use it
                        // we can ignore this and retry later on
                    } catch (final InvalidSyntaxException e) {
                        // this can never happen as we pass {@code null} as the filter
                    }
                }
            }
        }
        if ( configAdminServiceBundleId == null ) {
            // no configuration admin found, we have to retry
            return false;
        }
        final ConfigurationAdmin configAdmin = this.getConfigurationAdmin(configAdminServiceBundleId);
        if ( configAdmin == null ) {
            // getting configuration admin failed, we have to retry
            return false;
        }
        this.state.setConfigAdminBundleId(cfg.getBundleId(), configAdminServiceBundleId);

        boolean ignore = false;
        try {
            // get existing configuration - if any
            boolean update = false;
            Configuration configuration = ConfigUtil.getOrCreateConfiguration(configAdmin, cfg.getPid(), false);
            if ( configuration == null ) {
                // new configuration
                configuration = ConfigUtil.getOrCreateConfiguration(configAdmin, cfg.getPid(), true);
                update = true;
            } else {
                if ( cfg.getPolicy() == ConfigPolicy.FORCE ) {
                    update = true;
                } else {
                    if ( configList.getLastInstalled() == null
                         || configList.getChangeCount() != configuration.getChangeCount() ) {
                        ignore = true;
                    } else {
                        update = true;
                    }
                }
            }

            if ( update ) {
                configuration.updateIfDifferent(cfg.getProperties());
                cfg.setState(ConfigState.INSTALLED);
                configList.setChangeCount(configuration.getChangeCount());
                configList.setLastInstalled(cfg);
            }
        } catch (final InvalidSyntaxException | IOException e) {
            SystemLogger.error("Unable to update configuration " + cfg.getPid() + " : " + e.getMessage(), e);
            ignore = true;
        }
        if ( ignore ) {
            cfg.setState(ConfigState.IGNORED);
            configList.setChangeCount(-1);
            configList.setLastInstalled(null);
        }

        return true;
    }

    /**
     * Try to deactivate a configuration
     * Check policy and change count
     * @param cfg The configuration
     */
    public boolean deactivate(final ConfigList configList, final Config cfg) {
        final Long configAdminServiceBundleId = this.state.getConfigAdminBundleId(cfg.getBundleId());
        // check if configuration admin bundle is still available
        // if not or if we didn't record anything, we consider the configuration uninstalled
        final Bundle configBundle = configAdminServiceBundleId == null ? null : this.bundleContext.getBundle(Constants.SYSTEM_BUNDLE_LOCATION).getBundleContext().getBundle(configAdminServiceBundleId);
        if ( configBundle != null ) {
            final ConfigurationAdmin configAdmin = this.getConfigurationAdmin(configAdminServiceBundleId);
            if ( configAdmin == null ) {
                // getting configuration admin failed, we have to retry
                return false;
            }

            try {
                final Configuration c = ConfigUtil.getOrCreateConfiguration(configAdmin, cfg.getPid(), false);
                if ( c != null ) {
                    if ( cfg.getPolicy() == ConfigPolicy.FORCE
                            || configList.getChangeCount() == c.getChangeCount() ) {
                        c.delete();
                    }
                }
            } catch (final InvalidSyntaxException | IOException e) {
                SystemLogger.error("Unable to remove configuration " + cfg.getPid() + " : " + e.getMessage(), e);
            }
        }
        cfg.setState(ConfigState.UNINSTALLED);
        configList.setChangeCount(-1);
        configList.setLastInstalled(null);

        return true;
    }
}
