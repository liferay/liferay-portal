/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.bundle.config.extender.internal;

import com.liferay.frontend.js.loader.modules.extender.npm.ModuleNameUtil;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletContext;

import java.net.URL;

import java.util.Dictionary;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Carlos Sierra Andrés
 */
public class JSBundleConfigRegistryUtil {

	public static String getJavaScript() {
		return _javaScriptDCLSingleton.getSingleton(
			JSBundleConfigRegistryUtil::_createJavaScript);
	}

	public static long getLastModified() {
		return _lastModified;
	}

	private static String _createJavaScript() {
		List<String> javaScripts = _serviceTrackerList.toList();

		StringBundler sb = new StringBundler(javaScripts.size());

		for (String javaScript : javaScripts) {
			sb.append(javaScript);
		}

		return sb.toString();
	}

	private static String _getModuleMainJavaScript(
		ServletContext servletContext) {

		try {
			URL url = servletContext.getResource(
				"META-INF/resources/package.json");

			if (url == null) {
				url = servletContext.getResource("package.json");
			}

			if (url == null) {
				return StringPool.BLANK;
			}

			JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
				URLUtil.toString(url));

			String moduleName = jsonObject.getString("name");
			String moduleVersion = jsonObject.getString("version");

			String moduleMain = jsonObject.getString("main");

			if (Validator.isNull(moduleMain)) {
				moduleMain = "index.js";
			}

			return StringBundler.concat(
				"var MODULE_MAIN='", moduleName, "@", moduleVersion, "/",
				ModuleNameUtil.toModuleName(moduleMain), "';");
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSBundleConfigRegistryUtil.class);

	private static final DCLSingleton<String> _javaScriptDCLSingleton =
		new DCLSingleton<>();
	private static volatile long _lastModified = System.currentTimeMillis();
	private static final ServiceTrackerList<String> _serviceTrackerList;

	private static class JSBundleConfigServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<ServletContext, String> {

		@Override
		public String addingService(
			ServiceReference<ServletContext> serviceReference) {

			Bundle bundle = serviceReference.getBundle();

			Dictionary<String, String> headers = bundle.getHeaders(
				StringPool.BLANK);

			String jsConfig = headers.get("Liferay-JS-Config");

			if (jsConfig == null) {
				return null;
			}

			URL url = bundle.getEntry(jsConfig);

			if (url == null) {
				return null;
			}

			ServletContext servletContext = _bundleContext.getService(
				serviceReference);

			try {
				String javaScript = StringBundler.concat(
					"try {", _getModuleMainJavaScript(servletContext),
					"var MODULE_PATH='", PortalUtil.getPathProxy(),
					servletContext.getContextPath(), "';",
					StringUtil.removeSubstring(
						URLUtil.toString(url),
						"//# sourceMappingURL=config.js.map"),
					"} catch(error) {console.error(error);}");

				_javaScriptDCLSingleton.destroy(null);

				_lastModified = System.currentTimeMillis();

				return javaScript;
			}
			catch (Exception exception) {
				_log.error("Unable to open resource", exception);
			}

			_bundleContext.ungetService(serviceReference);

			return null;
		}

		@Override
		public void modifiedService(
			ServiceReference<ServletContext> serviceReference,
			String javaScript) {
		}

		@Override
		public void removedService(
			ServiceReference<ServletContext> serviceReference,
			String javaScript) {

			_bundleContext.ungetService(serviceReference);

			_javaScriptDCLSingleton.destroy(null);

			_lastModified = System.currentTimeMillis();
		}

		private JSBundleConfigServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		private final BundleContext _bundleContext;

	}

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			JSBundleConfigRegistryUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, ServletContext.class, "(osgi.web.contextpath=*)",
			new JSBundleConfigServiceTrackerCustomizer(bundleContext));
	}

}