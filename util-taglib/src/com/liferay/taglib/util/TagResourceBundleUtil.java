/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.util;

import com.liferay.portal.kernel.portlet.LiferayPortletContext;
import com.liferay.portal.kernel.resource.bundle.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletConfig;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Adolfo Pérez
 */
public class TagResourceBundleUtil {

	public static ResourceBundle getResourceBundle(
		HttpServletRequest httpServletRequest, Locale locale) {

		ResourceBundleLoader resourceBundleLoader = acquireResourceBundleLoader(
			httpServletRequest);

		if (resourceBundleLoader != null) {
			return resourceBundleLoader.loadResourceBundle(locale);
		}

		ResourceBundle portletResourceBundle = getPortletResourceBundle(
			httpServletRequest, locale);

		ResourceBundle portalResourceBundle = PortalUtil.getResourceBundle(
			locale);

		return new AggregateResourceBundle(
			portletResourceBundle, portalResourceBundle);
	}

	public static ResourceBundle getResourceBundle(PageContext pageContext) {
		ResourceBundle resourceBundle =
			(ResourceBundle)pageContext.getAttribute("resourceBundle");

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		Locale locale = PortalUtil.getLocale(httpServletRequest);

		if (resourceBundle != null) {
			return new AggregateResourceBundle(
				resourceBundle, PortalUtil.getResourceBundle(locale));
		}

		return getResourceBundle(httpServletRequest, locale);
	}

	protected static ResourceBundleLoader acquireResourceBundleLoader(
		HttpServletRequest httpServletRequest) {

		ResourceBundleLoader resourceBundleLoader =
			(ResourceBundleLoader)httpServletRequest.getAttribute(
				WebKeys.RESOURCE_BUNDLE_LOADER);

		if (resourceBundleLoader == null) {
			ServletContext servletContext =
				httpServletRequest.getServletContext();

			String servletContextName = servletContext.getServletContextName();

			if (Validator.isNull(servletContextName)) {
				return null;
			}

			resourceBundleLoader =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByServletContextName(
						servletContextName);

			PortletConfig portletConfig =
				(PortletConfig)httpServletRequest.getAttribute(
					JavaConstants.JAVAX_PORTLET_CONFIG);

			if (portletConfig != null) {
				LiferayPortletContext liferayPortletContext =
					(LiferayPortletContext)portletConfig.getPortletContext();

				ServletContext portletServletContext =
					liferayPortletContext.getServletContext();

				String portletServletContextName =
					portletServletContext.getServletContextName();

				if (servletContextName.equals(portletServletContextName)) {
					resourceBundleLoader =
						locale -> portletConfig.getResourceBundle(locale);
				}
			}
		}

		if (resourceBundleLoader == null) {
			return null;
		}

		return new AggregateResourceBundleLoader(
			resourceBundleLoader,
			ResourceBundleLoaderUtil.getPortalResourceBundleLoader());
	}

	protected static ResourceBundle getPortletResourceBundle(
		HttpServletRequest httpServletRequest, Locale locale) {

		PortletConfig portletConfig =
			(PortletConfig)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_CONFIG);

		if (portletConfig != null) {
			return portletConfig.getResourceBundle(locale);
		}

		return _emptyResourceBundle;
	}

	private static final ResourceBundle _emptyResourceBundle =
		new EmptyResourceBundle();

	private static class EmptyResourceBundle extends ResourceBundle {

		@Override
		public boolean containsKey(String key) {
			return false;
		}

		@Override
		public Enumeration<String> getKeys() {
			return Collections.emptyEnumeration();
		}

		@Override
		protected Object handleGetObject(String key) {
			return null;
		}

	}

}