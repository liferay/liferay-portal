/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.deploy.hot;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.plugin.PluginPackage;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.ServletContext;

import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Ivica Cardic
 * @author Brian Wing Shun Chan
 * @author Raymond Augé
 * @author Miguel Pastor
 */
public class HotDeployEvent {

	public HotDeployEvent(ServletContext servletContext) {
		this(servletContext, servletContext.getClassLoader());
	}

	public HotDeployEvent(
		ServletContext servletContext, ClassLoader contextClassLoader) {

		_servletContext = servletContext;
		_contextClassLoader = contextClassLoader;

		try {
			initDependentServletContextNames();
		}
		catch (IOException ioException) {
			_log.error(ioException);
		}
	}

	public ClassLoader getContextClassLoader() {
		return _contextClassLoader;
	}

	public Set<String> getDependentServletContextNames() {
		return _dependentServletContextNames;
	}

	public PluginPackage getPluginPackage() {
		return _pluginPackage;
	}

	public ServletContext getServletContext() {
		return _servletContext;
	}

	public String getServletContextName() {
		return _servletContext.getServletContextName();
	}

	public void setPluginPackage(PluginPackage pluginPackage) {
		_pluginPackage = pluginPackage;
	}

	protected void initDependentServletContextNames() throws IOException {
		if (!DependencyManagementThreadLocal.isEnabled() || isWAB()) {
			return;
		}

		InputStream inputStream = _servletContext.getResourceAsStream(
			"/WEB-INF/liferay-plugin-package.properties");

		if (inputStream != null) {
			String propertiesString = StringUtil.read(inputStream);

			Properties properties = PropertiesUtil.load(propertiesString);

			String[] pluginPackgeRequiredDeploymentContexts = StringUtil.split(
				properties.getProperty("required-deployment-contexts"));

			for (String pluginPackageRequiredDeploymentContext :
					pluginPackgeRequiredDeploymentContexts) {

				_dependentServletContextNames.add(
					pluginPackageRequiredDeploymentContext.trim());
			}
		}

		if (!_dependentServletContextNames.isEmpty() && _log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Plugin ", _servletContext.getServletContextName(),
					" requires ",
					StringUtil.merge(_dependentServletContextNames, ", ")));
		}
	}

	protected boolean isWAB() {

		// Never enable plugin dependency management when the servlet context is
		// from a Liferay WAB since dependency is handled by the OSGi runtime

		Object osgiBundleContext = _servletContext.getAttribute(
			"osgi-bundlecontext");
		Object osgiRuntimeVendor = _servletContext.getAttribute(
			"osgi-runtime-vendor");

		if ((osgiBundleContext != null) && (osgiRuntimeVendor != null) &&
			osgiRuntimeVendor.equals(ReleaseInfo.getVendor())) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(HotDeployEvent.class);

	private final ClassLoader _contextClassLoader;
	private final Set<String> _dependentServletContextNames = new TreeSet<>();
	private PluginPackage _pluginPackage;
	private final ServletContext _servletContext;

}