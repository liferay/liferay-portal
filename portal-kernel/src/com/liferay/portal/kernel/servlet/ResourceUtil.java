/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.portal.kernel.util.ObjectValuePair;

import jakarta.servlet.ServletContext;

import java.io.IOException;

import java.net.URL;

/**
 * @author Minhchau Dang
 */
public class ResourceUtil {

	public static ObjectValuePair<ServletContext, URL> getObjectValuePair(
			String requestPath, String requestURI,
			ServletContext defaultServletContext)
		throws IOException {

		ServletContext servletContext = defaultServletContext;

		URL resourceURL = servletContext.getResource(requestURI);

		if (resourceURL != null) {
			return new ObjectValuePair<>(servletContext, resourceURL);
		}

		servletContext = PortalWebResourcesUtil.getPathServletContext(
			requestPath);

		resourceURL = PortalWebResourcesUtil.getResource(
			servletContext, requestPath);

		if (resourceURL != null) {
			return new ObjectValuePair<>(servletContext, resourceURL);
		}

		servletContext = PortletResourcesUtil.getPathServletContext(
			requestPath);

		resourceURL = PortletResourcesUtil.getResource(
			servletContext, requestPath);

		if (resourceURL != null) {
			return new ObjectValuePair<>(servletContext, resourceURL);
		}

		servletContext = DynamicResourceIncludeUtil.getPathServletContext(
			requestPath);

		resourceURL = DynamicResourceIncludeUtil.getResource(
			servletContext, requestPath);

		if (resourceURL != null) {
			return new ObjectValuePair<>(servletContext, resourceURL);
		}

		return null;
	}

	public static ServletContext getPathServletContext(
			String requestPath, String requestURI,
			ServletContext defaultServletContext)
		throws IOException {

		ObjectValuePair<ServletContext, URL> objectValuePair =
			getObjectValuePair(requestPath, requestURI, defaultServletContext);

		if (objectValuePair == null) {
			return null;
		}

		return objectValuePair.getKey();
	}

	public static URL getResourceURL(
			String requestPath, String requestURI,
			ServletContext defaultServletContext)
		throws IOException {

		ObjectValuePair<ServletContext, URL> objectValuePair =
			getObjectValuePair(requestPath, requestURI, defaultServletContext);

		if (objectValuePair == null) {
			return null;
		}

		return objectValuePair.getValue();
	}

}