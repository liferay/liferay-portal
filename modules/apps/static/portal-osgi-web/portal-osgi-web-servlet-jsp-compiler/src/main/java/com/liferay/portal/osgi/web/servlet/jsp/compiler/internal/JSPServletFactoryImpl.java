/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.osgi.web.servlet.JSPServletFactory;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.Servlet;

import java.io.File;

import java.lang.reflect.Method;

import java.net.URL;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.apache.jasper.compiler.Compiler;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.VersionRange;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Preston Crary
 */
@Component(service = JSPServletFactory.class)
public class JSPServletFactoryImpl implements JSPServletFactory {

	@Override
	public Servlet createJSPServlet() {
		return new JspServlet(_fragmentCounts.keySet());
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.RESOLVED,
			new JspFragmentBundleTrackerCustomizer(bundleContext));

		_bundleTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();
	}

	private static final String _DIR_NAME_RESOURCES = "/META-INF/resources";

	private static final String _WORK_DIR = StringBundler.concat(
		PropsValues.LIFERAY_HOME, File.separator, "work", File.separator);

	private static final Log _log = LogFactoryUtil.getLog(
		JSPServletFactoryImpl.class);

	static {
		try {
			ClassLoader classLoader = PortalClassLoaderUtil.getClassLoader();

			Class<?> clazz = classLoader.loadClass(
				"com.liferay.portal.jsp.engine.internal.compiler." +
					"BridgeCompiler");

			Method initMethod = clazz.getMethod("init", Supplier.class);

			initMethod.invoke(
				null, (Supplier<Compiler>)() -> new CompilerWrapper());
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new ExceptionInInitializerError(reflectiveOperationException);
		}
	}

	private BundleTracker<Tracked> _bundleTracker;
	private final Map<String, AtomicInteger> _fragmentCounts = new HashMap<>();

	private static class Tracked {

		public boolean match(Bundle bundle) {
			if (_symbolicName.equals(bundle.getSymbolicName()) &&
				((_versionRange == null) ||
				 _versionRange.includes(bundle.getVersion()))) {

				return true;
			}

			return false;
		}

		private Tracked(String symbolicName, VersionRange versionRange) {
			_symbolicName = symbolicName;
			_versionRange = versionRange;
		}

		private final String _symbolicName;
		private final VersionRange _versionRange;

	}

	private class JspFragmentBundleTrackerCustomizer
		implements BundleTrackerCustomizer<Tracked> {

		@Override
		public Tracked addingBundle(Bundle bundle, BundleEvent event) {
			Dictionary<String, String> headers = bundle.getHeaders(
				StringPool.BLANK);

			String fragmentHost = headers.get(Constants.FRAGMENT_HOST);

			if (fragmentHost == null) {
				return null;
			}

			Enumeration<URL> enumeration = bundle.findEntries(
				_DIR_NAME_RESOURCES, "*.jsp*", true);

			if (enumeration == null) {
				return null;
			}

			String[] fragmentHostParts = StringUtil.split(
				fragmentHost, CharPool.SEMICOLON);

			String symbolicName = StringUtil.trim(fragmentHostParts[0]);

			VersionRange versionRange = null;

			if (fragmentHostParts.length > 1) {
				String[] versionParts = StringUtil.split(
					fragmentHostParts[1], CharPool.EQUAL);

				versionRange = new VersionRange(
					StringUtil.unquote(StringUtil.trim(versionParts[1])));
			}

			Tracked tracked = new Tracked(symbolicName, versionRange);

			_deleteJSPServletClasses(tracked, true);

			return tracked;
		}

		@Override
		public void modifiedBundle(
			Bundle bundle, BundleEvent event, Tracked tracked) {
		}

		@Override
		public void removedBundle(
			Bundle bundle, BundleEvent event, Tracked tracked) {

			_deleteJSPServletClasses(tracked, false);
		}

		private JspFragmentBundleTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		private void _deleteJSPServletClasses(Tracked tracked, boolean add) {
			for (Bundle bundle : _bundleContext.getBundles()) {
				if (!tracked.match(bundle)) {
					continue;
				}

				_fragmentCounts.compute(
					tracked._symbolicName,
					(symbolicName, count) -> {
						if (add) {
							if (count == null) {
								return new AtomicInteger(1);
							}

							count.incrementAndGet();

							return count;
						}

						if (count.decrementAndGet() == 0) {
							return null;
						}

						return count;
					});

				String scratchDir = StringBundler.concat(
					_WORK_DIR, bundle.getSymbolicName(), StringPool.DASH,
					bundle.getVersion());

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Deleting JSP class files from ".concat(scratchDir));
				}

				FileUtil.deltree(new File(scratchDir));
			}
		}

		private final BundleContext _bundleContext;

	}

}