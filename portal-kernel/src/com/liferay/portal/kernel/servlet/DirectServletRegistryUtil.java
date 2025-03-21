/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;

import java.io.File;

import java.lang.reflect.Method;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Shuyang Zhou
 */
public class DirectServletRegistryUtil {

	public static void clearServlets() {
		_servletInfos.clear();
	}

	public static Servlet getServlet(String path) {
		ServletInfo servletInfo = _servletInfos.get(path);

		if (servletInfo == null) {
			return null;
		}

		Servlet servlet = servletInfo.getServlet();

		if (_DIRECT_SERVLET_CONTEXT_RELOAD) {
			long lastModified = _getFileLastModified(path, servlet);

			if ((lastModified == 0) ||
				(lastModified != servletInfo.getLastModified())) {

				_servletInfos.remove(path);

				servlet = null;

				if (_log.isDebugEnabled()) {
					_log.debug("Reload " + path);
				}
			}
			else {
				servlet = _reloadDependants(path, servlet);
			}
		}

		return servlet;
	}

	public static void putServlet(String path, Servlet servlet) {
		if (path.startsWith(PathModulePrefixHolder._PATH_MODULE_PREFIX) ||
			_servletInfos.containsKey(path)) {

			return;
		}

		long lastModified = 1;

		if (_DIRECT_SERVLET_CONTEXT_RELOAD) {
			lastModified = _getFileLastModified(path, servlet);
		}

		if (lastModified > 0) {
			ServletInfo servletInfo = new ServletInfo();

			servletInfo.setLastModified(lastModified);
			servletInfo.setServlet(servlet);

			_servletInfos.put(path, servletInfo);
		}
	}

	private static File _getFile(String path, Servlet servlet) {
		ServletConfig servletConfig = servlet.getServletConfig();

		ServletContext servletContext = servletConfig.getServletContext();

		String contextPath = servletContext.getContextPath();

		if (!Validator.isBlank(contextPath) && path.startsWith(contextPath)) {
			path = path.substring(contextPath.length());
		}

		return new File(servletContext.getRealPath(StringPool.BLANK), path);
	}

	private static long _getFileLastModified(String path, Servlet servlet) {
		File file = _getFile(path, servlet);

		if (file.exists()) {
			return file.lastModified();
		}

		return -1;
	}

	private static Servlet _reloadDependants(String path, Servlet servlet) {
		if (!_reloadDependants) {
			return servlet;
		}

		try {
			Method method = ReflectionUtil.getDeclaredMethod(
				servlet.getClass(), "getDependants");

			Collection<String> dependants = null;

			if (JasperVersionDetector.hasJspServletDependantsMap()) {
				Map<String, ?> dependantsMap = (Map<String, ?>)method.invoke(
					servlet);

				if (dependantsMap != null) {
					dependants = dependantsMap.keySet();
				}
			}
			else {
				dependants = (List<String>)method.invoke(servlet);
			}

			if (dependants == null) {
				return servlet;
			}

			boolean reloadServlet = false;

			for (String dependant : dependants) {
				long lastModified = _getFileLastModified(dependant, servlet);

				Long previousLastModified = _dependantTimestamps.get(dependant);

				if (previousLastModified == null) {
					_dependantTimestamps.put(dependant, lastModified);

					previousLastModified = lastModified;
				}

				if ((lastModified == 0) ||
					(lastModified != previousLastModified.longValue())) {

					reloadServlet = true;

					_dependantTimestamps.put(dependant, lastModified);

					if (_log.isDebugEnabled()) {
						_log.debug("Reload dependant " + dependant);
					}
				}
			}

			if (reloadServlet) {
				_servletInfos.remove(path);

				File file = _getFile(path, servlet);

				file.setLastModified(System.currentTimeMillis());

				servlet = null;
			}
		}
		catch (NoSuchMethodException noSuchMethodException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Reloading of dependant JSP is disabled because your " +
						"servlet container is not a variant of Jasper",
					noSuchMethodException);
			}

			_reloadDependants = false;
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return servlet;
	}

	private static final boolean _DIRECT_SERVLET_CONTEXT_RELOAD =
		GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.DIRECT_SERVLET_CONTEXT_RELOAD));

	private static final Log _log = LogFactoryUtil.getLog(
		DirectServletRegistryUtil.class);

	private static final Map<String, Long> _dependantTimestamps =
		new ConcurrentHashMap<>();
	private static boolean _reloadDependants = true;
	private static final Map<String, ServletInfo> _servletInfos =
		new ConcurrentHashMap<>();

	private static class PathModulePrefixHolder {

		private static final String _PATH_MODULE_PREFIX = StringBundler.concat(
			PortalUtil.getPathContext(), Portal.PATH_MODULE, StringPool.SLASH);

	}

	private static class ServletInfo {

		public long getLastModified() {
			return _lastModified;
		}

		public Servlet getServlet() {
			return _servlet;
		}

		public void setLastModified(long lastModified) {
			_lastModified = lastModified;
		}

		public void setServlet(Servlet servlet) {
			_servlet = servlet;
		}

		private long _lastModified;
		private Servlet _servlet;

	}

}