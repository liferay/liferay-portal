/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.util;

import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBCategoryLocalServiceUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Sergio González
 */
public class MBBreadcrumbUtil {

	public static void addPortletBreadcrumbEntries(
			long categoryId, HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws Exception {

		MBCategory category = null;

		if ((categoryId != MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) &&
			(categoryId != MBCategoryConstants.DISCUSSION_CATEGORY_ID)) {

			category = MBCategoryLocalServiceUtil.getCategory(categoryId);
		}

		addPortletBreadcrumbEntries(
			category, httpServletRequest, renderResponse);
	}

	public static void addPortletBreadcrumbEntries(
			MBCategory category, HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String mvcRenderCommandName = ParamUtil.getString(
			httpServletRequest, "mvcRenderCommandName");

		PortletURL portletURL = renderResponse.createRenderURL();

		if (mvcRenderCommandName.equals("/message_boards/select_category")) {
			portletURL.setParameter(
				"mvcRenderCommandName", "/message_boards/select_category");
			portletURL.setWindowState(LiferayWindowState.POP_UP);

			PortalUtil.addPortletBreadcrumbEntry(
				httpServletRequest, themeDisplay.translate("categories"),
				portletURL.toString());
		}
		else {
			portletURL.setParameter(
				"mvcRenderCommandName", "/message_boards/view");
		}

		PortalUtil.addPortletBreadcrumbEntry(
			httpServletRequest, themeDisplay.translate("home"),
			portletURL.toString());

		if (category == null) {
			return;
		}

		if (!mvcRenderCommandName.equals("/message_boards/select_category")) {
			portletURL.setParameter(
				"mvcRenderCommandName", "/message_boards/view_category");
		}

		List<MBCategory> ancestorCategories = category.getAncestors();

		Collections.reverse(ancestorCategories);

		for (MBCategory curCategory : ancestorCategories) {
			portletURL.setParameter(
				"mbCategoryId", String.valueOf(curCategory.getCategoryId()));

			PortalUtil.addPortletBreadcrumbEntry(
				httpServletRequest, curCategory.getName(),
				portletURL.toString());
		}

		portletURL.setParameter(
			"mbCategoryId", String.valueOf(category.getCategoryId()));

		PortalUtil.addPortletBreadcrumbEntry(
			httpServletRequest, category.getName(), portletURL.toString());
	}

	public static void addPortletBreadcrumbEntries(
			MBMessage message, HttpServletRequest httpServletRequest,
			RenderResponse renderResponse)
		throws Exception {

		if (message.getCategoryId() ==
				MBCategoryConstants.DISCUSSION_CATEGORY_ID) {

			return;
		}

		MBCategory category = null;

		if (message.getCategoryId() !=
				MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) {

			category = message.getCategory();
		}

		addPortletBreadcrumbEntries(
			category, httpServletRequest, renderResponse);

		PortalUtil.addPortletBreadcrumbEntry(
			httpServletRequest, message.getSubject(),
			PortletURLBuilder.createRenderURL(
				renderResponse
			).setMVCRenderCommandName(
				"/message_boards/view_message"
			).setParameter(
				"messageId", message.getMessageId()
			).buildString());
	}

}