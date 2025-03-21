/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.staging.taglib.internal.servlet;

import com.liferay.layout.util.LayoutsTree;
import com.liferay.portal.kernel.module.service.Snapshot;

import jakarta.servlet.ServletContext;

/**
 * @author Daniel Kocsis
 */
public class ServletContextUtil {

	public static LayoutsTree getLayoutsTree() {
		return _layoutsTreeSnapshot.get();
	}

	public static ServletContext getServletContext() {
		return _servletContextSnapshot.get();
	}

	private static final Snapshot<LayoutsTree> _layoutsTreeSnapshot =
		new Snapshot<>(ServletContextUtil.class, LayoutsTree.class);
	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			ServletContextUtil.class, ServletContext.class,
			"(osgi.web.symbolicname=com.liferay.staging.taglib)");

}