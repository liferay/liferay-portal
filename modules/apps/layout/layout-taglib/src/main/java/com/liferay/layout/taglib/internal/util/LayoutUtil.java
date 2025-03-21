/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.taglib.internal.util;

import com.liferay.item.selector.criteria.UUIDItemSelectorReturnType;
import com.liferay.layout.item.selector.LayoutItemSelectorReturnType;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutServiceUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class LayoutUtil {

	public static String getLayoutPayload(
			HttpServletRequest httpServletRequest,
			String itemSelectorReturnType, Layout layout,
			ThemeDisplay themeDisplay)
		throws Exception {

		if (Objects.equals(
				LayoutItemSelectorReturnType.class.getName(),
				itemSelectorReturnType)) {

			return JSONUtil.put(
				"layoutId", layout.getLayoutId()
			).put(
				"name", layout.getName(themeDisplay.getLocale())
			).put(
				"plid", layout.getPlid()
			).put(
				"previewURL",
				() -> {
					String layoutURL = HttpComponentsUtil.addParameter(
						PortalUtil.getLayoutFullURL(layout, themeDisplay),
						"p_l_mode", Constants.PREVIEW);

					return HttpComponentsUtil.addParameter(
						layoutURL, "p_p_auth",
						AuthTokenUtil.getToken(httpServletRequest));
				}
			).put(
				"private", layout.isPrivateLayout()
			).put(
				"url", PortalUtil.getLayoutFullURL(layout, themeDisplay)
			).put(
				"uuid", layout.getUuid()
			).toString();
		}
		else if (Objects.equals(
					UUIDItemSelectorReturnType.class.getName(),
					itemSelectorReturnType)) {

			return layout.getUuid();
		}

		return PortalUtil.getLayoutRelativeURL(layout, themeDisplay, false);
	}

	public static JSONObject getLayoutsJSONObject(
			boolean checkDisplayPage, boolean enableCurrentPage, long groupId,
			HttpServletRequest httpServletRequest,
			String itemSelectorReturnType, boolean privateLayout,
			long parentLayoutId, String[] selectedLayoutUuid, long selPlid,
			int start, int end)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		List<Layout> layouts = ListUtil.filter(
			LayoutServiceUtil.getLayouts(
				groupId, privateLayout, parentLayoutId),
			layout -> !_isExcludedLayout(layout));

		for (Layout layout : ListUtil.subList(layouts, start, end)) {
			List<Layout> childLayouts = ListUtil.filter(
				LayoutServiceUtil.getLayouts(
					groupId, layout.isPrivateLayout(), layout.getLayoutId()),
				curLayout -> !_isExcludedLayout(curLayout));

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

						return null;
					}
				).put(
					"groupId", layout.getGroupId()
				).put(
					"hasChildren", ListUtil.isNotEmpty(childLayouts)
				).put(
					"hasGuestViewPermission",
					() -> {
						Role role = RoleLocalServiceUtil.getRole(
							layout.getCompanyId(), RoleConstants.GUEST);

						return ResourcePermissionLocalServiceUtil.
							hasResourcePermission(
								layout.getCompanyId(), Layout.class.getName(),
								ResourceConstants.SCOPE_INDIVIDUAL,
								String.valueOf(layout.getPlid()),
								role.getRoleId(), ActionKeys.VIEW);
					}
				).put(
					"icon", layout.getIcon()
				).put(
					"id", layout.getUuid()
				).put(
					"layoutId", layout.getLayoutId()
				).put(
					"name", layout.getName(themeDisplay.getLocale())
				).put(
					"paginated",
					() -> {
						if (childLayouts.size() >
								PropsValues.
									LAYOUT_MANAGE_PAGES_INITIAL_CHILDREN) {

							return true;
						}

						return false;
					}
				).put(
					"payload",
					getLayoutPayload(
						httpServletRequest, itemSelectorReturnType, layout,
						themeDisplay)
				).put(
					"privateLayout", layout.isPrivateLayout()
				).put(
					"returnType", itemSelectorReturnType
				).put(
					"selected",
					() -> {
						if (ArrayUtil.contains(
								selectedLayoutUuid, layout.getUuid())) {

							return true;
						}

						return null;
					}
				).put(
					"url",
					PortalUtil.getLayoutRelativeURL(layout, themeDisplay, false)
				).put(
					"value", layout.getBreadcrumb(themeDisplay.getLocale())
				));
		}

		return JSONUtil.put(
			"items", jsonArray
		).put(
			"total", layouts.size()
		);
	}

	private static boolean _isExcludedLayout(Layout layout) {
		if (!layout.isTypeContent()) {
			return false;
		}

		if (layout.fetchDraftLayout() != null) {
			return !layout.isPublished();
		}

		if (layout.isApproved() && !layout.isHidden() && !layout.isSystem()) {
			return false;
		}

		return true;
	}

}