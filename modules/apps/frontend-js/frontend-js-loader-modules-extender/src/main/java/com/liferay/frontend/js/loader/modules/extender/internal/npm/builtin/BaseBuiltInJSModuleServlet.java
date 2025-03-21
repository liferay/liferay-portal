/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.internal.npm.builtin;

import com.liferay.frontend.js.loader.modules.extender.npm.JSBundle;
import com.liferay.frontend.js.loader.modules.extender.npm.JSModule;
import com.liferay.frontend.js.loader.modules.extender.npm.JSPackage;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MimeTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.util.Locale;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Provides a base abstract class to implement servlets that return JavaScript
 * modules tracked by the {@link
 * com.liferay.frontend.js.loader.modules.extender.npm.NPMRegistry}.
 *
 * @author Adolfo Pérez
 */
public abstract class BaseBuiltInJSModuleServlet extends HttpServlet {

	public BaseBuiltInJSModuleServlet() {
		_workDirName = StringBundler.concat(
			PropsValues.LIFERAY_HOME, File.separator, "work");
	}

	@Override
	public void destroy() {
		_serviceTrackerMap.close();
	}

	@Override
	public void init() {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundle.getBundleContext(), ResourceBundleLoader.class,
			"bundle.symbolic.name");
	}

	protected abstract MimeTypes getMimeTypes();

	/**
	 * Returns the requested resource descriptor. This is a template method that
	 * must be implemented by subclasses to lookup the requested resource.
	 *
	 * @param  pathInfo the request's pathInfo
	 * @return the {@link String} content of the resource or <code>null</code>
	 */
	protected abstract ResourceDescriptor getResourceDescriptor(
		String pathInfo);

	@Override
	protected void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String pathInfo = httpServletRequest.getPathInfo();

		ResourceDescriptor resourceDescriptor = getResourceDescriptor(pathInfo);

		if (resourceDescriptor == null) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		_setContentType(httpServletResponse, pathInfo);

		String languageId = httpServletRequest.getParameter("languageId");

		Locale locale = LocaleUtil.fromLanguageId(languageId);

		_sendResource(
			httpServletResponse, resourceDescriptor, locale, pathInfo);
	}

	private void _sendResource(
			HttpServletResponse httpServletResponse,
			ResourceDescriptor resourceDescriptor, Locale locale,
			String pathInfo)
		throws IOException {

		InputStream inputStream = null;

		String extension = FileUtil.getExtension(pathInfo);
		JSPackage jsPackage = resourceDescriptor.getJsPackage();

		String moduleName = resourceDescriptor.getPackagePath();

		if (moduleName != null) {
			if (extension.equals("map")) {
				JSModule jsModule = jsPackage.getJSModule(
					moduleName.substring(0, moduleName.length() - 7));

				if (jsModule != null) {
					inputStream = jsModule.getSourceMapInputStream();
				}
			}
			else {
				if (extension.equals("js")) {
					moduleName = moduleName.substring(
						0, moduleName.length() - 3);
				}

				JSModule jsModule = jsPackage.getJSModule(moduleName);

				if (jsModule != null) {
					inputStream = jsModule.getInputStream();
				}
			}
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug("Module name is null");
			}
		}

		if (inputStream == null) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		try {
			String content = StringUtil.read(inputStream);

			httpServletResponse.setCharacterEncoding(StringPool.UTF8);

			PrintWriter printWriter = httpServletResponse.getWriter();

			if (extension.equals("js")) {
				JSBundle jsBundle = jsPackage.getJSBundle();

				ResourceBundleLoader resourceBundleLoader =
					_serviceTrackerMap.getService(jsBundle.getName());

				if (resourceBundleLoader != null) {
					content = LanguageUtil.process(
						() -> resourceBundleLoader.loadResourceBundle(locale),
						locale, content);
				}
			}

			printWriter.print(content);
		}
		catch (IOException ioException) {
			_log.error(
				"Unable to read " + resourceDescriptor.toString(), ioException);

			httpServletResponse.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				"Unable to read file");
		}
		finally {
			inputStream.close();
		}
	}

	private void _setContentType(
		HttpServletResponse httpServletResponse, String pathInfo) {

		String extension = FileUtil.getExtension(pathInfo);

		if (extension.equals("js")) {
			httpServletResponse.setContentType(
				ContentTypes.TEXT_JAVASCRIPT_UTF8);
		}
		else if (extension.equals("map")) {
			httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);
		}
		else {
			MimeTypes mimeTypes = getMimeTypes();

			httpServletResponse.setContentType(
				mimeTypes.getContentType(pathInfo));
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseBuiltInJSModuleServlet.class);

	private ServiceTrackerMap<String, ResourceBundleLoader> _serviceTrackerMap;
	private final String _workDirName;

}