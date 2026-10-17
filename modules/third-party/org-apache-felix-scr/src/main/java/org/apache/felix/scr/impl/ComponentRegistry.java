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
package org.apache.felix.scr.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.felix.scr.impl.inject.ComponentMethods;
import org.apache.felix.scr.impl.inject.ComponentMethodsImpl;
import org.apache.felix.scr.impl.logger.ComponentLogger;
import org.apache.felix.scr.impl.logger.ScrLogger;
import org.apache.felix.scr.impl.manager.AbstractComponentManager;
import org.apache.felix.scr.impl.manager.ComponentActivator;
import org.apache.felix.scr.impl.manager.ComponentHolder;
import org.apache.felix.scr.impl.manager.ConfigurableComponentHolder;
import org.apache.felix.scr.impl.manager.DependencyManager;
import org.apache.felix.scr.impl.manager.RegionConfigurationSupport;
import org.apache.felix.scr.impl.manager.ScrConfiguration;
import org.apache.felix.scr.impl.metadata.ComponentMetadata;
import org.apache.felix.scr.impl.metadata.TargetedPID;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentException;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.log.LogService;


/**
 * The <code>ComponentRegistry</code> class acts as the global registry for
 * components by name and by component ID.
 */
public class ComponentRegistry
{
    /**
     * Service property for change count. This constant is defined here to avoid
     * a dependency on R7 of the framework.
     * The value of the property is of type {@code Long}.
     */
    private static String PROP_CHANGECOUNT = "service.changecount";


    /**
     * The map of known components indexed by component name. The values are
     * either null (for name reservations) or implementations
     * of the {@link ComponentHolder} interface.
     * <p>
     * The {@link #checkComponentName(String)} will first add an entry to this
     * map with null value to reserve the name. After setting up
     * the component, the {@link #registerComponentHolder(String, ComponentHolder)}
     * method replaces the value of the named entry with the actual
     * {@link ComponentHolder}.
     *
     * @see #checkComponentName(String)
     * @see #registerComponentHolder(String, ComponentHolder)
     * @see #unregisterComponentHolder(String)
     */
    private final Map<ComponentRegistryKey, ComponentHolder<?>> m_componentHoldersByName;

    /**
     * The map of known components indexed by component configuration pid. The values are
     * Sets of the {@link ComponentHolder} interface. Normally, the configuration pid
     * is the component name, but since DS 1.2 (OSGi 4.3), a component may specify a specific
     * pid, and it is possible that different components refer to the same pid. That's why
     * the values of this map are Sets of ComponentHolders, allowing to lookup all components
     * which are using a given configuration pid.
     * This map is used when the ConfigurationSupport detects that a CM pid is updated. When
     * a PID is updated, the ConfigurationSupport listener class invokes the
     * {@link #getComponentHoldersByPid(String)} method which returns an iterator over all
     * components that are using the given pid for configuration.
     * <p>
     *
     * @see #registerComponentHolder(String, ComponentHolder)
     * @see #unregisterComponentHolder(String)
     * @see RegionConfigurationSupport#configurationEvent(org.osgi.service.cm.ConfigurationEvent)
     */
    private final Map<String, Set<ComponentHolder<?>>> m_componentHoldersByPid;

    /**
     * Map of components by component ID. This map indexed by the component
     * ID number (<code>java.lang.Long</code>) contains the actual
     * {@link AbstractComponentManager} instances existing in the system.
     *
     * @see #registerComponentId(AbstractComponentManager)
     * @see #unregisterComponentId(long)
     */
    private final Map<Long, AbstractComponentManager<?>> m_componentsById;

    /**
     * Counter to setup the component IDs as issued by the
     * {@link #registerComponentId(AbstractComponentManager)} method. This
     * counter is only incremented.
     */
    private long m_componentCounter = -1;

    private final Map<ServiceReference<?>, List<Entry<?, ?>>> m_missingDependencies = new HashMap<>( );

    private final ScrLogger m_logger;

    private final ScrConfiguration m_configuration;

    public ComponentRegistry( final ScrConfiguration scrConfiguration, final ScrLogger logger )
    {
        m_configuration = scrConfiguration;
        m_logger = logger;
        m_componentHoldersByName = new HashMap<>();
        m_componentHoldersByPid = new HashMap<>();
        m_componentsById = new HashMap<>();

    }

