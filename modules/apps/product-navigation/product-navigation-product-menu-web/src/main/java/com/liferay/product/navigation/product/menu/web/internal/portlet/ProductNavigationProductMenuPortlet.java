/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.portlet;

import com.liferay.application.list.GroupProvider;
import com.liferay.application.list.PanelAppRegistry;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.application.list.display.context.logic.PanelCategoryHelper;
import com.liferay.layout.util.LayoutsTree;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.product.navigation.control.menu.manager.ProductNavigationControlMenuManager;
import com.liferay.product.navigation.product.menu.constants.ProductNavigationProductMenuPortletKeys;
import com.liferay.product.navigation.product.menu.web.internal.display.context.LayoutsTreeDisplayContext;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.instanceable=false",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.system=true",
		"com.liferay.portlet.use-default-template=false",
		"jakarta.portlet.display-name=Product Menu",
		"jakarta.portlet.init-param.view-template=/portlet/view.jsp",
		"jakarta.portlet.name=" + ProductNavigationProductMenuPortletKeys.PRODUCT_NAVIGATION_PRODUCT_MENU,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.supported-public-render-parameter=layoutSetBranchId",
		"jakarta.portlet.supported-public-render-parameter=privateLayout",
		"jakarta.portlet.supported-public-render-parameter=selPlid",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class ProductNavigationProductMenuPortlet extends MVCPortlet {

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		if (!_productNavigationControlMenuManager.isShowControlMenu(
				httpServletRequest)) {

			return;
		}

		renderRequest.setAttribute(
			ApplicationListWebKeys.GROUP_PROVIDER, _groupProvider);
		renderRequest.setAttribute(
			ApplicationListWebKeys.PANEL_APP_REGISTRY, _panelAppRegistry);
		renderRequest.setAttribute(
			ApplicationListWebKeys.PANEL_CATEGORY_HELPER,
			new PanelCategoryHelper(_panelAppRegistry));
		renderRequest.setAttribute(
			LayoutsTreeDisplayContext.class.getName(),
			new LayoutsTreeDisplayContext(
				httpServletRequest, _language, _layoutLocalService,
				_layoutService, _layoutsTree, renderRequest, renderResponse,
				_siteNavigationMenuItemLocalService,
				_siteNavigationMenuItemTypeRegistry,
				_siteNavigationMenuLocalService));

		super.doDispatch(renderRequest, renderResponse);
	}

	@Reference
	private GroupProvider _groupProvider;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private LayoutsTree _layoutsTree;

	@Reference
	private PanelAppRegistry _panelAppRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private ProductNavigationControlMenuManager
		_productNavigationControlMenuManager;

	@Reference
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Reference
	private SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;

	@Reference
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

}