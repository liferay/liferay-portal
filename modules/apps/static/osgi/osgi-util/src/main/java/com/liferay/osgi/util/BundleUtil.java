/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osgi.util;

import com.liferay.osgi.util.bundle.BundleStartLevelUtil;
import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.lpkg.deployer.LPKGDeployer;
import com.liferay.portal.osgi.web.wab.generator.WabGenerator;

import java.io.File;

import java.net.URI;
import java.net.URL;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.FrameworkWiring;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Carlos Sierra Andrés
 * @author Matthew Tambara
 */
public class BundleUtil {

	public static URL getResourceInBundleOrFragments(
		Bundle bundle, String name) {

		String dirName = "/";
		String fileName = name;

		int index = name.lastIndexOf('/');

		if (index > 0) {
			dirName = name.substring(0, index);
			fileName = name.substring(index + 1);
		}
		else if (index == 0) {
			fileName = name.substring(1);
		}

		if (fileName.length() == 0) {
			if (!dirName.equals("/")) {
				dirName = dirName + "/";
			}

			return bundle.getEntry(dirName);
		}

		Enumeration<URL> enumeration = bundle.findEntries(
			dirName, fileName, false);

		if ((enumeration == null) || !enumeration.hasMoreElements()) {
			return null;
		}

		List<URL> list = Collections.list(enumeration);

		return list.get(list.size() - 1);
	}

	public static void installBundle(
			BundleContext bundleContext, LPKGDeployer lpkgDeployer,
			String location, int startLevel)
		throws Exception {

		Bundle bundle = null;

		Map<String, String[]> parameters = _getParameterMap(location);

		String[] lpkgPaths = parameters.get("lpkgPath");
		String[] protocols = parameters.get("protocol");
		String[] webContextPaths = parameters.get("Web-ContextPath");

		if (parameters.isEmpty() && location.endsWith(".lpkg")) {
			URI uri = new URI(location);

			uri = uri.normalize();

			bundle = bundleContext.installBundle(
				location, lpkgDeployer.toBundle(new File(uri.getPath())));
		}
		else if (ArrayUtil.isNotEmpty(lpkgPaths)) {
			bundle = bundleContext.getBundle(lpkgPaths[0]);

			refreshBundles(
				bundleContext, Collections.<Bundle>singletonList(bundle));

			return;
		}
		else if (ArrayUtil.isNotEmpty(protocols) &&
				 protocols[0].equals("lpkg") &&
				 ArrayUtil.isNotEmpty(webContextPaths)) {

			String contextName = webContextPaths[0].substring(1);

			for (Bundle installedBundle : bundleContext.getBundles()) {
				Dictionary<String, String> headers = installedBundle.getHeaders(
					StringPool.BLANK);

				if (!contextName.equals(
						headers.get("Liferay-WAB-Context-Name"))) {

					continue;
				}

				refreshBundles(
					bundleContext,
					Collections.<Bundle>singletonList(installedBundle));
			}

			return;
		}
		else if (location.startsWith("webbundle:")) {
			WebBundleInstaller webBundleInstaller = new WebBundleInstaller(
				bundleContext, location, startLevel);

			webBundleInstaller.open();

			return;
		}
		else {
			bundle = bundleContext.installBundle(location);
		}

		BundleStartLevelUtil.setStartLevelAndStart(
			bundle, startLevel, bundleContext);
	}

	public static void refreshBundles(
		BundleContext bundleContext, List<Bundle> refreshBundles) {

		Bundle systemBundle = bundleContext.getBundle(0);

		FrameworkWiring frameworkWiring = systemBundle.adapt(
			FrameworkWiring.class);

		final DefaultNoticeableFuture<FrameworkEvent> defaultNoticeableFuture =
			new DefaultNoticeableFuture<>();

		frameworkWiring.refreshBundles(
			refreshBundles,
			new FrameworkListener() {

				@Override
				public void frameworkEvent(FrameworkEvent frameworkEvent) {
					defaultNoticeableFuture.set(frameworkEvent);
				}

			});

		try {
			FrameworkEvent frameworkEvent = defaultNoticeableFuture.get();

			if (frameworkEvent.getType() != FrameworkEvent.PACKAGES_REFRESHED) {
				throw frameworkEvent.getThrowable();
			}
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}
	}

	private static Map<String, String[]> _getParameterMap(String location) {
		int index = location.indexOf(CharPool.QUESTION);

		if (index == -1) {
			return Collections.emptyMap();
		}

		String queryString = location.substring(index + 1);

		if (Validator.isNull(queryString)) {
			return Collections.emptyMap();
		}

		String[] parameters = StringUtil.split(queryString, CharPool.AMPERSAND);

		Map<String, String[]> parameterMap = new HashMap<>();

		for (String parameter : parameters) {
			if (parameter.length() <= 0) {
				continue;
			}

			String[] kvp = StringUtil.split(parameter, CharPool.EQUAL);

			if (kvp.length == 0) {
				continue;
			}

			String key = kvp[0];

			String value = StringPool.BLANK;

			if (kvp.length > 1) {
				value = kvp[1];
			}

			String[] values = parameterMap.get(key);

			if (values == null) {
				parameterMap.put(key, new String[] {value});
			}
			else {
				parameterMap.put(key, ArrayUtil.append(values, value));
			}
		}

		return parameterMap;
	}

	private static class WebBundleInstaller
		extends ServiceTracker<WabGenerator, Void> {

		public WebBundleInstaller(
				BundleContext bundleContext, String location, int startLevel)
			throws InvalidSyntaxException {

			super(bundleContext, WabGenerator.class, null);

			_location = location;
			_startLevel = startLevel;
		}

		@Override
		public Void addingService(
			ServiceReference<WabGenerator> serviceReference) {

			// Service must be explicitly gotten from the bundle context to
			// ensure DS component's lazy activation is completed

			WabGenerator wabGenerator = context.getService(serviceReference);

			if (wabGenerator == null) {
				throw new IllegalStateException("Missing WAB generator");
			}

			try {
				Bundle bundle = context.installBundle(_location);

				BundleStartLevelUtil.setStartLevelAndStart(
					bundle, _startLevel, context);
			}
			catch (Exception exception) {
				ReflectionUtil.throwException(exception);
			}
			finally {
				context.ungetService(serviceReference);
			}

			close();

			return null;
		}

		private final String _location;
		private final int _startLevel;

	}

}