    //---------- ComponentManager registration by component Id

    /**
     * Assigns a unique ID to the component, internally registers the
     * component under that ID and returns the assigned component ID.
     *
     * @param componentManager The {@link AbstractComponentManager} for which
     *      to assign a component ID and which is to be internally registered
     *
     * @return the assigned component ID
     */
    final long registerComponentId( final AbstractComponentManager<?> componentManager )
    {
        long componentId;
        synchronized ( m_componentsById )
        {
            componentId = ++m_componentCounter;

            m_componentsById.put( componentId, componentManager );
        }

        return componentId;
    }


    /**
     * Unregisters the component with the given component ID from the internal
     * registry. After unregistration, the component ID should be considered
     * invalid.
     *
     * @param componentId The ID of the component to be removed from the
     *      internal component registry.
     */
    final void unregisterComponentId( final long componentId )
    {
        synchronized ( m_componentsById )
        {
            m_componentsById.remove( componentId );
        }
    }


    //---------- ComponentHolder registration by component name

    /**
     * Checks whether the component name is "globally" unique or not. If it is
     * unique, it is reserved until the actual component is registered with
     * {@link #registerComponentHolder(String, ComponentHolder)} or until
     * it is unreserved by calling {@link #unregisterComponentHolder(String)}.
     * If a component with the same name has already been reserved or registered
     * a ComponentException is thrown with a descriptive message.
     *
     * @param bundle the bundle registering the component
     * @param name the component name to check and reserve
     * @throws ComponentException if the name is already in use by another
     *      component.
     */
    final ComponentRegistryKey checkComponentName( final Bundle bundle, final String name )
    {
        // register the name if no registration for that name exists already
        final ComponentRegistryKey key = new ComponentRegistryKey( bundle, name );
        ComponentHolder<?> existingRegistration = null;
        boolean present;
        synchronized ( m_componentHoldersByName )
        {
            present = m_componentHoldersByName.containsKey( key );
            if ( !present )
            {
                m_componentHoldersByName.put( key, null );
            }
            else
            {
                existingRegistration = m_componentHoldersByName.get( key );
            }
        }

        // there was a registration already, throw an exception and use the
        // existing registration to provide more information if possible
        if ( present )
        {
            String message = "The component name '" + name + "' has already been registered";

            if ( existingRegistration != null )
            {
                Bundle cBundle = existingRegistration.getActivator().getBundleContext().getBundle();
                ComponentMetadata cMeta = existingRegistration.getComponentMetadata();

                StringBuilder buf = new StringBuilder( message );
                buf.append( " by Bundle " ).append( cBundle.getBundleId() );
                if ( cBundle.getSymbolicName() != null )
                {
                    buf.append( " (" ).append( cBundle.getSymbolicName() ).append( ")" );
                }
                buf.append( " as Component of Class " ).append( cMeta.getImplementationClassName() );
                message = buf.toString();
            }

            throw new ComponentException( message );
        }

        return key;
    }


    /**
     * Registers the given component under the given name. If the name has not
     * already been reserved calling {@link #checkComponentName(String)} this
     * method throws a {@link ComponentException}.
     *
     * @param name The name to register the component under
     * @param componentHolder The component to register
     *
     * @throws ComponentException if the name has not been reserved through
     *      {@link #checkComponentName(String)} yet.
     */
    final void registerComponentHolder( final ComponentRegistryKey key, ComponentHolder<?> componentHolder )
    {
        m_logger.log(LogService.LOG_DEBUG,
                "Registering component with pid {0} for bundle {1}", null,
                componentHolder.getComponentMetadata().getConfigurationPid(), key.getBundleId());
        synchronized ( m_componentHoldersByName )
        {
            // only register the component if there is a m_registration for it !
            if ( m_componentHoldersByName.get( key ) != null )
            {
                // this is not expected if all works ok
                throw new ComponentException( "The component name '{0}" + componentHolder.getComponentMetadata().getName()
                    + "' has already been registered." );
            }

            m_componentHoldersByName.put( key, componentHolder );
        }

        synchronized (m_componentHoldersByPid)
        {
            // See if the component declares a specific configuration pid (112.4.4 configuration-pid)
            List<String> configurationPids = componentHolder.getComponentMetadata().getConfigurationPid();

            for ( String configurationPid: configurationPids )
            {
                // Since several components may refer to the same configuration pid, we have to
                // store the component holder in a Set, in order to be able to lookup every
                // components from a given pid.
                Set<ComponentHolder<?>> set = m_componentHoldersByPid.get( configurationPid );
                if ( set == null )
                {
                    set = new HashSet<>();
                    m_componentHoldersByPid.put( configurationPid, set );
                }
                set.add( componentHolder );
            }
        }
        this.updateChangeCount();
  }

