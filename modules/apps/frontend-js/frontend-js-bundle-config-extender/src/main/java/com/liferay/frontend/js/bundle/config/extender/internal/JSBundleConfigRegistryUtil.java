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

	public static class JSConfig {

		public ServletContext getServletContext() {
			return _servletContext;
		}

		public URL getURL() {
			return _url;
		}

		private JSConfig(ServletContext servletContext, URL url) {
			_servletContext = servletContext;
			_url = url;
		}

		private final ServletContext _servletContext;
		private final URL _url;

	}

	private static String _createJavaScript() {
		List<JSConfig> jsConfigs = _serviceTrackerList.toList();

		StringBundler sb = new StringBundler(jsConfigs.size() * 8);

		for (JSConfig jsConfig : jsConfigs) {
			try {
				sb.append("try {var MODULE_MAIN='");

				ServletContext servletContext = jsConfig.getServletContext();

				sb.append(_getModuleMain(servletContext));

				sb.append("';var MODULE_PATH='");
				sb.append(PortalUtil.getPathProxy());
				sb.append(servletContext.getContextPath());
				sb.append("';");
				sb.append(
					StringUtil.removeSubstring(
						URLUtil.toString(jsConfig.getURL()),
						"//# sourceMappingURL=config.js.map"));
				sb.append("} catch(error) {console.error(error);}");
			}
			catch (Exception exception) {
				_log.error("Unable to open resource", exception);
			}
		}

		return sb.toString();
	}

	private static String _getModuleMain(ServletContext servletContext) {
		try {
			URL url = servletContext.getResource(
				"META-INF/resources/package.json");

			if (url == null) {
				url = servletContext.getResource("package.json");
			}

			if (url == null) {
				return null;
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
				moduleName, "@", moduleVersion, "/",
				ModuleNameUtil.toModuleName(moduleMain));
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
	private static final ServiceTrackerList<JSConfig> _serviceTrackerList;

	private static class JSBundleConfigServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<ServletContext, JSConfig> {

		public JSBundleConfigServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public JSConfig addingService(
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

			_javaScriptDCLSingleton.destroy(null);

			_lastModified = System.currentTimeMillis();

			return new JSConfig(servletContext, url);
		}

		@Override
		public void modifiedService(
			ServiceReference<ServletContext> serviceReference,
			JSConfig jsConfig) {
		}

		@Override
		public void removedService(
			ServiceReference<ServletContext> serviceReference,
			JSConfig jsConfig) {

			if (jsConfig != null) {
				_bundleContext.ungetService(serviceReference);

				_javaScriptDCLSingleton.destroy(null);

				_lastModified = System.currentTimeMillis();
			}
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