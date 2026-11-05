/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.application.list.taglib.internal.servlet.taglib;

import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.application.list.taglib.internal.display.context.SideNavigationDisplayContext;
import com.liferay.application.list.util.PanelCategoryRegistryUtil;
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
	property = "dynamic.include.key=/html/common/themes/body_top.jsp#post",
	service = DynamicInclude.class
)
public class SideNavigationBodyTopJSPDynamicInclude
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

		httpServletRequest.setAttribute(
			SideNavigationDisplayContext.class.getName(),
			new SideNavigationDisplayContext(
				httpServletRequest, _panelAppRegistry,
				PanelCategoryRegistryUtil.getPanelCategory(
					PanelCategoryKeys.SITE_ADMINISTRATION)));

		super.include(httpServletRequest, httpServletResponse, key);
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/body_top.jsp";
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
		SideNavigationBodyTopJSPDynamicInclude.class);

	@Reference
	private PanelAppRegistry _panelAppRegistry;

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