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
package org.apache.felix.cm.impl;


import java.io.IOException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.felix.cm.PersistenceManager;
import org.apache.felix.cm.impl.helper.BaseTracker;
import org.apache.felix.cm.impl.helper.ConfigurationMap;
import org.apache.felix.cm.impl.helper.ManagedServiceFactoryTracker;
import org.apache.felix.cm.impl.helper.ManagedServiceTracker;
import org.apache.felix.cm.impl.helper.TargetedPID;
import org.apache.felix.cm.impl.persistence.CachingPersistenceManagerProxy;
import org.apache.felix.cm.impl.persistence.ExtPersistenceManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.cm.ConfigurationPermission;
import org.osgi.service.cm.ConfigurationPlugin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.cm.SynchronousConfigurationListener;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;


/**
 * The {@code ConfigurationManager} is the central class in this
 * implementation of the Configuration Admin Service Specification. As such it
 * has the following tasks:
 * <ul>
 * <li>It is a <code>BundleListener</code> which gets informed when the
 * states of bundles change. Mostly this is needed to unbind any bound
 * configuration in case a bundle is uninstalled.
 * <li>It is a <code>ServiceListener</code> which gets informed when
 * <code>ManagedService</code> and <code>ManagedServiceFactory</code>
 * services are registered and unregistered. This is used to provide
 * configuration to these services. As a service listener it also listens for
 * {@link PersistenceManager} instances being registered to support different
 * configuration persistence layers.
 * <li>A {@link ConfigurationAdminFactory} instance is registered as the
 * <code>ConfigurationAdmin</code> service.
 * <li>Last but not least this instance manages all tasks laid out in the
 * specification such as maintaining configuration, taking care of configuration
 * events, etc.
 * </ul>
 */
public class ConfigurationManager implements BundleListener
{
    // random number generator to create configuration PIDs for factory
    // configurations
    private static Random numberGenerator;

    // the BundleContext of the Configuration Admin Service bundle
    private final BundleContext bundleContext;

    // the service registration of the configuration admin
    private volatile ServiceRegistration<ConfigurationAdmin> configurationAdminRegistration;

    // the service registration properties
    private volatile Dictionary<String, Object> serviceProperties;

    // the ConfigurationEvent listeners
    private ServiceTracker<ConfigurationListener, ConfigurationListener> configurationListenerTracker;

    // the synchronous ConfigurationEvent listeners
    private ServiceTracker<SynchronousConfigurationListener, SynchronousConfigurationListener> syncConfigurationListenerTracker;

    // service tracker for managed services
    private ManagedServiceTracker managedServiceTracker;

    // service tracker for managed service factories
    private ManagedServiceFactoryTracker managedServiceFactoryTracker;

    // the thread used to schedule tasks required to run asynchronously
    private UpdateThread updateThread;

    // the thread used to schedule events to be dispatched asynchronously
    private UpdateThread eventThread;

    /**
     * The persistence manager
     */
    private final ExtPersistenceManager persistenceManager;

    // the cache of Configuration instances mapped by their PID
    // have this always set to prevent NPE on bundle shutdown
    private final HashMap<String, ConfigurationImpl> configurations = new HashMap<>();

    /**
     * The map of dynamic configuration bindings. This maps the
     * PID of the dynamically bound configuration or factory to its bundle
     * location.
     * <p>
     * On bundle startup this map is loaded from persistence and validated
     * against the locations of installed bundles: Entries pointing to bundle
     * locations not currently installed are removed.
     * <p>
     * The map is written to persistence on each change.
     */
    private final DynamicBindings dynamicBindings;

    // flag indicating whether BundleChange events should be consumed (FELIX-979)
    private volatile boolean handleBundleEvents;

    // flag indicating whether the manager is considered alive
    private volatile boolean isActive;

    // Coordinator service if available
    private volatile Object coordinator;

    public ConfigurationManager(final ExtPersistenceManager persistenceManager,
            final BundleContext bundleContext)
    throws IOException
    {
        // set up some fields
        this.bundleContext = bundleContext;
        this.dynamicBindings = new DynamicBindings( bundleContext, persistenceManager.getDelegatee() );
        this.persistenceManager = persistenceManager;
    }

    public ServiceReference<ConfigurationAdmin> start()
    {
        // configurationlistener support
        configurationListenerTracker = new ServiceTracker<>( bundleContext, ConfigurationListener.class, null );
        configurationListenerTracker.open();
        syncConfigurationListenerTracker = new ServiceTracker<>( bundleContext,
                SynchronousConfigurationListener.class, null );
        syncConfigurationListenerTracker.open();

        // initialize the asynchonous updater thread
        ThreadGroup tg = new ThreadGroup( "Configuration Admin Service" );
        tg.setDaemon( true );
        this.updateThread = new UpdateThread( tg, "CM Configuration Updater" );
        this.eventThread = new UpdateThread( tg, "CM Event Dispatcher" );

        // register as bundle and service listener
        handleBundleEvents = true;
        bundleContext.addBundleListener( this );

        // consider alive now (before clients use Configuration Admin
        // service registered in the next step)
        isActive = true;

        // create and register configuration admin - start after PM tracker ...
        ConfigurationAdminFactory caf = new ConfigurationAdminFactory( this );
        serviceProperties = new Hashtable<>();
        serviceProperties.put(Constants.SERVICE_PID, "org.apache.felix.cm.ConfigurationAdmin");
        serviceProperties.put(Constants.SERVICE_DESCRIPTION,
                "Configuration Admin Service Specification 1.6 Implementation");
        serviceProperties.put(Constants.SERVICE_VENDOR, "The Apache Software Foundation");
        serviceProperties.put("osgi.command.scope", "cm");
        Set<String> functions = new HashSet<>();
        for ( Method method : ConfigurationAdmin.class.getMethods() )
        {
            functions.add(method.getName());
        }
        serviceProperties.put("osgi.command.function", functions.toArray(new String[0]));
        configurationAdminRegistration = bundleContext.registerService(ConfigurationAdmin.class, caf,
                serviceProperties);

        // start handling ManagedService[Factory] services
        managedServiceTracker = new ManagedServiceTracker(this);
        managedServiceFactoryTracker = new ManagedServiceFactoryTracker(this);

        // start processing the event queues only after registering the service
        // see FELIX-2813 for details
        this.updateThread.start();
        this.eventThread.start();

        return configurationAdminRegistration.getReference();
    }


