/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osgi.log.service.extender.internal.activator;

import com.liferay.osgi.log.service.extender.internal.osgi.commands.LoggingOSGiCommands;
import com.liferay.osgi.util.osgi.commands.OSGiCommands;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log4j.Log4JUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PropertiesUtil;

import java.io.IOException;

import java.net.URL;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogLevel;
import org.osgi.service.log.admin.LoggerAdmin;
import org.osgi.service.log.admin.LoggerContext;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Shuyang Zhou
 */
public class OSGiLogServiceExtenderBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) {
		_serviceTracker = new ServiceTracker<>(
			bundleContext, LoggerAdmin.class,
			new LoggerAdminServiceTrackerCustomizer(bundleContext));

		_serviceTracker.open();
	}

	@Override
	public void stop(BundleContext bundleContext) {
		_serviceTracker.close();
	}

	public static class Tracked
		extends AbstractMap.SimpleEntry
			<BundleTracker<LoggerContext>, ServiceRegistration<OSGiCommands>> {

		public Tracked(
			BundleTracker<LoggerContext> bundleTracker,
			ServiceRegistration<OSGiCommands> serviceRegistration) {

			super(bundleTracker, serviceRegistration);
		}

	}

	private static Map<String, LogLevel> _loadLogConfigurations(Bundle bundle) {
		Map<String, LogLevel> logLevels = new HashMap<>();

		try {
			_loadLogConfigurations(
				bundle, "osgi-logging.properties", logLevels);
			_loadLogConfigurations(
				bundle, "osgi-logging-ext.properties", logLevels);
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to load OSGi logging configurations for " + bundle,
				ioException);

			return Collections.emptyMap();
		}

		return logLevels;
	}

	private static Map<String, LogLevel> _loadLogConfigurations(
			Bundle bundle, String resourcePath, Map<String, LogLevel> logLevels)
		throws IOException {

		Enumeration<URL> enumeration = bundle.findEntries(
			"META-INF", resourcePath, false);

		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				Properties properties = PropertiesUtil.load(
					enumeration.nextElement());

				for (String name : properties.stringPropertyNames()) {
					String value = properties.getProperty(name);

					try {
						logLevels.put(name, LogLevel.valueOf(value));
					}
					catch (IllegalArgumentException illegalArgumentException) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								StringBundler.concat(
									"Bundle ", bundle, ", resource ",
									resourcePath, ", and logger ", name,
									" contains an invalid log level \"", value,
									"\""),
								illegalArgumentException);
						}
					}
				}
			}
		}

		return logLevels;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OSGiLogServiceExtenderBundleActivator.class);

	private volatile ServiceTracker<LoggerAdmin, Tracked> _serviceTracker;

	private static class LoggerAdminServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<LoggerAdmin, Tracked> {

		@Override
		public Tracked addingService(
			ServiceReference<LoggerAdmin> serviceReference) {

			LoggerAdmin loggerAdmin = _bundleContext.getService(
				serviceReference);

			BundleTracker<LoggerContext> bundleTracker = new BundleTracker<>(
				_bundleContext, Bundle.ACTIVE,
				new LoggerContextBundleTrackerCustomizer(loggerAdmin));

			bundleTracker.open();

			LoggingOSGiCommands loggingOSGiCommands = new LoggingOSGiCommands(
				loggerAdmin);

			ServiceRegistration<OSGiCommands> serviceRegistration =
				_bundleContext.registerService(
					OSGiCommands.class, loggingOSGiCommands,
					HashMapDictionaryBuilder.<String, Object>put(
						"osgi.command.function",
						new String[] {"levels", "level"}
					).put(
						"osgi.command.scope", "logging"
					).build());

			return new Tracked(bundleTracker, serviceRegistration);
		}

		@Override
		public void modifiedService(
			ServiceReference<LoggerAdmin> serviceReference, Tracked tracked) {
		}

		@Override
		public void removedService(
			ServiceReference<LoggerAdmin> serviceReference, Tracked tracked) {

			ServiceRegistration<OSGiCommands> serviceRegistration =
				tracked.getValue();

			serviceRegistration.unregister();

			BundleTracker<LoggerContext> bundleTracker = tracked.getKey();

			bundleTracker.close();

			_bundleContext.ungetService(serviceReference);
		}

		private LoggerAdminServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		private final BundleContext _bundleContext;

	}

	private static class LoggerContextBundleTrackerCustomizer
		implements BundleTrackerCustomizer<LoggerContext> {

		@Override
		public LoggerContext addingBundle(
			Bundle bundle, BundleEvent bundleEvent) {

			Map<String, LogLevel> logLevels = _loadLogConfigurations(bundle);

			if (logLevels.isEmpty()) {
				return null;
			}

			for (Map.Entry<String, LogLevel> entry : logLevels.entrySet()) {
				String name = "osgi.logging.".concat(entry.getKey());

				LogLevel logLevel = entry.getValue();

				Log4JUtil.setLevel(name, logLevel.toString(), false);
			}

			LoggerContext loggerContext = _loggerAdmin.getLoggerContext(
				StringBundler.concat(
					bundle.getSymbolicName(), "|", bundle.getVersion(), "|",
					bundle.getLocation()));

			loggerContext.setLogLevels(logLevels);

			return loggerContext;
		}

		@Override
		public void modifiedBundle(
			Bundle bundle, BundleEvent bundleEvent,
			LoggerContext loggerContext) {
		}

		@Override
		public void removedBundle(
			Bundle bundle, BundleEvent bundleEvent,
			LoggerContext loggerContext) {

			loggerContext.clear();
		}

		private LoggerContextBundleTrackerCustomizer(LoggerAdmin loggerAdmin) {
			_loggerAdmin = loggerAdmin;
		}

		private final LoggerAdmin _loggerAdmin;

	}

}