    /**
     * Returns the component registered under the given name or <code>null</code>
     * if no component is registered yet.
     */
    public final ComponentHolder<?> getComponentHolder( final Bundle bundle, final String name )
    {
        synchronized ( m_componentHoldersByName )
        {
            return m_componentHoldersByName.get( new ComponentRegistryKey( bundle, name ) );
        }
    }

    /**
     * Returns the set of ComponentHolder instances whose configuration pids are matching
     * the given pid.
     * @param pid the pid candidate
     * @return the set of ComponentHolders matching the singleton pid supplied
     */
    public final Collection<ComponentHolder<?>> getComponentHoldersByPid(TargetedPID targetedPid)
    {
        String pid = targetedPid.getServicePid();
        Set<ComponentHolder<?>> componentHoldersUsingPid = new HashSet<>();
        synchronized (m_componentHoldersByPid)
        {
            Set<ComponentHolder<?>> set = m_componentHoldersByPid.get(pid);
            // only return the entry if non-null and not a reservation
            if (set != null)
            {
                for (ComponentHolder<?> holder: set)
                {
                    Bundle bundle = holder.getActivator().getBundleContext().getBundle();
                    if (targetedPid.matchesTarget(bundle))
                    {
                        componentHoldersUsingPid.add( holder );
                    }
                }
            }
        }
        return componentHoldersUsingPid;
    }

    /**
     * Returns an array of all values currently stored in the component holders
     * map. The entries in the array are either String types for component
     * name reservations or {@link ComponentHolder} instances for actual
     * holders of components.
     */
    public final List<ComponentHolder<?>> getComponentHolders()
    {
    	List<ComponentHolder<?>> all = new ArrayList<>();
        synchronized ( m_componentHoldersByName )
        {
            for (ComponentHolder<?> holder: m_componentHoldersByName.values())
            {
                // Ignore name reservations
                if (holder != null)
                {
                    all.add(holder);
                }
            }
        }
        return all;
    }

    public final List<ComponentHolder<?>> getComponentHolders(Bundle...bundles)
    {
    	List<ComponentHolder<?>> all =getComponentHolders();
        List<ComponentHolder<?>> holders = new ArrayList<>();
        for ( ComponentHolder<?> holder: all)
        {
        	ComponentActivator activator = holder.getActivator();
        	if (activator != null)
        	{
        	    try
        	    {
            		Bundle holderBundle = activator.getBundleContext().getBundle();
            		for (Bundle b: bundles)
            		{
            			if (b == holderBundle)
            			{
            				holders.add(holder);
            			}
            		}
        	    }
        	    catch ( IllegalStateException ise)
        	    {
        	        // ignore inactive bundles
        	    }
        	}
        }
        return holders;
    }


    /**
     * Removes the component registered under that name. If no component is
     * yet registered but the name is reserved, it is unreserved.
     * <p>
     * After calling this method, the name can be reused by other components.
     */
    final void unregisterComponentHolder( final Bundle bundle, final String name )
    {
        unregisterComponentHolder( new ComponentRegistryKey( bundle, name ) );
    }


    /**
     * Removes the component registered under that name. If no component is
     * yet registered but the name is reserved, it is unreserved.
     * <p>
     * After calling this method, the name can be reused by other components.
     */
    final void unregisterComponentHolder( final ComponentRegistryKey key )
    {
        ComponentHolder<?> component;
        synchronized ( m_componentHoldersByName )
        {
            component = m_componentHoldersByName.remove( key );
        }

        if (component != null) {
            m_logger.log(LogService.LOG_DEBUG,
                    "Unregistering component with pid {0} for bundle {1}", null,
                    component.getComponentMetadata().getConfigurationPid(), key.getBundleId());
            synchronized (m_componentHoldersByPid)
            {
                List<String> configurationPids = component.getComponentMetadata().getConfigurationPid();
                for ( String configurationPid: configurationPids )
                {
                    Set<ComponentHolder<?>> componentsForPid = m_componentHoldersByPid.get( configurationPid );
                    if ( componentsForPid != null )
                    {
                        componentsForPid.remove( component );
                        if ( componentsForPid.size() == 0 )
                        {
                            m_componentHoldersByPid.remove( configurationPid );
                        }
                    }
                }
            }
            this.updateChangeCount();
        }
    }