    public void stop( )
    {

        // stop handling bundle events immediately
        handleBundleEvents = false;

        // stop handling ManagedService[Factory] services
        if (managedServiceFactoryTracker != null) {
            managedServiceFactoryTracker.close();
        }
        if (managedServiceTracker != null) {
            managedServiceTracker.close();
        }

        // stop queue processing before unregistering the service
        // see FELIX-2813 for details
        if ( updateThread != null )
        {
            updateThread.terminate();
        }
        if ( eventThread != null )
        {
            eventThread.terminate();
        }

        // immediately unregister the Configuration Admin before cleaning up
        // clearing the field before actually unregistering the service
        // prevents IllegalStateException in getServiceReference() if
        // the field is not null but the service already unregistered
        final ServiceRegistration<ConfigurationAdmin> caReg = configurationAdminRegistration;
        configurationAdminRegistration = null;
        if ( caReg != null )
        {
            caReg.unregister();
        }

        // consider inactive after unregistering such that during
        // unregistration the manager is still alive and can react
        isActive = false;

        // stop listening for events
        try 
        {
            bundleContext.removeBundleListener( this );
        }
        catch ( final IllegalStateException ise ) 
        {
            // might happen on shutdown - we can ignore this
        }

        if ( configurationListenerTracker != null )
        {
            configurationListenerTracker.close();
        }

        if ( syncConfigurationListenerTracker != null )
        {
            syncConfigurationListenerTracker.close();
        }

        // just ensure the configuration cache is empty
        synchronized ( configurations )
        {
            configurations.clear();
        }
    }


    /**
     * Returns <code>true</code> if this manager is considered active.
     */
    boolean isActive()
    {
        return isActive;
    }

    public BundleContext getBundleContext()
    {
        return bundleContext;
    }

    // ---------- Configuration caching support --------------------------------

    ConfigurationImpl getCachedConfiguration( String pid )
    {
        synchronized ( configurations )
        {
            return configurations.get( pid );
        }
    }


    ConfigurationImpl[] getCachedConfigurations()
    {
        synchronized ( configurations )
        {
            return configurations.values().toArray(
                    new ConfigurationImpl[configurations.size()] );
        }
    }


    ConfigurationImpl cacheConfiguration( ConfigurationImpl configuration )
    {
        synchronized ( configurations )
        {
            final String pid = configuration.getPidString();
            final Object existing = configurations.get( pid );
            if ( existing != null )
            {
                return ( ConfigurationImpl ) existing;
            }

            configurations.put( pid, configuration );
            return configuration;
        }
    }


    void removeConfiguration( ConfigurationImpl configuration )
    {
        synchronized ( configurations )
        {
            configurations.remove( configuration.getPidString() );
        }
    }


    // ---------- ConfigurationAdminImpl support

    void setDynamicBundleLocation( final String pid, final String location )
    {
        if ( dynamicBindings != null )
        {
            try
            {
                dynamicBindings.putLocation( pid, location );
            }
            catch ( IOException ioe )
            {
                Log.logger.log( LogService.LOG_ERROR, "Failed storing dynamic configuration binding for {0} to {1}", new Object[]
                        { pid, location, ioe } );
            }
        }
    }


    String getDynamicBundleLocation( final String pid )
    {
        if ( dynamicBindings != null )
        {
            return dynamicBindings.getLocation( pid );
        }

        return null;
    }


    ConfigurationImpl createFactoryConfiguration( String factoryPid, String location ) throws IOException
    {
        return cacheConfiguration( internalCreateConfiguration( createPid( factoryPid ), factoryPid, location ) );
    }

    ConfigurationImpl createFactoryConfiguration(String pid, String factoryPid, String location ) throws IOException
    {
    	return cacheConfiguration( internalCreateConfiguration( pid, factoryPid, location ) );
    }

    /**
     * Returns a targeted configuration for the given service PID and
     * the reference target service.
     * <p>
     * A configuration returned has already been checked for visibility
     * by the bundle registering the referenced service. Additionally,
     * the configuration is also dynamically bound if needed.
     *
     * @param rawPid The raw service PID to get targeted configuration for.
     * @param target The target <code>ServiceReference</code> to get
     *      configuration for.
     * @return The best matching targeted configuration or <code>null</code>
     *      if there is no configuration at all.
     * @throwss IOException if an error occurrs reading configurations
     *      from persistence.
     */
    ConfigurationImpl getTargetedConfiguration( final String rawPid, final ServiceReference target ) throws IOException
    {
        final Bundle serviceBundle = target.getBundle();
        if ( serviceBundle != null )
        {
            // list of targeted PIDs to check
            final StringBuilder targetedPid = new StringBuilder( rawPid );
            int i = 3;
            String[] names = new String[4];
            names[i--] = targetedPid.toString();
            targetedPid.append( '|' ).append( serviceBundle.getSymbolicName() );
            names[i--] = targetedPid.toString();
            targetedPid.append( '|' ).append( serviceBundle.getVersion().toString() );
            names[i--] = targetedPid.toString();
            targetedPid.append( '|' ).append( Activator.getLocation(serviceBundle) );
            names[i--] = targetedPid.toString();

            for ( String candidate : names )
            {
                ConfigurationImpl config = getConfiguration( candidate );
                if ( config != null && !config.isDeleted() )
                {
                    // check visibility to use and dynamically bind
                    if ( canReceive( serviceBundle, config.getBundleLocation() ) )
                    {
                        config.tryBindLocation( Activator.getLocation(serviceBundle) );
                        return config;
                    }

                    // CM 1.4 / 104.13.2.2 / 104.5.3
                    // act as if there is no configuration
                    Log.logger.log(
                            LogService.LOG_DEBUG,
                            "Cannot use configuration {0} for {1}: No visibility to configuration bound to {2}; calling with null",
                            new Object[]
                                    { config.getPid(), target , config.getBundleLocation() } );
                }
            }
        }
        else
        {
            Log.logger.log( LogService.LOG_INFO,
                    "Service for PID {0} seems to already have been unregistered, not updating with configuration",
                    new Object[]
                            { rawPid } );
        }

        // service already unregistered, nothing to do really
        return null;
    }


    /**
     * Returns the {@link ConfigurationImpl} with the given PID if
     * available in the internal cache or from any persistence manager.
     * Otherwise <code>null</code> is returned.
     *
     * @param pid The PID for which to return the configuration
     * @return The configuration or <code>null</code> if non exists
     * @throws IOException If an error occurs reading from a persistence
     *      manager.
     */
    ConfigurationImpl getConfiguration( String pid ) throws IOException
    {
        ConfigurationImpl config = getCachedConfiguration( pid );
        if ( config != null )
        {
            Log.logger.log( LogService.LOG_DEBUG, "Found cached configuration {0} bound to {1}", new Object[]
                    { pid, config.getBundleLocation() } );

            config.ensureFactoryConfigPersisted();

            return config;
        }

		final Dictionary props = this.persistenceManager.load( pid );

        if ( props != null )
        {
            config = new ConfigurationImpl( this, this.persistenceManager, props );
            Log.logger.log( LogService.LOG_DEBUG, "Found existing configuration {0} bound to {1}", new Object[]
                    { pid, config.getBundleLocation() } );
            return cacheConfiguration( config );
        }

        // neither the cache nor the persistence manager has configuration
        return null;
    }


