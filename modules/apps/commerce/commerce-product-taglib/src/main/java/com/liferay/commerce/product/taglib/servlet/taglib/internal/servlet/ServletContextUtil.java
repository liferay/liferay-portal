/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.taglib.servlet.taglib.internal.servlet;

import com.liferay.commerce.product.content.helper.CPContentHelper;
import com.liferay.commerce.product.content.render.list.CPContentListRendererRegistry;
import com.liferay.commerce.product.content.render.list.entry.CPContentListEntryRendererRegistry;
import com.liferay.portal.kernel.module.service.Snapshot;

import jakarta.servlet.ServletContext;

/**
 * @author Alessio Antonio Rendina
 */
public class ServletContextUtil {

	public static CPContentHelper getCPContentHelper() {
		return _cpContentHelperSnapshot.get();
	}

	public static CPContentListEntryRendererRegistry
		getCPContentListEntryRendererRegistry() {

		return _cpContentListEntryRendererRegistrySnapshot.get();
	}

	public static CPContentListRendererRegistry
		getCPContentListRendererRegistry() {

		return _cpContentListRendererRegistrySnapshot.get();
	}

	public static ServletContext getServletContext() {
		return _servletContextSnapshot.get();
	}

	private static final Snapshot<CPContentHelper> _cpContentHelperSnapshot =
		new Snapshot<>(ServletContextUtil.class, CPContentHelper.class);
	private static final Snapshot<CPContentListEntryRendererRegistry>
		_cpContentListEntryRendererRegistrySnapshot = new Snapshot<>(
			ServletContextUtil.class, CPContentListEntryRendererRegistry.class);
	private static final Snapshot<CPContentListRendererRegistry>
		_cpContentListRendererRegistrySnapshot = new Snapshot<>(
			ServletContextUtil.class, CPContentListRendererRegistry.class);
	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			ServletContextUtil.class, ServletContext.class,
			"(osgi.web.symbolicname=com.liferay.commerce.product.taglib)");

}