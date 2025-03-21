/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.struts;

import com.liferay.layout.taglib.internal.util.LayoutUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sandro Chinea
 */
@Component(property = "path=/portal/find_layouts", service = StrutsAction.class)
public class FindLayoutsStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		long groupId = ParamUtil.getLong(httpServletRequest, "groupId");

		Group group = _groupLocalService.fetchGroup(groupId);

		String keywords = ParamUtil.getString(httpServletRequest, "keywords");

		if ((group == null) || Validator.isNull(keywords)) {
			jsonObject.put("layouts", _jsonFactory.createJSONArray());

			ServletResponseUtil.write(
				httpServletResponse, jsonObject.toString());

			return null;
		}

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		boolean privateLayout = ParamUtil.getBoolean(
			httpServletRequest, "privateLayout");
		boolean searchOnlyByTitle = ParamUtil.getBoolean(
			httpServletRequest, "searchOnlyByTitle");

		int layoutsCount = _layoutLocalService.searchCount(
			group, privateLayout, keywords, searchOnlyByTitle,
			new String[] {
				LayoutConstants.TYPE_CONTENT, LayoutConstants.TYPE_EMBEDDED,
				LayoutConstants.TYPE_FULL_PAGE_APPLICATION,
				LayoutConstants.TYPE_LINK_TO_LAYOUT, LayoutConstants.TYPE_PANEL,
				LayoutConstants.TYPE_PORTLET, LayoutConstants.TYPE_URL
			});

		boolean hasMoreElements = false;

		if (layoutsCount > 0) {
			int start = ParamUtil.getInteger(
				httpServletRequest, "start", QueryUtil.ALL_POS);

			if (start != QueryUtil.ALL_POS) {
				start = Math.max(0, start);
			}

			int pageSize = GetterUtil.getInteger(
				PropsValues.LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN);

			int end = ParamUtil.getInteger(
				httpServletRequest, "end", start + pageSize);

			if ((start == QueryUtil.ALL_POS) || (pageSize <= 0)) {
				start = QueryUtil.ALL_POS;
				end = QueryUtil.ALL_POS;
			}

			int startEndMax = Math.max(start, end);

			if ((startEndMax != QueryUtil.ALL_POS) && (pageSize > 0) &&
				(layoutsCount > startEndMax)) {

				hasMoreElements = true;
			}

			List<Layout> layouts = _layoutLocalService.search(
				groupId, privateLayout, keywords, searchOnlyByTitle,
				new String[] {
					LayoutConstants.TYPE_CONTENT, LayoutConstants.TYPE_EMBEDDED,
					LayoutConstants.TYPE_FULL_PAGE_APPLICATION,
					LayoutConstants.TYPE_LINK_TO_LAYOUT,
					LayoutConstants.TYPE_PANEL, LayoutConstants.TYPE_PORTLET,
					LayoutConstants.TYPE_URL
				},
				start, end, null);

			boolean checkDisplayPage = ParamUtil.getBoolean(
				httpServletRequest, "checkDisplayPage");
			boolean enableCurrentPage = ParamUtil.getBoolean(
				httpServletRequest, "enableCurrentPage");
			String itemSelectorReturnType = ParamUtil.getString(
				httpServletRequest, "itemSelectorReturnType");
			long selPlid = ParamUtil.getLong(
				httpServletRequest, "selPlid", LayoutConstants.DEFAULT_PLID);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			for (Layout layout : layouts) {
				jsonArray.put(
					JSONUtil.put(
						"disabled",
						() -> {
							if ((checkDisplayPage &&
								 !layout.isContentDisplayPage()) ||
								(!enableCurrentPage &&
								 (layout.getPlid() == selPlid))) {

								return true;
							}

							return false;
						}
					).put(
						"groupId", layout.getGroupId()
					).put(
						"id", layout.getUuid()
					).put(
						"layoutId", layout.getLayoutId()
					).put(
						"name", layout.getName(themeDisplay.getLocale())
					).put(
						"path",
						_getLayoutPathJSONArray(
							layout, themeDisplay.getLocale())
					).put(
						"payload",
						LayoutUtil.getLayoutPayload(
							httpServletRequest, itemSelectorReturnType, layout,
							themeDisplay)
					).put(
						"privateLayout", layout.isPrivateLayout()
					).put(
						"returnType", itemSelectorReturnType
					).put(
						"value", layout.getBreadcrumb(themeDisplay.getLocale())
					));
			}
		}

		jsonObject.put(
			"hasMoreElements", hasMoreElements
		).put(
			"layouts", jsonArray
		);

		ServletResponseUtil.write(httpServletResponse, jsonObject.toString());

		return null;
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
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

}