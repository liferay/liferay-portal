/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.log4j.extender.internal.activator;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log4j.Log4JUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;

/**
 * @author Shuyang Zhou
 */
public class Log4jExtenderBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) {
		_bundleTracker = new BundleTracker<Bundle>(
			bundleContext, Bundle.ACTIVE | Bundle.STARTING, null) {

			@Override
			public Bundle addingBundle(Bundle bundle, BundleEvent bundleEvent) {
				_configureLog4j(bundle, "module-log4j.xml");
				_configureLog4j(bundle, "module-log4j-ext.xml");
				_configureLog4j(bundle.getSymbolicName());

				return bundle;
			}

		};

		_bundleTracker.open();
	}

	@Override
	public void stop(BundleContext bundleContext) {
		_bundleTracker.close();
	}

	private void _configureLog4j(Bundle bundle, String resourcePath) {
		Enumeration<URL> enumeration = bundle.findEntries(
			"META-INF", resourcePath, false);

		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				Log4JUtil.configureLog4J(enumeration.nextElement());
			}
		}
	}

	private void _configureLog4j(String symbolicName) {
		File configFile = new File(
			StringBundler.concat(
				PropsValues.MODULE_FRAMEWORK_BASE_DIR, "/log4j/", symbolicName,
				"-log4j-ext.xml"));

		if (!configFile.exists()) {
			return;
		}

		URI uri = configFile.toURI();

		try {
			Log4JUtil.configureLog4J(uri.toURL());
		}
		catch (MalformedURLException malformedURLException) {
			_log.error(
				"Unable to configure Log4j for bundle " + symbolicName,
				malformedURLException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		Log4jExtenderBundleActivator.class);

	private volatile BundleTracker<Bundle> _bundleTracker;

}