    /**
     * Creates a regular (non-factory) configuration for the given PID
     * setting the bundle location accordingly.
     * <p>
     * This method assumes the configuration to not exist yet and will
     * create it without further checking.
     *
     * @param pid The PID of the new configuration
     * @param bundleLocation The location to set on the new configuration.
     *      This may be <code>null</code> to not bind the configuration
     *      yet.
     * @return The new configuration persisted in the first persistence
     *      manager.
     * @throws IOException If an error occurrs writing the configuration
     *      to the persistence.
     */
    ConfigurationImpl createConfiguration( String pid, String bundleLocation ) throws IOException
    {
        // check for existing (cached or persistent) configuration
        ConfigurationImpl config = getConfiguration( pid );
        if ( config != null )
        {
            return config;
        }

        // else create new configuration also setting the bundle location
        // and cache the new configuration
        config = internalCreateConfiguration( pid, null, bundleLocation );
        return cacheConfiguration( config );
    }


    ConfigurationImpl[] listConfigurations( ConfigurationAdminImpl configurationAdmin, String filterString )
            throws IOException, InvalidSyntaxException
    {
        SimpleFilter filter = null;
        if ( filterString != null )
        {
            filter = SimpleFilter.parse( filterString );
        }

        Log.logger.log( LogService.LOG_DEBUG, "Listing configurations matching {0}", new Object[]
                { filterString } );

        List<ConfigurationImpl> configList = new ArrayList<>();

        Collection<Dictionary> configs = this.persistenceManager.getDictionaries(filter );
        for(final Dictionary config : configs)
        {
            // ignore non-Configuration dictionaries
            final String pid = ( String ) config.get( Constants.SERVICE_PID );
            if ( pid == null )
            {
                continue;
            }

            // CM 1.4 / 104.13.2.3 Permission required
            if ( !configurationAdmin.hasPermission( this,
                    ( String ) config.get( ConfigurationAdmin.SERVICE_BUNDLELOCATION ) ) )
            {
                Log.logger.log(
                        LogService.LOG_DEBUG,
                        "Omitting configuration {0}: No permission for bundle {1} on configuration bound to {2}",
                        new Object[]
                                { pid, Activator.getLocation(configurationAdmin.getBundle()),
                                        config.get( ConfigurationAdmin.SERVICE_BUNDLELOCATION ) } );
                continue;
            }

            // ensure the service.pid and returned a cached config if available
            ConfigurationImpl cfg = null;
            if ( this.persistenceManager instanceof CachingPersistenceManagerProxy)
            {
                cfg = getCachedConfiguration( pid );
                if (cfg == null) {
                    cfg = new ConfigurationImpl(this, this.persistenceManager, config);
                    // add the to configurations cache if it wasn't in the cache
                    cacheConfiguration(cfg);
                }
            } else {
                cfg = new ConfigurationImpl( this, this.persistenceManager, config );
            }

            // FELIX-611: Ignore configuration objects without props
            if ( !cfg.isNew() )
            {
                Log.logger.log( LogService.LOG_DEBUG, "Adding configuration {0}", new Object[]
                        { pid } );
                configList.add( cfg );
            }
            else
            {
                Log.logger.log( LogService.LOG_DEBUG, "Omitting configuration {0}: Is new", new Object[]
                        { pid } );
            }
        }

        if ( configList.size() == 0 )
        {
            return null;
        }
        return configList.toArray( new ConfigurationImpl[configList
                                                         .size()] );
    }


    void deleted( ConfigurationImpl config )
    {
        // remove the configuration from the cache
        removeConfiguration( config );
        fireConfigurationEvent( ConfigurationEvent.CM_DELETED, config.getPidString(), config.getFactoryPidString() );
        final Runnable task = new DeleteConfiguration( config );
        if ( this.coordinator == null || !CoordinatorUtil.addToCoordination(this.coordinator, updateThread, task) )
        {
            updateThread.schedule( task );
        }
        Log.logger.log( LogService.LOG_DEBUG, "DeleteConfiguration({0}) scheduled", new Object[]
                { config.getPid() } );
    }


    void updated( ConfigurationImpl config, boolean fireEvent )
    {
        if ( fireEvent )
        {
            fireConfigurationEvent( ConfigurationEvent.CM_UPDATED, config.getPidString(), config.getFactoryPidString() );
        }
        final Runnable task = new UpdateConfiguration( config );
        if ( this.coordinator == null || !CoordinatorUtil.addToCoordination(this.coordinator, updateThread, task) )
        {
            updateThread.schedule( task );
        }
        Log.logger.log( LogService.LOG_DEBUG, "UpdateConfiguration({0}) scheduled", new Object[]
                { config.getPid() } );
    }


    void locationChanged( ConfigurationImpl config, String oldLocation )
    {
        fireConfigurationEvent( ConfigurationEvent.CM_LOCATION_CHANGED, config.getPidString(), config.getFactoryPidString() );
        if ( oldLocation != null && !config.isNew() )
        {
            final Runnable task = new LocationChanged( config, oldLocation );
            if ( this.coordinator == null || !CoordinatorUtil.addToCoordination(this.coordinator, updateThread, task) )
            {
                updateThread.schedule( task );
            }
            Log.logger.log( LogService.LOG_DEBUG, "LocationChanged({0}, {1}=>{2}) scheduled", new Object[]
                    { config.getPid(), oldLocation, config.getBundleLocation() } );
        }
        else
        {
            Log.logger.log( LogService.LOG_DEBUG,
                    "LocationChanged not scheduled for {0} (old location is null or configuration is new)", new Object[]
                            { config.getPid() } );
        }
    }


