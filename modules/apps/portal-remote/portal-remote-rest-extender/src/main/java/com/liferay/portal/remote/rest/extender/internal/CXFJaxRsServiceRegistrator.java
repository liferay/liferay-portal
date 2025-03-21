/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.rest.extender.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.RuntimeDelegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.provider.json.JSONProvider;

/**
 * @author Carlos Sierra Andrés
 */
public class CXFJaxRsServiceRegistrator {

	public CXFJaxRsServiceRegistrator(Map<String, Object> properties) {
		_properties = properties;
	}

	public synchronized void addApplication(Application application) {
		_swapClassLoader(application, this::_addApplication);
	}

	public synchronized void addBus(Bus bus) {
		_swapClassLoader(bus, this::_addBus);
	}

	public synchronized void addProvider(Object provider) {
		_swapClassLoader(provider, this::_addProvider);
	}

	public synchronized void addService(Object service) {
		_swapClassLoader(service, this::_addService);
	}

	public synchronized void removeApplication(Application application) {
		_swapClassLoader(application, this::_removeApplication);
	}

	public synchronized void removeBus(Bus bus) {
		_swapClassLoader(bus, this::_removeBus);
	}

	public synchronized void removeProvider(Object provider) {
		_swapClassLoader(provider, this::_removeProvider);
	}

	public synchronized void removeService(Object service) {
		_swapClassLoader(service, this::_removeService);
	}

	private void _addApplication(Application application) {
		_applications.add(application);

		_rewire();
	}

	private void _addBus(Bus bus) {
		_buses.add(bus);

		for (Application application : _applications) {
			_registerApplication(bus, application);
		}
	}

	private void _addProvider(Object provider) {
		_providers.add(provider);

		_rewire();
	}

	private void _addService(Object service) {
		_services.add(service);

		_rewire();
	}

	private void _registerApplication(Bus bus, Application application) {
		RuntimeDelegate runtimeDelegate = RuntimeDelegate.getInstance();

		JAXRSServerFactoryBean jaxRSServerFactoryBean =
			runtimeDelegate.createEndpoint(
				application, JAXRSServerFactoryBean.class);

		jaxRSServerFactoryBean.setBus(bus);
		jaxRSServerFactoryBean.setProperties(_properties);

		JSONProvider<Object> jsonProvider = new JSONProvider<>();

		jsonProvider.setDropCollectionWrapperElement(true);
		jsonProvider.setDropRootElement(true);
		jsonProvider.setSerializeAsArray(true);
		jsonProvider.setSupportUnwrapped(true);

		jaxRSServerFactoryBean.setProvider(jsonProvider);

		for (Object provider : _providers) {
			jaxRSServerFactoryBean.setProvider(provider);
		}

		for (Object service : _services) {
			jaxRSServerFactoryBean.setServiceBean(service);
		}

		Server server = jaxRSServerFactoryBean.create();

		server.start();

		_store(bus, application, server);
	}

	private void _registerApplications() {
		for (Bus bus : _buses) {
			for (Application application : _applications) {
				_registerApplication(bus, application);
			}
		}
	}

	private void _remove(Object application) {
		for (Map<Object, Server> servers : _busServers.values()) {
			Server server = servers.remove(application);

			if (server != null) {
				server.destroy();
			}
		}
	}

	private void _removeApplication(Application application) {
		_applications.remove(application);

		_remove(application);
	}

	private void _removeBus(Bus bus) {
		_buses.remove(bus);

		Map<Object, Server> servers = _busServers.remove(bus);

		if (servers == null) {
			return;
		}

		for (Server server : servers.values()) {
			server.destroy();
		}
	}

	private void _removeProvider(Object provider) {
		_providers.remove(provider);

		_rewire();
	}

	private void _removeService(Object service) {
		_services.remove(service);

		_rewire();
	}

	private void _rewire() {
		for (Application application : _applications) {
			_remove(application);
		}

		_registerApplications();
	}

	private void _store(Bus bus, Object object, Server server) {
		Map<Object, Server> servers = _busServers.get(bus);

		if (servers == null) {
			servers = new HashMap<>();

			_busServers.put(bus, servers);
		}

		servers.put(object, server);
	}

	private <T> void _swapClassLoader(T t, Consumer<T> consumer) {
		Class<?> clazz = t.getClass();

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				clazz.getClassLoader())) {

			consumer.accept(t);
		}
	}

	private final Collection<Application> _applications = new ArrayList<>();
	private final Collection<Bus> _buses = new ArrayList<>();
	private final Map<Bus, Map<Object, Server>> _busServers =
		new IdentityHashMap<>();
	private final Map<String, Object> _properties;
	private final Collection<Object> _providers = new ArrayList<>();
	private final Collection<Object> _services = new ArrayList<>();

}