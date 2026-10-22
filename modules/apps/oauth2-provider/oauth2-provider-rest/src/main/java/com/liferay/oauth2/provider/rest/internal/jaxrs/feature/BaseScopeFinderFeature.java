/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.jaxrs.feature;

import com.liferay.oauth2.provider.scope.spi.scope.finder.ScopeFinder;

import jakarta.ws.rs.core.Application;

import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.endpoint.ServerLifeCycleListener;
import org.apache.cxf.endpoint.ServerLifeCycleManager;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.jaxrs.JAXRSServiceImpl;
import org.apache.cxf.jaxrs.model.ApplicationInfo;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.model.MethodDispatcher;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Shuyang Zhou
 */
public abstract class BaseScopeFinderFeature
	implements Feature, ServerLifeCycleListener {

	public BaseScopeFinderFeature(BundleContext bundleContext) {
		_bundleContext = bundleContext;
	}

	@Override
	public void initialize(Bus bus) {
	}

	@Override
	public void initialize(Client client, Bus bus) {
	}

	@Override
	public void initialize(InterceptorProvider interceptorProvider, Bus bus) {
	}

	@Override
	public void initialize(Server server, Bus bus) {
		ServerLifeCycleManager serverLifeCycleManager = bus.getExtension(
			ServerLifeCycleManager.class);

		_serverLifeCycleManagers.put(server, serverLifeCycleManager);

		serverLifeCycleManager.registerListener(this);
	}

	@Override
	public void startServer(Server server) {
		if (!_serverLifeCycleManagers.containsKey(server)) {
			return;
		}

		Endpoint endpoint = server.getEndpoint();

		JAXRSServiceImpl jaxrsServiceImpl =
			(JAXRSServiceImpl)endpoint.getService();

		Set<String> scopes = new HashSet<>();

		for (ClassResourceInfo classResourceInfo :
				jaxrsServiceImpl.getClassResourceInfos()) {

			_collectScopes(classResourceInfo, scopes);
		}

		ApplicationInfo applicationInfo = (ApplicationInfo)endpoint.get(
			Application.class.getName());

		Map<String, Object> properties = applicationInfo.getProperties();

		_serviceRegistrations.put(
			server,
			_bundleContext.registerService(
				ScopeFinder.class,
				new CollectionScopeFinder(Collections.unmodifiableSet(scopes)),
				new Hashtable<>(
					(Map<String, Object>)properties.get(
						"osgi.jaxrs.application.serviceProperties"))));
	}

	@Override
	public void stopServer(Server server) {
		ServerLifeCycleManager serverLifeCycleManager =
			_serverLifeCycleManagers.remove(server);

		if (serverLifeCycleManager == null) {
			return;
		}

		serverLifeCycleManager.unRegisterListener(this);

		ServiceRegistration<ScopeFinder> serviceRegistration =
			_serviceRegistrations.remove(server);

		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	protected abstract void collectScopes(
		OperationResourceInfo operationResourceInfo, Set<String> scopes);

	private void _collectScopes(
		ClassResourceInfo classResourceInfo, Set<String> scopes) {

		MethodDispatcher methodDispatcher =
			classResourceInfo.getMethodDispatcher();

		for (OperationResourceInfo operationResourceInfo :
				methodDispatcher.getOperationResourceInfos()) {

			collectScopes(operationResourceInfo, scopes);
		}

		for (ClassResourceInfo subresourceClassResourceInfo :
				classResourceInfo.getSubResources()) {

			if (subresourceClassResourceInfo == classResourceInfo) {
				continue;
			}

			_collectScopes(subresourceClassResourceInfo, scopes);
		}
	}

	private final BundleContext _bundleContext;
	private final Map<Server, ServerLifeCycleManager> _serverLifeCycleManagers =
		new ConcurrentHashMap<>();
	private final Map<Server, ServiceRegistration<ScopeFinder>>
		_serviceRegistrations = new ConcurrentHashMap<>();

}