    void fireConfigurationEvent( int type, String pid, String factoryPid )
    {
        // prevent event senders
        FireConfigurationEvent asyncSender = new FireConfigurationEvent( this.configurationListenerTracker, type, pid,
                factoryPid );
        FireConfigurationEvent syncSender = new FireConfigurationEvent( this.syncConfigurationListenerTracker, type,
                pid, factoryPid );

        // send synchronous events
        if ( syncSender.hasConfigurationEventListeners() )
        {
            syncSender.run();
        }
        else
        {
            Log.logger.log( LogService.LOG_DEBUG, "No SynchronousConfigurationListeners to send {0} event to.", new Object[]
                    { syncSender.getTypeName() } );
        }

        // schedule asynchronous events
        if ( asyncSender.hasConfigurationEventListeners() )
        {
            if ( this.coordinator == null || !CoordinatorUtil.addToCoordination(this.coordinator, eventThread, asyncSender) )
            {
                eventThread.schedule( asyncSender );
            }
        }
        else
        {
            Log.logger.log( LogService.LOG_DEBUG, "No ConfigurationListeners to send {0} event to.", new Object[]
                    { asyncSender.getTypeName() } );
        }
    }


    // ---------- BundleListener -----------------------------------------------

    @Override
    public void bundleChanged( BundleEvent event )
    {
        if ( event.getType() == BundleEvent.UNINSTALLED && handleBundleEvents )
        {
            final String location = Activator.getLocation(event.getBundle());

            // we only reset dynamic bindings, which are only present in
            // cached configurations, hence only consider cached configs here
            final ConfigurationImpl[] configs = getCachedConfigurations();
            for ( int i = 0; i < configs.length; i++ )
            {
                final ConfigurationImpl cfg = configs[i];
                if ( location.equals( cfg.getDynamicBundleLocation() ) )
                {
                    cfg.setDynamicBundleLocation( null, true );
                }
            }
        }
    }


    // ---------- internal -----------------------------------------------------

    private ServiceReference<ConfigurationAdmin> getServiceReference()
    {
        ServiceRegistration<ConfigurationAdmin> reg = configurationAdminRegistration;
        if (reg != null) {
            return reg.getReference();
        }

        // probably called for firing an event during service registration
        // since we didn't get the service registration yet we use the
        // service registry to get our service reference
        BundleContext context = bundleContext;
        if ( context != null )
        {
            try
            {
                Collection<ServiceReference<ConfigurationAdmin>> refs = context.getServiceReferences( ConfigurationAdmin.class, null );
                if ( refs != null && !refs.isEmpty())
                {
                    for(final ServiceReference<ConfigurationAdmin> ref : refs)
                    {
                        if ( ref.getBundle().getBundleId() == context.getBundle().getBundleId() )
                        {
                            return ref;
                        }
                    }
                }
            }
            catch ( InvalidSyntaxException e )
            {
                // unexpected since there is no filter
            }
        }

        // service references
        return null;
    }


    /**
     * Configures the ManagedService and returns the service.pid
     * service property as a String[], which may be <code>null</code> if
     * the ManagedService does not have such a property.
     */
    /**
     * Configures the ManagedServiceFactory and returns the service.pid
     * service property as a String[], which may be <code>null</code> if
     * the ManagedServiceFactory does not have such a property.
     */
    /**
     * Schedules the configuration of the referenced service with
     * configuration for the given PID.
     *
     * @param pid The list of service PID of the configurations to be
     *      provided to the referenced service.
     * @param sr The <code>ServiceReference</code> to the service
     *      to be configured.
     * @param factory <code>true</code> If the service is considered to
     *      be a <code>ManagedServiceFactory</code>. Otherwise the service
     *      is considered to be a <code>ManagedService</code>.
     */
    public void configure( String[] pid, ServiceReference sr, final boolean factory, final ConfigurationMap<?> configs )
    {
        if ( Log.logger.isLogEnabled( LogService.LOG_DEBUG ) )
        {
            Log.logger.log( LogService.LOG_DEBUG, "configure(ManagedService {0})", new Object[]
                    { sr } );
        }

        Runnable r;
        if ( factory )
        {
            r = new ManagedServiceFactoryUpdate( pid, sr, configs );
        }
        else
        {
            r = new ManagedServiceUpdate( pid, sr, configs );
        }
        if ( this.coordinator == null || !CoordinatorUtil.addToCoordination(this.coordinator, updateThread, r) )
        {
            updateThread.schedule( r );
        }
        Log.logger.log( LogService.LOG_DEBUG, "[{0}] scheduled", new Object[]
                { r } );
    }


    /**
     * Factory method to create a new configuration object. The configuration
     * object returned is not stored in configuration cache and only persisted
     * if the <code>factoryPid</code> parameter is <code>null</code>.
     *
     * @param pid
     *            The PID of the new configuration object. Must not be
     *            <code>null</code>.
     * @param factoryPid
     *            The factory PID of the new configuration. Not
     *            <code>null</code> if the new configuration object belongs to a
     *            factory. The configuration object will not be persisted if
     *            this parameter is not <code>null</code>.
     * @param bundleLocation
     *            The bundle location of the bundle to which the configuration
     *            belongs or <code>null</code> if the configuration is not bound
     *            yet.
     * @return The new configuration object
     * @throws IOException
     *             May be thrown if an error occurrs persisting the new
     *             configuration object.
     */
    private ConfigurationImpl internalCreateConfiguration( String pid, String factoryPid, String bundleLocation ) throws IOException
    {
        Log.logger.log( LogService.LOG_DEBUG, "createConfiguration({0}, {1}, {2})", new Object[]
                { pid, factoryPid, bundleLocation } );
        return new ConfigurationImpl( this, this.persistenceManager, pid, factoryPid, bundleLocation );
    }


    /**
     * Returns a list of {@link Factory} instances according to the
     * Configuration Admin 1.5 specification for targeted PIDs (Section
     * 104.3.2)
     *
     * @param rawFactoryPid The raw factory PID without any targettng.
     * @param target The <code>ServiceReference</code> of the service to
     *      be supplied with targeted configuration.
     * @return A list of {@link Factory} instances as listed above. This
     *      list will always at least include an instance for the
     *      <code>rawFactoryPid</code>. Other instances are only included
     *      if existing.
     * @throws IOException If an error occurs reading any of the
     *      {@link Factory} instances from persistence
     */
    List<String> getTargetedFactories( final String rawFactoryPid, final ServiceReference target ) throws IOException
    {
        List<String> factories = new LinkedList<>();

        final Bundle serviceBundle = target.getBundle();
        if ( serviceBundle != null )
        {
            final StringBuilder targetedPid = new StringBuilder( rawFactoryPid );
            factories.add( targetedPid.toString() );

            targetedPid.append( '|' ).append( serviceBundle.getSymbolicName() );
            factories.add( 0, targetedPid.toString() );

            targetedPid.append( '|' ).append( serviceBundle.getVersion().toString() );
            factories.add( 0, targetedPid.toString() );

            targetedPid.append( '|' ).append( Activator.getLocation(serviceBundle) );
            factories.add( 0, targetedPid.toString() );
        }

        return factories;
    }

