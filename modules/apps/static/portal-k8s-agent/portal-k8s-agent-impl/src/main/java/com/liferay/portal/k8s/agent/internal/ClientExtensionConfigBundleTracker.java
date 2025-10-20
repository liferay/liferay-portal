/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.k8s.agent.internal;

import com.liferay.osgi.util.configuration.ConfigurationFactoryUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.persistence.InMemoryOnlyConfigurationThreadLocal;
import com.liferay.portal.k8s.agent.internal.util.ConfigurationUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.tools.DBUpgrader;

import java.net.URL;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.felix.configurator.impl.json.JSONUtil;
import org.apache.felix.configurator.impl.model.Config;
import org.apache.felix.configurator.impl.model.ConfigurationFile;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Gregory Amerson
 */
@Component(service = {})
public class ClientExtensionConfigBundleTracker {

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		if (DBUpgrader.isUpgradeClient()) {
			return;
		}

		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE,
			new ClientExtensionConfigBundleTrackerCustomizer());

		_bundleTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		if (_bundleTracker != null) {
			_bundleTracker.close();
		}
	}

	private String _getVirtualInstancePid(
		Config config, String virtualInstanceId) {

		String pid = config.getPid();

		String factoryPid = ConfigurationFactoryUtil.getFactoryPidFromPid(pid);

		if (factoryPid == null) {
			return pid;
		}

		int index = pid.indexOf('/');

		if (index != -1) {
			String scopedVirtualInstanceId = pid.substring(index);

			if (!Objects.equals(
					scopedVirtualInstanceId, "/" + virtualInstanceId)) {

				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Overriding virtual instance ID from ",
							scopedVirtualInstanceId, " to ", virtualInstanceId,
							" for factory configuration with PID ", pid));
				}
			}

			return StringBundler.concat(
				pid.substring(0, index), "/", virtualInstanceId);
		}

		return StringBundler.concat(pid, "/", virtualInstanceId);
	}

	private Configuration _processConfiguration(Bundle bundle, Config config)
		throws Exception {

		Dictionary<String, Object> properties = config.getProperties();

		String virtualInstanceId = (String)properties.get(
			"dxp.lxc.liferay.com.virtualInstanceId");

		if (Objects.equals(virtualInstanceId, "default")) {
			virtualInstanceId = PropsValues.COMPANY_DEFAULT_WEB_ID;
		}

		String virtualInstancePid = _getVirtualInstancePid(
			config, virtualInstanceId);

		try (SafeCloseable safeCloseable =
				InMemoryOnlyConfigurationThreadLocal.
					setInMemoryOnlyWithSafeCloseable(true)) {

			Configuration configuration = null;

			Configuration[] configurations =
				_configurationAdmin.listConfigurations(
					StringBundler.concat(
						"(.cx.config.key=", virtualInstancePid, ")"));

			if (ArrayUtil.isNotEmpty(configurations)) {
				configuration = configurations[0];

				Dictionary<String, Object> configurationProperties =
					configuration.getProperties();

				if (Objects.equals(
						configurationProperties.get(
							".cx.config.bundle.last.modified"),
						bundle.getLastModified())) {

					if (_log.isInfoEnabled()) {
						_log.info(
							"Configuration and bundle resource versions are " +
								"identical");
					}

					return configuration;
				}
			}
			else {
				configuration = ConfigurationUtil.getConfiguration(
					_configurationAdmin, virtualInstancePid);
			}

			Set<Configuration.ConfigurationAttribute> configurationAttributes =
				configuration.getAttributes();

			if (configurationAttributes.contains(
					Configuration.ConfigurationAttribute.READ_ONLY)) {

				configuration.removeAttributes(
					Configuration.ConfigurationAttribute.READ_ONLY);
			}

			properties.put(".cx.config.bundle.id", bundle.getBundleId());
			properties.put(
				".cx.config.bundle.last.modified", bundle.getLastModified());
			properties.put(".cx.config.key", virtualInstancePid);

			if (_log.isInfoEnabled()) {
				_log.info("Processed configuration " + properties);
			}

			configuration.updateIfDifferent(properties);

			configuration.addAttributes(
				Configuration.ConfigurationAttribute.READ_ONLY);

			return configuration;
		}
	}

	private void _processConfigurations(
			Bundle bundle, List<Configuration> configurations, String fileName,
			String json)
		throws Exception {

		JSONUtil.Report report = new JSONUtil.Report();

		ConfigurationFile configurationFile =
			ConfigurationUtil.getConfigurationFile(
				bundle, fileName, json, report);

		for (String error : report.errors) {
			_log.error(error);
		}

		for (String warning : report.warnings) {
			if (_log.isWarnEnabled()) {
				_log.warn(warning);
			}
		}

		if (configurationFile == null) {
			return;
		}

		for (Config config : configurationFile.getConfigurations()) {
			try {
				configurations.add(_processConfiguration(bundle, config));
			}
			catch (Exception exception) {
				_log.error(
					"Unable to process configuration " + config, exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClientExtensionConfigBundleTracker.class);

	private BundleTracker<List<Configuration>> _bundleTracker;

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	private class ClientExtensionConfigBundleTrackerCustomizer
		implements BundleTrackerCustomizer<List<Configuration>> {

		@Override
		public List<Configuration> addingBundle(
			Bundle bundle, BundleEvent bundleEvent) {

			List<Configuration> configurations = new ArrayList<>();

			Enumeration<URL> enumeration = bundle.findEntries(
				"/META-INF/client-extension-config", "*.json", false);

			if (enumeration != null) {
				while (enumeration.hasMoreElements()) {
					URL url = enumeration.nextElement();

					try {
						_processConfigurations(
							bundle, configurations, url.getPath(),
							URLUtil.toString(url));
					}
					catch (Exception exception) {
						_log.error(
							"Unable to process client extension config " + url,
							exception);
					}
				}
			}

			if (configurations.isEmpty()) {
				return null;
			}

			return configurations;
		}

		@Override
		public void modifiedBundle(
			Bundle bundle, BundleEvent bundleEvent,
			List<Configuration> unusedConfigurations) {
		}

		@Override
		public void removedBundle(
			Bundle bundle, BundleEvent event,
			List<Configuration> configurations) {

			for (Configuration configuration : configurations) {
				try {
					if (_log.isInfoEnabled()) {
						_log.info(
							"Deleting configuration " +
								configuration.getProperties());
					}

					configuration.delete();
				}
				catch (Exception exception) {
					_log.error(
						"Unable to delete configuration " +
							configuration.getPid(),
						exception);
				}
			}
		}

	}

}