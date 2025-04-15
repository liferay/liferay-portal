/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.model;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletBag;
import com.liferay.portal.kernel.portlet.PortletBagPool;
import com.liferay.portal.kernel.resource.bundle.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ClassResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Julio Camarero
 */
public abstract class BaseJSPAssetRenderer<T>
	extends BaseAssetRenderer<T> implements AssetRenderer<T> {

	public abstract String getJspPath(
		HttpServletRequest httpServletRequest, String template);

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		String jspPath = getJspPath(httpServletRequest, template);

		if (Validator.isNull(jspPath)) {
			return false;
		}

		ResourceBundleLoader originalResourceBundleLoader =
			(ResourceBundleLoader)httpServletRequest.getAttribute(
				WebKeys.RESOURCE_BUNDLE_LOADER);

		ServletContext servletContext = getServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(jspPath);

		try {
			httpServletRequest.setAttribute(
				WebKeys.RESOURCE_BUNDLE_LOADER, acquireResourceBundleLoader());

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
				WebKeys.RESOURCE_BUNDLE_LOADER, originalResourceBundleLoader);
		}
	}

	public void setServletContext(ServletContext servletContext) {
		_servletContext = servletContext;
	}

	protected ResourceBundleLoader acquireResourceBundleLoader() {
		if (_servletContext != null) {
			ResourceBundleLoader resourceBundleLoader =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByServletContextName(
						_servletContext.getServletContextName());

			if (resourceBundleLoader == null) {
				return ResourceBundleLoaderUtil.getPortalResourceBundleLoader();
			}

			return resourceBundleLoader;
		}

		return new AggregateResourceBundleLoader(
			new ClassResourceBundleLoader("content.Language", getClass()),
			ResourceBundleLoaderUtil.getPortalResourceBundleLoader());
	}

	protected ServletContext getServletContext() {
		if (_servletContext != null) {
			return _servletContext;
		}

		PortletBag portletBag = PortletBagPool.get(
			getAssetRendererFactory().getPortletId());

		return portletBag.getServletContext();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseJSPAssetRenderer.class);

	private ServletContext _servletContext;

}