    //---------- base configuration support

    /**
     * Factory method to issue {@link ComponentHolder} instances to manage
     * components described by the given component <code>metadata</code>.
     */
    public <S> ComponentHolder<S> createComponentHolder( ComponentActivator activator, ComponentMetadata metadata, ComponentLogger logger )
    {
        return new DefaultConfigurableComponentHolder<>(activator, metadata, logger);
    }

    static class DefaultConfigurableComponentHolder<S> extends ConfigurableComponentHolder<S>
    {
        public DefaultConfigurableComponentHolder(ComponentActivator activator, ComponentMetadata metadata, ComponentLogger logger)
        {
            super(activator, metadata, logger);
        }

        @Override
        protected ComponentMethods<S> createComponentMethods()
        {
            return new ComponentMethodsImpl<>();
        }
    }


    //---------- ServiceListener

    //---------- Helper method

    private final ThreadLocal<List<ServiceReference<?>>> circularInfos = new ThreadLocal<List<ServiceReference<?>>> ()
    {

        @Override
        protected List<ServiceReference<?>> initialValue()
        {
            return new ArrayList<>();
        }
    };


    /**
     * Track getService calls by service reference.
     * @param serviceReference
     * @return true is we have encountered a circular dependency, false otherwise.
     */
    public <T> boolean enterCreate(final ServiceReference<T> serviceReference)
    {
        List<ServiceReference<?>> info = circularInfos.get();
        if (info.contains(serviceReference))
        {
            m_logger.log(LogService.LOG_ERROR,
                "Circular reference detected trying to get service {0}\n stack of references: {1}", new Exception("stack trace"),
                serviceReference, new Info(info));
            return true;
        }
        m_logger.log(LogService.LOG_DEBUG,
            "getService  {0}: stack of references: {1}", null,
            serviceReference, info);
        info.add(serviceReference);
        return false;
    }


    private class Info
    {

        private final List<ServiceReference<?>> info;


        public Info(List<ServiceReference<?>> info)
        {
            this.info = info;
        }


        @Override
        public String toString()
        {
        	StringBuilder sb = new StringBuilder();
            for (ServiceReference<?> sr: info)
            {
                sb.append("ServiceReference: ").append(sr).append("\n");
                List<Entry<?, ?>> entries = m_missingDependencies.get(sr);
                if (entries != null)
                {
                    for (Entry<?, ?> entry: entries)
                    {
                        sb.append("    Dependency: ").append(entry.getDm()).append("\n");
                    }
                }
            }
            return sb.toString();
        }

    }

    public <T> void leaveCreate(final ServiceReference<T> serviceReference)
    {
        List<ServiceReference<?>> info = circularInfos.get();
        if (info != null)
        {
            if (!info.isEmpty() && info.iterator().next().equals(serviceReference))
            {
                circularInfos.remove();
            }
            else
            {
                info.remove(serviceReference);
            }
        }

    }

    /**
     * Schedule late binding of now-available reference on a different thread.  The late binding cannot occur on this thread
     * due to service registry circular reference detection. We cannot wait for the late binding before returning from the initial
     * getService call because of synchronization in the service registry.
     * @param serviceReference
     * @param actor
     */
    public synchronized <T> void missingServicePresent( final ServiceReference<T> serviceReference, ComponentActorThread actor )
    {
        final List<Entry<?, ?>> dependencyManagers = m_missingDependencies.remove( serviceReference );
        if ( dependencyManagers != null )
        {

            Runnable runnable = new Runnable()
            {

                @Override
                @SuppressWarnings("unchecked")
                public void run()
                {
                    for ( Entry<?, ?> entry : dependencyManagers )
                    {
                        ((DependencyManager<?, T>)entry.getDm()).invokeBindMethodLate( serviceReference, entry.getTrackingCount() );
                    }
                    m_logger.log(LogService.LOG_DEBUG,
                        "Ran {0} asynchronously", null, this);
                }

                @Override
                public String toString()
                {
                    return "Late binding task of reference " + serviceReference + " for dependencyManagers " + dependencyManagers;
                }

            } ;
            m_logger.log(LogService.LOG_DEBUG,
                "Scheduling runnable {0} asynchronously", null, runnable);
            actor.schedule( runnable );
        }
    }

