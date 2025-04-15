/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.portlet.action;

import com.liferay.layout.util.LayoutsTree;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.product.navigation.product.menu.constants.ProductNavigationProductMenuPortletKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ProductNavigationProductMenuPortletKeys.PRODUCT_NAVIGATION_PRODUCT_MENU,
		"mvc.command.name=/product_navigation_product_menu/get_layouts"
	},
	service = MVCResourceCommand.class
)
public class GetLayoutsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		boolean incomplete = ParamUtil.getBoolean(
			httpServletRequest, "incomplete", true);
		long parentLayoutId = ParamUtil.getLong(
			httpServletRequest, "parentLayoutId");
		boolean privateLayout = ParamUtil.getBoolean(
			httpServletRequest, "privateLayout");

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"hasMoreElements",
				() -> {
					int pageSize = GetterUtil.getInteger(
						PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN);

					if (pageSize <= 0) {
						return false;
					}

					int childLayoutsCount = _layoutService.getLayoutsCount(
						themeDisplay.getScopeGroupId(), privateLayout,
						parentLayoutId);

					int start = ParamUtil.getInteger(
						httpServletRequest, "start");

					start = Math.max(0, start);

					int end = ParamUtil.getInteger(
						httpServletRequest, "end", start + pageSize);

					String key = StringBundler.concat(
						"productMenuPagesTree:", themeDisplay.getScopeGroupId(),
						StringPool.COLON, privateLayout, ":Pagination");

					String paginationJSON = SessionClicks.get(
						httpServletRequest.getSession(), key,
						_jsonFactory.getNullJSON());

					JSONObject paginationJSONObject =
						_jsonFactory.createJSONObject(paginationJSON);

					int loadedLayoutsCount = paginationJSONObject.getInt(
						String.valueOf(parentLayoutId), 0);

					if (loadedLayoutsCount > end) {
						end = loadedLayoutsCount;
					}

					end = Math.max(start, end);

					if (childLayoutsCount > end) {
						return true;
					}

					return false;
				}
			).put(
				"items",
				_layoutsTree.getLayoutsJSONArray(
					null, themeDisplay.getScopeGroupId(), httpServletRequest,
					true, incomplete, true, parentLayoutId, privateLayout,
					"productMenuPagesTree")
			));
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private LayoutsTree _layoutsTree;

	@Reference
	private Portal _portal;

}