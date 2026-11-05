/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.taglib.internal.servlet.taglib;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.product.navigation.control.menu.manager.ProductNavigationControlMenuManager;
import com.liferay.product.navigation.product.menu.helper.ProductNavigationProductMenuHelper;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mario Leandro
 */
@Component(
	property = "dynamic.include.key=/html/common/themes/top_head.jsp#post",
	service = DynamicInclude.class
)
public class SideNavigationTopHeadJSPDynamicInclude
	extends BaseJSPDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		if (!_productNavigationControlMenuManager.isShowControlMenu(
				httpServletRequest) ||
			!_productNavigationProductMenuHelper.isShowProductMenu(
				httpServletRequest)) {

			return;
		}

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/top_head.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SideNavigationTopHeadJSPDynamicInclude.class);

	@Reference
	private ProductNavigationControlMenuManager
		_productNavigationControlMenuManager;

	@Reference
	private ProductNavigationProductMenuHelper
		_productNavigationProductMenuHelper;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.application.list.taglib)"
	)
	private ServletContext _servletContext;

}