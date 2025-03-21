/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.product.menu.web.internal.portlet.action;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.product.navigation.product.menu.constants.ProductNavigationProductMenuPortletKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ProductNavigationProductMenuPortletKeys.PRODUCT_NAVIGATION_PRODUCT_MENU,
		"mvc.command.name=/product_navigation_product_menu/find_layouts"
	},
	service = MVCResourceCommand.class
)
public class FindLayoutsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		String keywords = ParamUtil.getString(resourceRequest, "keywords");

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(resourceResponse);

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

		if (Validator.isNull(keywords)) {
			jsonObject.put(
				"layouts", _jsonFactory.createJSONArray()
			).put(
				"totalCount", 0
			);

			ServletResponseUtil.write(
				httpServletResponse, jsonObject.toString());

			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		List<Layout> layouts = _layoutLocalService.getLayouts(
			themeDisplay.getSiteGroupId(), keywords,
			new String[] {
				LayoutConstants.TYPE_CONTENT, LayoutConstants.TYPE_EMBEDDED,
				LayoutConstants.TYPE_LINK_TO_LAYOUT,
				LayoutConstants.TYPE_FULL_PAGE_APPLICATION,
				LayoutConstants.TYPE_PANEL, LayoutConstants.TYPE_PORTLET,
				LayoutConstants.TYPE_URL
			},
			new int[] {WorkflowConstants.STATUS_ANY}, 0, 10, null);

		for (Layout layout : layouts) {
			JSONArray layoutPathJSONArray = _getLayoutPathJSONArray(
				layout, themeDisplay.getLocale());

			jsonArray.put(
				JSONUtil.put(
					"name", layout.getName(themeDisplay.getLocale())
				).put(
					"path", layoutPathJSONArray
				).put(
					"target",
					HtmlUtil.escape(layout.getTypeSettingsProperty("target"))
				).put(
					"url", _portal.getLayoutFullURL(layout, themeDisplay)
				));
		}

		jsonObject.put("layouts", jsonArray);

		int totalCount = _layoutLocalService.getLayoutsCount(
			themeDisplay.getSiteGroupId(), keywords,
			new String[] {
				LayoutConstants.TYPE_CONTENT, LayoutConstants.TYPE_EMBEDDED,
				LayoutConstants.TYPE_LINK_TO_LAYOUT,
				LayoutConstants.TYPE_FULL_PAGE_APPLICATION,
				LayoutConstants.TYPE_PANEL, LayoutConstants.TYPE_PORTLET,
				LayoutConstants.TYPE_URL
			},
			new int[] {WorkflowConstants.STATUS_ANY});

		jsonObject.put("totalCount", totalCount);

		ServletResponseUtil.write(httpServletResponse, jsonObject.toString());
	}

	private JSONArray _getLayoutPathJSONArray(Layout layout, Locale locale)
		throws Exception {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		List<Layout> ancestorLayouts = layout.getAncestors();

		Collections.reverse(ancestorLayouts);

		for (Layout ancestorLayout : ancestorLayouts) {
			jsonArray.put(HtmlUtil.escape(ancestorLayout.getName(locale)));
		}

		return jsonArray;
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}