    public synchronized <S, T> void registerMissingDependency( DependencyManager<S, T> dependencyManager, ServiceReference<T> serviceReference, int trackingCount )
    {
        //check that the service reference is from scr
        if ( serviceReference.getProperty( ComponentConstants.COMPONENT_NAME ) == null || serviceReference.getProperty( ComponentConstants.COMPONENT_ID ) == null )
        {
            m_logger.log(LogService.LOG_DEBUG,
                "Missing service {0} for dependency manager {1} is not a DS service, cannot resolve circular dependency", null,
                serviceReference, dependencyManager);
            return;
        }
        List<Entry<?, ?>> dependencyManagers = m_missingDependencies.get( serviceReference );
        if ( dependencyManagers == null )
        {
            dependencyManagers = new ArrayList<>();
            m_missingDependencies.put( serviceReference, dependencyManagers );
        }
        dependencyManagers.add( new Entry<>( dependencyManager, trackingCount ) );
        m_logger.log(LogService.LOG_DEBUG,
            "Dependency managers {0} waiting for missing service {1}", null,
            dependencyManagers, serviceReference);
        }

    private static class Entry<S,T>
    {
        private final DependencyManager<S, T> dm;
        private final int trackingCount;

        private Entry( DependencyManager<S, T> dm, int trackingCount )
        {
            this.dm = dm;
            this.trackingCount = trackingCount;
        }

        public DependencyManager<S, T> getDm()
        {
            return dm;
        }

        public int getTrackingCount()
        {
            return trackingCount;
        }

        @Override
        public String toString()
        {
            return dm.toString() + "@" + trackingCount;
        }
    }

    private final ConcurrentMap<Long, RegionConfigurationSupport> bundleToRcsMap = new ConcurrentHashMap<>();

    public RegionConfigurationSupport registerRegionConfigurationSupport(
            ServiceReference<ConfigurationAdmin> reference) {
        RegionConfigurationSupport trialRcs = new RegionConfigurationSupport(m_logger, reference) {
            @Override
            protected Collection<ComponentHolder<?>> getComponentHolders(TargetedPID pid)
            {
                return ComponentRegistry.this.getComponentHoldersByPid(pid);
            }
        };
        return registerRegionConfigurationSupport(trialRcs);
    }

    public RegionConfigurationSupport registerRegionConfigurationSupport(
			RegionConfigurationSupport trialRcs) {
		Long bundleId = trialRcs.getBundleId();
		RegionConfigurationSupport existing = null;
		RegionConfigurationSupport previous = null;
		while (true)
		{
			existing = bundleToRcsMap.putIfAbsent(bundleId, trialRcs);
			if (existing == null)
			{
				trialRcs.start();
				return trialRcs;
			}
			if (existing == previous)
			{
				//the rcs we referenced is still current
				return existing;
			}
			if (existing.reference())
			{
				//existing can still be used
				previous = existing;
			}
			else
			{
				//existing was discarded in another thread, start over
				previous = null;
			}
		}
	}

	public void unregisterRegionConfigurationSupport(
			RegionConfigurationSupport rcs) {
		if (rcs.dereference())
		{
			bundleToRcsMap.remove(rcs.getBundleId());
		}

	}

	private final Object changeCountLock = new Object();

    private volatile long changeCount;

    private volatile Timer timer;

    private volatile ServiceRegistration<ServiceComponentRuntime> registration;

    public Dictionary<String, Object> getServiceRegistrationProperties()
    {
        final Dictionary<String, Object> props = new Hashtable<>();
        props.put(PROP_CHANGECOUNT, this.changeCount);

        return props;
    }

    public void setRegistration(final ServiceRegistration<ServiceComponentRuntime> reg)
    {
        this.registration = reg;
    }

    public void updateChangeCount()
    {
    }
}
/* @generated */