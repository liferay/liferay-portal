/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.convert;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.MaintenanceUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.commons.lang.time.StopWatch;

/**
 * @author Alexander Chow
 */
public abstract class BaseConvertProcess implements ConvertProcess {

	@Override
	public void convert() throws ConvertException {
		try {
			if (getPath() != null) {
				return;
			}

			StopWatch stopWatch = new StopWatch();

			stopWatch.start();

			if (_log.isInfoEnabled()) {
				Class<?> clazz = getClass();

				_log.info("Starting conversion for " + clazz.getName());
			}

			doConvert();

			if (_log.isInfoEnabled()) {
				Class<?> clazz = getClass();

				_log.info(
					StringBundler.concat(
						"Finished conversion for ", clazz.getName(), " in ",
						stopWatch.getTime(), " ms"));
			}
		}
		catch (Exception exception) {
			throw new ConvertException(exception);
		}
		finally {
			setParameterValues(null);

			if (MaintenanceUtil.isMaintaining()) {
				MaintenanceUtil.cancel();
			}
		}
	}

	@Override
	public String getConfigurationErrorMessage() {
		return null;
	}

	@Override
	public abstract String getDescription();

	@Override
	public String getParameterDescription() {
		return null;
	}

	@Override
	public String[] getParameterNames() {
		return null;
	}

	public String[] getParameterValues() {
		return _paramValues;
	}

	@Override
	public boolean includeCustomView(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		String jspPath = getJspPath();

		if (Validator.isNull(jspPath)) {
			return false;
		}

		ResourceBundleLoader resourceBundleLoader =
			(ResourceBundleLoader)httpServletRequest.getAttribute(
				WebKeys.RESOURCE_BUNDLE_LOADER);

		ServletContext servletContext = getServletContext(httpServletRequest);

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(jspPath);

		try {
			httpServletRequest.setAttribute(
				WebKeys.RESOURCE_BUNDLE_LOADER,
				getResourceBundleLoader(httpServletRequest));

			requestDispatcher.include(httpServletRequest, httpServletResponse);

			return true;
		}
		catch (ServletException servletException) {
			_log.error("Unable to include JSP " + jspPath, servletException);

			throw new IOException(
				"Unable to include " + jspPath, servletException);
		}
		finally {
			httpServletRequest.setAttribute(
				WebKeys.RESOURCE_BUNDLE_LOADER, resourceBundleLoader);
		}
	}

	@Override
	public abstract boolean isEnabled();

	@Override
	public void setParameterValues(String[] values) {
		_paramValues = values;
	}

	/**
	 * @throws ConvertException
	 */
	@Override
	public void validate() throws ConvertException {
	}

	protected abstract void doConvert() throws Exception;

	protected String getJspPath() {
		return null;
	}

	protected ResourceBundleLoader getResourceBundleLoader(
		HttpServletRequest httpServletRequest) {

		ServletContext servletContext = getServletContext(httpServletRequest);

		ResourceBundleLoader resourceBundleLoader =
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByServletContextName(
					servletContext.getServletContextName());

		if (resourceBundleLoader == null) {
			resourceBundleLoader =
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader();
		}

		return resourceBundleLoader;
	}

	protected ServletContext getServletContext(
		HttpServletRequest httpServletRequest) {

		return httpServletRequest.getServletContext();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseConvertProcess.class);

	private String[] _paramValues;

}