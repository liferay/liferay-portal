/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.internal.servlet;

import com.liferay.portal.kernel.module.service.Snapshot;

import jakarta.servlet.ServletContext;

/**
 * @author Roberto Díaz
 */
public class ServletContextUtil {

	public static String getContextPath() {
		ServletContext servletContext = getServletContext();

		return servletContext.getContextPath();
	}

	public static ServletContext getServletContext() {
		return _servletContextSnapshot.get();
	}

	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			ServletContextUtil.class, ServletContext.class,
			"(osgi.web.symbolicname=com.liferay.item.selector.taglib)");

}