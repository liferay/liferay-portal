/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.portlet.action;

import com.liferay.application.list.GroupProvider;
import com.liferay.application.list.constants.ApplicationListWebKeys;
import com.liferay.layout.util.LayoutsTree;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.product.navigation.product.menu.constants.ProductNavigationProductMenuPortletKeys;
import com.liferay.product.navigation.product.menu.helper.ProductNavigationProductMenuHelper;
import com.liferay.product.navigation.product.menu.web.internal.display.context.LayoutsTreeDisplayContext;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mario Leandro
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ProductNavigationProductMenuPortletKeys.PRODUCT_NAVIGATION_PRODUCT_MENU,
		"mvc.command.name=/product_navigation_product_menu/get_page_tree"
	},
	service = MVCResourceCommand.class
)
public class GetPageTreeMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		if (!_productNavigationProductMenuHelper.isShowProductMenu(
				httpServletRequest)) {

			return;
		}

		resourceRequest.setAttribute(
			ApplicationListWebKeys.GROUP_PROVIDER, _groupProvider);

		JSONSerializer jsonSerializer = _jsonFactory.createJSONSerializer();

		LayoutsTreeDisplayContext layoutsTreeDisplayContext =
			new LayoutsTreeDisplayContext(
				httpServletRequest, _language, _layoutLocalService,
				_layoutService, _layoutsTree, resourceResponse, resourceRequest,
				_siteNavigationMenuItemLocalService,
				_siteNavigationMenuItemTypeRegistry,
				_siteNavigationMenuLocalService);

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			jsonSerializer.serializeDeep(
				HashMapBuilder.<String, Object>putAll(
					layoutsTreeDisplayContext.getData()
				).put(
					"portletNamespace",
					_portal.getPortletNamespace(
						ProductNavigationProductMenuPortletKeys.
							PRODUCT_NAVIGATION_PRODUCT_MENU)
				).build()));
	}

	@Reference
	private GroupProvider _groupProvider;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private LayoutsTree _layoutsTree;

	@Reference
	private Portal _portal;

	@Reference
	private ProductNavigationProductMenuHelper
		_productNavigationProductMenuHelper;

	@Reference
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Reference
	private SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;

	@Reference
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

}