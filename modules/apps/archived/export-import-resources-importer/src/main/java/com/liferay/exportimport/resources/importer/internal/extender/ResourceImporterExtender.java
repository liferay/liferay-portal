/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.resources.importer.internal.extender;

import com.liferay.exportimport.resources.importer.internal.messaging.DestinationNames;
import com.liferay.exportimport.resources.importer.provider.ResourceImporterBundleProvider;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.cluster.ClusterMasterExecutor;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.plugin.PluginPackageUtil;

import jakarta.servlet.ServletContext;

import java.io.IOException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 */
@Component(service = {})
public class ResourceImporterExtender {

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, ResourceImporterBundleProvider.class,
			new ResourceImporterBundleProviderServiceTrackerCustomizer());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourceImporterExtender.class);

	private BundleContext _bundleContext;

	@Reference
	private ClusterMasterExecutor _clusterMasterExecutor;

	@Reference
	private MessageBus _messageBus;

	private ServiceTracker
		<ResourceImporterBundleProvider, ServiceRegistration<ServletContext>>
			_serviceTracker;

	private class ResourceImporterBundleProviderServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<ResourceImporterBundleProvider,
			 ServiceRegistration<ServletContext>> {

		@Override
		public ServiceRegistration<ServletContext> addingService(
			ServiceReference<ResourceImporterBundleProvider> serviceReference) {

			if (!_clusterMasterExecutor.isMaster()) {
				return null;
			}

			Bundle bundle = serviceReference.getBundle();

			String bundleSymbolicName = bundle.getSymbolicName();

			try {
				PluginPackageUtil.registerInstalledPluginPackage(
					getPluginPackage(bundle));

				ServletContext servletContext = new BundleServletContextAdapter(
					bundle);

				ServiceRegistration<ServletContext> serviceRegistration =
					_bundleContext.registerService(
						ServletContext.class, servletContext,
						new HashMapDictionary<String, Object>());

				Message message = new Message();

				message.put("command", "deploy");
				message.put("servletContextName", bundleSymbolicName);

				_messageBus.sendMessage(DestinationNames.HOT_DEPLOY, message);

				return serviceRegistration;
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to initialize bundle: " + bundleSymbolicName,
						exception);
				}
			}

			return null;
		}

		@Override
		public void modifiedService(
			ServiceReference<ResourceImporterBundleProvider> serviceReference,
			ServiceRegistration<ServletContext> serviceRegistration) {
		}

		@Override
		public void removedService(
			ServiceReference<ResourceImporterBundleProvider> serviceReference,
			ServiceRegistration<ServletContext> serviceRegistration) {

			serviceRegistration.unregister();

			Bundle bundle = serviceReference.getBundle();

			String bundleSymbolicName = bundle.getSymbolicName();

			try {
				PluginPackageUtil.unregisterInstalledPluginPackage(
					getPluginPackage(bundle));
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to unregister bundle: " + bundleSymbolicName,
						exception);
				}
			}
		}

		protected PluginPackage getPluginPackage(Bundle bundle)
			throws IOException {

			PluginPackage pluginPackage =
				PluginPackageUtil.readPluginPackageProperties(
					bundle.getSymbolicName() + "-web",
					PropertiesUtil.load(
						bundle.getResource(
							"/WEB-INF/liferay-plugin-package.properties")));

			pluginPackage.setContext(bundle.getSymbolicName());

			return pluginPackage;
		}

	}

}