    /**
     * Calls the registered configuration plugins on the given configuration
     * properties from the given configuration object.
     * <p>
     * The plugins to be called are selected as <code>ConfigurationPlugin</code>
     * services registered with a <code>cm.target</code> property set to
     * <code>*</code> or the factory PID of the configuration (for factory
     * configurations) or the PID of the configuration (for non-factory
     * configurations).
     *
     * @param props The configuration properties run through the registered
     *          ConfigurationPlugin services. This must not be
     *          <code>null</code>.
     * @param sr The service reference of the managed service (factory) which
     *          is to be updated with configuration
     * @param configPid The PID of the configuration object whose properties
     *          are to be augmented
     * @param factoryPid the factory PID of the configuration object whose
     *          properties are to be augmented. This is non-<code>null</code>
     *          only for a factory configuration.
     */
    public void callPlugins( final Dictionary<String, Object> props, final ServiceReference<?> sr, final String configPid,
            final String factoryPid )
    {
        ServiceReference<?>[] plugins = null;
        try
        {
            final String targetPid = (factoryPid == null) ? configPid : factoryPid;
            String filter = "(|(!(cm.target=*))(cm.target=" + targetPid + "))";
            plugins = bundleContext.getServiceReferences( ConfigurationPlugin.class.getName(), filter );
        }
        catch ( InvalidSyntaxException ise )
        {
            // no filter, no exception ...
        }

        // abort early if there are no plugins
        if ( plugins == null || plugins.length == 0 )
        {
            return;
        }

        // sort the plugins by their service.cmRanking
        if ( plugins.length > 1 )
        {
            Arrays.sort( plugins, RankingComparator.CM_RANKING );
        }

        // call the plugins in order
        for ( int i = 0; i < plugins.length; i++ )
        {
            ServiceReference<?> pluginRef = plugins[i];
            ConfigurationPlugin plugin = ( ConfigurationPlugin ) bundleContext.getService( pluginRef );
            if ( plugin != null )
            {
                // if cmRanking is below 0 or above 1000, ignore modifications from the plugin
                boolean ignore = false;
                Object rankObj = pluginRef.getProperty( ConfigurationPlugin.CM_RANKING );
                if ( rankObj instanceof Integer )
                {
                    final int ranking = ( ( Integer ) rankObj ).intValue();
                    ignore = (ranking < 0 ) || (ranking > 1000);
                }

                try
                {
                    plugin.modifyConfiguration( sr, ignore ? CaseInsensitiveDictionary.unmodifiable(props) : props );
                }
                catch ( Throwable t )
                {
                    Log.logger.log( LogService.LOG_ERROR, "Unexpected problem calling configuration plugin {0}", new Object[]
                            { pluginRef , t } );
                }
                finally
                {
                    // ensure ungetting the plugin
                    bundleContext.ungetService( pluginRef );
                }
                ConfigurationImpl.setAutoProperties( props, configPid, factoryPid );
            }
        }
    }


    /**
     * Creates a PID for the given factoryPid
     *
     * @param factoryPid
     * @return
     */
    private static String createPid( String factoryPid )
    {
        Random ng = numberGenerator;
        if ( ng == null )
        {
            // FELIX-2771 Secure Random not available on Mika
            try
            {
                ng = new SecureRandom();
            }
            catch ( Throwable t )
            {
                // fall back to Random
                ng = new Random();
            }
        }

        byte[] randomBytes = new byte[16];
        ng.nextBytes( randomBytes );
        randomBytes[6] &= 0x0f; /* clear version */
        randomBytes[6] |= 0x40; /* set to version 4 */
        randomBytes[8] &= 0x3f; /* clear variant */
        randomBytes[8] |= 0x80; /* set to IETF variant */

        StringBuilder buf = new StringBuilder( factoryPid.length() + 1 + 36 );

        // prefix the new pid with the factory pid
        buf.append( factoryPid ).append( "~" );

        // serialize the UUID into the buffer
        for ( int i = 0; i < randomBytes.length; i++ )
        {

            if ( i == 4 || i == 6 || i == 8 || i == 10 )
            {
                buf.append( '-' );
            }

            int val = randomBytes[i] & 0xff;
            buf.append( Integer.toHexString( val >> 4 ) );
            buf.append( Integer.toHexString( val & 0xf ) );
        }

        return buf.toString();
    }


    /**
     * Checks whether the bundle is allowed to receive the configuration
     * with the given location binding.
     * <p>
     * This method implements the logic defined CM 1.4 / 104.4.1:
     * <ul>
     * <li>If the location is <code>null</code> (the configuration is not
     * bound yet), assume the bundle is allowed</li>
     * <li>If the location is a single location (no leading "?"), require
     * the bundle's location to match</li>
     * <li>If the location is a multi-location (leading "?"), assume the
     * bundle is allowed if there is no security manager. If there is a
     * security manager, check whether the bundle has "target" permission
     * on this location.</li>
     * </ul>
     */
    boolean canReceive( final Bundle bundle, final String location )
    {
        if ( location == null )
        {
            Log.logger.log( LogService.LOG_DEBUG, "canReceive=true; bundle={0}; configuration=(unbound)", new Object[]
                    { Activator.getLocation(bundle) } );
            return true;
        }
        else if ( location.startsWith( "?" ) )
        {
            // multi-location
            if ( System.getSecurityManager() != null )
            {
                final boolean hasPermission = bundle.hasPermission( new ConfigurationPermission( location,
                        ConfigurationPermission.TARGET ) );
                Log.logger.log( LogService.LOG_DEBUG, "canReceive={0}: bundle={1}; configuration={2} (SecurityManager check)",
                        new Object[]
                                { new Boolean( hasPermission ), Activator.getLocation(bundle), location } );
                return hasPermission;
            }

            Log.logger.log( LogService.LOG_DEBUG, "canReceive=true; bundle={0}; configuration={1} (no SecurityManager)",
                    new Object[]
                            { Activator.getLocation(bundle), location } );
            return true;
        }
        else
        {
            // single location, must match
            final boolean hasPermission = location.equals( Activator.getLocation(bundle) );
            Log.logger.log( LogService.LOG_DEBUG, "canReceive={0}: bundle={1}; configuration={2}", new Object[]
                    { new Boolean( hasPermission ), Activator.getLocation(bundle), location } );
            return hasPermission;
        }
    }


    // ---------- inner classes

    /**
     * The <code>ManagedServiceUpdate</code> updates a freshly registered
     * <code>ManagedService</code> with a specific configuration. If a
     * ManagedService is registered with multiple PIDs an instance of this
     * class is used for each registered PID.
     */
    public class ManagedServiceUpdate implements Runnable
    {
        public final List<String> pids = new ArrayList<>();

        private final ServiceReference<ManagedService> sr;

        private final ConfigurationMap<?> configs;


        ManagedServiceUpdate( String[] pids, ServiceReference<ManagedService> sr, ConfigurationMap<?> configs )
        {
            for(final String pid : pids) {
                this.pids.add(pid);
            }
            this.sr = sr;
            this.configs = configs;
        }


        @Override
        public void run()
        {
            for ( String pid : this.pids )
            {
                try
                {
                    final ConfigurationImpl config = getTargetedConfiguration( pid, this.sr );
                    provide( pid, config );
                }
                catch ( IOException ioe )
                {
                    Log.logger.log( LogService.LOG_ERROR, "Error loading configuration for {0}", new Object[]
                            { pid, ioe } );
                }
                catch ( Exception e )
                {
                    Log.logger.log( LogService.LOG_ERROR, "Unexpected problem providing configuration {0} to service {1}",
                            new Object[]
                                    { pid, this.sr, e } );
                }
            }
        }


        private void provide(final String servicePid, final ConfigurationImpl config)
        {
            // check configuration
            final TargetedPID configPid;
            final Dictionary<String, Object> properties;
            final long revision;
            if ( config != null )
            {
                synchronized ( config )
                {
                    configPid = config.getPid();
                    properties = config.getProperties( true );
                    revision = config.getRevision();
                }
            }
            else
            {
                // 104.5.3 ManagedService.updated must be called with null
                // if no configuration is available
                configPid = new TargetedPID( servicePid );
                properties = null;
                revision = -1;
            }

            Log.logger.log( LogService.LOG_DEBUG, "Updating service {0} with configuration {1}@{2}", new Object[]
                    { servicePid, configPid, revision } );

            managedServiceTracker.provideConfiguration( sr, configPid, null, properties, revision, this.configs );
        }

        @Override
        public String toString()
        {
            return "ManagedService Update: pid=" + pids;
        }
    }

    /**
     * The <code>ManagedServiceFactoryUpdate</code> updates a freshly
     * registered <code>ManagedServiceFactory</code> with a specific
     * configuration. If a ManagedServiceFactory is registered with
     * multiple PIDs an instance of this class is used for each registered
     * PID.
     */
    public class ManagedServiceFactoryUpdate implements Runnable
    {
        private final String[] factoryPids;

        private final ServiceReference<ManagedServiceFactory> sr;

        private final ConfigurationMap<?> configs;


        ManagedServiceFactoryUpdate( String[] factoryPids, ServiceReference<ManagedServiceFactory> sr, final ConfigurationMap<?> configs )
        {
            this.factoryPids = factoryPids;
            this.sr = sr;
            this.configs = configs;
        }


        @Override
        public void run()
        {
            for ( String factoryPid : this.factoryPids )
            {

                try
                {
                    final List<String> targetedFactoryPids = getTargetedFactories( factoryPid, sr );
                    final Set<String> pids = persistenceManager.getFactoryConfigurationPids(targetedFactoryPids);
                    for ( final String pid : pids )
                    {
                        ConfigurationImpl cfg;
                        try
                        {
                            cfg = getConfiguration( pid );
                        }
                        catch ( IOException ioe )
                        {
                            Log.logger.log( LogService.LOG_ERROR, "Error loading configuration for {0}", new Object[]
                                    { pid, ioe } );
                            continue;
                        }

                        // sanity check on the configuration
                        if ( cfg == null )
                        {
                            Log.logger.log( LogService.LOG_ERROR,
                                    "Configuration {0} referred to by factory {1} does not exist", new Object[]
                                            { pid, factoryPid } );
                            continue;
                        }
                        else if ( cfg.isNew() )
                        {
                            // Configuration has just been created but not yet updated
                            // we currently just ignore it and have the update mechanism
                            // provide the configuration to the ManagedServiceFactory
                            // As of FELIX-612 (not storing new factory configurations)
                            // this should not happen. We keep this for added stability
                            // but raise the logging level to error.
                            Log.logger.log( LogService.LOG_ERROR, "Ignoring new configuration pid={0}", new Object[]
                                    { pid } );
                            continue;
                        }

                        provide( factoryPid, cfg );
                    }
                }
                catch ( IOException ioe )
                {
                    Log.logger.log( LogService.LOG_ERROR, "Cannot get factory mapping for factory PID {0}", new Object[]
                            { factoryPid, ioe } );
                }
            }
        }


        private void provide(final String factoryPid, final ConfigurationImpl config) {

            final Dictionary<String, Object> rawProperties;
            final long revision;
            synchronized ( config )
            {
                rawProperties = config.getProperties( true );
                revision = config.getRevision();
            }

            Log.logger.log( LogService.LOG_DEBUG, "Updating service {0} with configuration {1}/{2}@{3}", new Object[]
                    { factoryPid, config.getFactoryPid(), config.getPid(), new Long( revision ) } );

            // CM 1.4 / 104.13.2.1
            final Bundle serviceBundle = this.sr.getBundle();
            if ( serviceBundle == null )
            {
                Log.logger.log(
                        LogService.LOG_INFO,
                        "ManagedServiceFactory for factory PID {0} seems to already have been unregistered, not updating with factory",
                        new Object[]
                                { factoryPid } );
                return;
            }

            if ( !canReceive( serviceBundle, config.getBundleLocation() ) )
            {
                Log.logger.log( LogService.LOG_ERROR,
                        "Cannot use configuration {0} for {1}: No visibility to configuration bound to {2}",
                        new Object[]
                                { config.getPid(), sr , config.getBundleLocation() } );

                // no service, really, bail out
                return;
            }

            // 104.4.2 Dynamic Binding
            config.tryBindLocation( Activator.getLocation(serviceBundle) );

            // update the service with the configuration (if non-null)
            if ( rawProperties != null )
            {
                Log.logger.log( LogService.LOG_DEBUG, "{0}: Updating configuration pid={1}", new Object[]
                        { sr, config.getPid() } );
                managedServiceFactoryTracker.provideConfiguration( sr, config.getPid(), config.getFactoryPid(),
                        rawProperties, revision, this.configs );
            }
        }


        @Override
        public String toString()
        {
            return "ManagedServiceFactory Update: factoryPid=" + Arrays.asList( this.factoryPids );
        }
    }

    public abstract class ConfigurationProvider<T> implements Runnable
    {

        protected final ConfigurationImpl config;
        protected final long revision;
        protected final Dictionary<String, ?> properties;
        private BaseTracker<T> helper;


        protected ConfigurationProvider( final ConfigurationImpl config )
        {
            synchronized ( config )
            {
                this.config = config;
                this.revision = config.getRevision();
                this.properties = config.getProperties( true );
            }
        }


        protected TargetedPID getTargetedServicePid()
        {
            final TargetedPID factoryPid = this.config.getFactoryPid();
            if ( factoryPid != null )
            {
                return factoryPid;
            }
            return this.config.getPid();
        }


        protected BaseTracker<T> getHelper()
        {
            if ( this.helper == null )
            {
                this.helper = ( BaseTracker<T> ) ( ( this.config.getFactoryPid() == null ) ? ConfigurationManager.this.managedServiceTracker
                        : ConfigurationManager.this.managedServiceFactoryTracker );
            }
            return this.helper;
        }


        protected boolean provideReplacement( ServiceReference<T> sr )
        {
            if ( this.config.getFactoryPid() == null )
            {
                try
                {
                    final String configPidString = this.getHelper().getServicePid( sr, this.config.getPid() );
                    if (configPidString == null) {
                        return false; // The managed service is not registered anymore in the OSGi service registry.
                    }
                    final ConfigurationImpl rc = getTargetedConfiguration( configPidString, sr );
                    if ( rc != null )
                    {
                        final TargetedPID configPid;
                        final Dictionary<String, Object> properties;
                        final long revision;
                        synchronized ( rc )
                        {
                            configPid = rc.getPid();
                            properties = rc.getProperties( true );
                            revision = rc.getRevision();
                        }

                        this.getHelper().provideConfiguration( sr, configPid, null, properties, -revision, null );

                        return true;
                    }
                }
                catch ( IOException ioe )
                {
                    Log.logger.log( LogService.LOG_ERROR, "Error loading configuration for {0}", new Object[]
                            { this.config.getPid(), ioe } );
                }
                catch ( Exception e )
                {
                    Log.logger.log( LogService.LOG_ERROR, "Unexpected problem providing configuration {0} to service {1}",
                            new Object[]
                                    { this.config.getPid(), sr, e } );
                }
            }

            // factory or no replacement available
            return false;
        }
    }

    /**
     * The <code>UpdateConfiguration</code> is used to update
     * <code>ManagedService[Factory]</code> services with the configuration
     * they are subscribed to. This may cause the configuration to be
     * supplied to multiple services.
     */
    public class UpdateConfiguration extends ConfigurationProvider
    {

        UpdateConfiguration( final ConfigurationImpl config )
        {
            super( config );
        }


        @Override
        public void run()
        {
            Log.logger.log( LogService.LOG_DEBUG, "Updating configuration {0} to revision #{1}", new Object[]
                    { config.getPid(), revision } );

            final List<ServiceReference<?>> srList = this.getHelper().getServices( getTargetedServicePid() );
            if ( !srList.isEmpty() )
            {
                // optionally bind dynamically to the first service
                Bundle bundle = srList.get(0).getBundle();
                if (bundle == null) {
                    Log.logger.log( LogService.LOG_DEBUG,
                            "Service {0} seems to be unregistered concurrently (not providing configuration)",
                            new Object[]
                                    { srList.get(0) } );
                    return;
                }
                config.tryBindLocation( Activator.getLocation(bundle) );

                final String configBundleLocation = config.getBundleLocation();

                // provide configuration to all services from the
                // correct bundle
                for (ServiceReference<?> ref : srList)
                {
                    final Bundle refBundle = ref.getBundle();
                    if ( refBundle == null )
                    {
                        Log.logger.log( LogService.LOG_DEBUG,
                                "Service {0} seems to be unregistered concurrently (not providing configuration)",
                                new Object[]
                                        { ref } );
                    }
                    else if ( canReceive( refBundle, configBundleLocation ) )
                    {
                        this.getHelper().provideConfiguration( ref, this.config.getPid(), this.config.getFactoryPid(),
                                this.properties, this.revision, null );
                    }
                    else
                    {
                        // CM 1.4 / 104.13.2.2
                        Log.logger.log( LogService.LOG_ERROR,
                                "Cannot use configuration {0} for {1}: No visibility to configuration bound to {2}",
                                new Object[]
                                        { config.getPid(), ref, configBundleLocation } );
                    }

                }
            }
            else if ( Log.logger.isLogEnabled( LogService.LOG_DEBUG ) )
            {
                Log.logger.log( LogService.LOG_DEBUG, "No ManagedService[Factory] registered for updates to configuration {0}",
                        new Object[]
                                { config.getPid() } );
            }
        }


        @Override
        public String toString()
        {
            return "Update: pid=" + config.getPid();
        }
    }


    /**
     * The <code>DeleteConfiguration</code> class is used to inform
     * <code>ManagedService[Factory]</code> services of a configuration
     * being deleted.
     */
    public class DeleteConfiguration extends ConfigurationProvider
    {

        private final String configLocation;


        DeleteConfiguration( ConfigurationImpl config )
        {
            /*
             * NOTE: We keep the configuration because it might be cleared just
             * after calling this method. The pid and factoryPid fields are
             * final and cannot be reset.
             */
            super(config);
            this.configLocation = config.getBundleLocation();
        }


        @Override
        public void run()
        {
            List<ServiceReference<?>> srList = this.getHelper().getServices( getTargetedServicePid() );
            if ( !srList.isEmpty() )
            {
                for (ServiceReference<?> sr : srList)
                {
                    final Bundle srBundle = sr.getBundle();
                    if ( srBundle == null )
                    {
                        Log.logger.log( LogService.LOG_DEBUG,
                                "Service {0} seems to be unregistered concurrently (not removing configuration)",
                                new Object[]
                                        { sr } );
                    }
                    else if ( canReceive( srBundle, configLocation ) )
                    {
                        // revoke configuration unless a replacement
                        // configuration can be provided
                        if ( !this.provideReplacement( sr ) )
                        {
                            this.getHelper().removeConfiguration( sr, this.config.getPid(), this.config.getFactoryPid() );
                        }
                    }
                    else
                    {
                        // CM 1.4 / 104.13.2.2
                        Log.logger.log( LogService.LOG_ERROR,
                                "Cannot remove configuration {0} for {1}: No visibility to configuration bound to {2}",
                                new Object[]
                                        { config.getPid(), sr, configLocation } );
                    }
                }
            }
        }

        @Override
        public String toString()
        {
            return "Delete: pid=" + config.getPid();
        }
    }

    public class LocationChanged extends ConfigurationProvider
    {
        private final String oldLocation;


        LocationChanged( ConfigurationImpl config, String oldLocation )
        {
            super( config );
            this.oldLocation = oldLocation;
        }


        @Override
        public void run()
        {
            List<ServiceReference<?>> srList = this.getHelper().getServices( getTargetedServicePid() );
            if ( !srList.isEmpty() )
            {
                for (final ServiceReference<?> sr : srList)
                {
                    final Bundle srBundle = sr.getBundle();
                    if ( srBundle == null )
                    {
                        Log.logger.log( LogService.LOG_DEBUG,
                                "Service {0} seems to be unregistered concurrently (not processing)", new Object[]
                                        { sr } );
                        continue;
                    }

                    final boolean wasVisible = canReceive( srBundle, oldLocation );
                    final boolean isVisible = canReceive( srBundle, config.getBundleLocation() );

                    // make sure the config is dynamically bound to the first
                    // service if the config has been unbound causing this update
                    if ( isVisible )
                    {
                        config.tryBindLocation( Activator.getLocation(srBundle) );
                    }

                    if ( wasVisible && !isVisible )
                    {
                        // revoke configuration unless a replacement
                        // configuration can be provided
                        if ( !this.provideReplacement( sr ) )
                        {
                            this.getHelper().removeConfiguration( sr, this.config.getPid(), this.config.getFactoryPid() );
                            Log.logger.log( LogService.LOG_DEBUG, "Configuration {0} revoked from {1} (no more visibility)",
                                    new Object[]
                                            { config.getPid(), sr } );
                        }
                    }
                    else if ( !wasVisible && isVisible )
                    {
                        // call updated method
                        this.getHelper().provideConfiguration( sr, this.config.getPid(), this.config.getFactoryPid(),
                                this.properties, this.revision, null );
                        Log.logger.log( LogService.LOG_DEBUG, "Configuration {0} provided to {1} (new visibility)", new Object[]
                                { config.getPid(), sr } );
                    }
                    else
                    {
                        // same visibility as before
                        Log.logger.log( LogService.LOG_DEBUG, "Unmodified visibility to configuration {0} for {1}", new Object[]
                                { config.getPid(), sr } );
                    }
                }
            }
        }


        @Override
        public String toString()
        {
            return "Location Changed (pid=" + config.getPid() + "): " + oldLocation + " ==> "
                    + config.getBundleLocation();
        }
    }

    private class FireConfigurationEvent implements Runnable
    {
        private final int type;

        private final String pid;

        private final String factoryPid;

        private final ServiceReference[] listenerReferences;

        private final ConfigurationListener[] listeners;

        private final Bundle[] listenerProvider;

        private ConfigurationEvent event;

        private FireConfigurationEvent( final ServiceTracker listenerTracker, final int type, final String pid, final String factoryPid)
        {
            this.type = type;
            this.pid = pid;
            this.factoryPid = factoryPid;

            final ServiceReference[] srs = listenerTracker.getServiceReferences();
            if ( srs == null || srs.length == 0 )
            {
                this.listenerReferences = null;
                this.listeners = null;
                this.listenerProvider = null;
            }
            else
            {
				Arrays.sort(srs, Comparator.reverseOrder());

                this.listenerReferences = srs;
                this.listeners = new ConfigurationListener[srs.length];
                this.listenerProvider = new Bundle[srs.length];
                for ( int i = 0; i < srs.length; i++ )
                {
                    this.listeners[i] = ( ConfigurationListener ) listenerTracker.getService( srs[i] );
                    this.listenerProvider[i] = srs[i].getBundle();
                }
            }
        }


        boolean hasConfigurationEventListeners()
        {
            return this.listenerReferences != null;
        }


        String getTypeName()
        {
            switch ( type )
            {
            case ConfigurationEvent.CM_DELETED:
                return "CM_DELETED";
            case ConfigurationEvent.CM_UPDATED:
                return "CM_UPDATED";
            case ConfigurationEvent.CM_LOCATION_CHANGED:
                return "CM_LOCATION_CHANGED";
            default:
                return "<UNKNOWN(" + type + ")>";
            }
        }


        @Override
        public void run()
        {
            for ( int i = 0; i < listeners.length; i++ )
            {
                sendEvent( i );
            }
        }


        @Override
        public String toString()
        {
            return "Fire ConfigurationEvent: pid=" + pid;
        }


        private ConfigurationEvent getConfigurationEvent(ServiceReference<ConfigurationAdmin> serviceReference)
        {
            if ( event == null )
            {
                this.event = new ConfigurationEvent( serviceReference, type, factoryPid, pid );
            }
            return event;
        }


        private void sendEvent( final int serviceIndex )
        {
            if ( (listenerProvider[serviceIndex] != null)
                    && (listenerProvider[serviceIndex].getState() & (Bundle.ACTIVE | Bundle.STARTING)) > 0
                    && this.listeners[serviceIndex] != null )
            {
                Log.logger.log( LogService.LOG_DEBUG, "Sending {0} event for {1} to {2}", new Object[]
                        { getTypeName(), pid, listenerReferences[serviceIndex]} );

                final ServiceReference<ConfigurationAdmin> serviceReference = getServiceReference();

                if (serviceReference == null)
                {
                    Log.logger.log( LogService.LOG_WARNING, "No ConfigurationAdmin for delivering configuration event to {0}", new Object[]
                            { listenerReferences[serviceIndex] } );

                    return;
                }

                try
                {
                    if ( System.getSecurityManager() != null )
                    {
                        AccessController.doPrivileged(
                            new PrivilegedAction<Object>()
                            {
                                @Override
                                public Void run()
                                {
                                    listeners[serviceIndex].configurationEvent(getConfigurationEvent(serviceReference));
                                    return null;
                                }
                            }, BaseTracker.getAccessControlContext(listenerProvider[serviceIndex])
                        );
                    }
                    else
                    {
                        listeners[serviceIndex].configurationEvent(getConfigurationEvent(serviceReference));
                    }
                }
                catch ( Throwable t )
                {
                    Log.logger.log( LogService.LOG_ERROR, "Unexpected problem delivering configuration event to {0}", new Object[]
                            { listenerReferences[serviceIndex], t } );
                }
                finally
                {
                    this.listeners[serviceIndex] = null;
                }
            }
        }
    }

    public void setCoordinator(final Object service)
    {
        this.coordinator = service;
    }

    public void updateRegisteredConfigurationPlugins(final String propValue) {
        final ServiceRegistration<ConfigurationAdmin> localReg = this.configurationAdminRegistration;
        if (localReg != null) {
            serviceProperties.put("config.plugins", propValue);
            localReg.setProperties(serviceProperties);
        }
    }
}
/* @generated */
