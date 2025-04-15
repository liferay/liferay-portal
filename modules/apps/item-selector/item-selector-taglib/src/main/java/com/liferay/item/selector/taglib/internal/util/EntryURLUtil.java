/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.internal.util;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorCriterion;
import com.liferay.item.selector.taglib.internal.servlet.item.selector.ItemSelectorUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import java.util.List;
import java.util.Objects;

/**
 * @author Adolfo Pérez
 */
public class EntryURLUtil {

	public static PortletURL getFolderPortletURL(
			Folder folder, LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse,
			PortletURL portletURL)
		throws PortalException, PortletException {

		PortletURL folderPortletURL = null;

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		if (folder.getGroupId() != group.getGroupId()) {
			group = GroupServiceUtil.getGroup(folder.getGroupId());
		}

		String scope = ParamUtil.getString(liferayPortletRequest, "scope");

		if (Objects.equals(scope, "everywhere")) {
			folderPortletURL = getGroupPortletURL(group, liferayPortletRequest);
		}
		else {
			folderPortletURL = PortletURLUtil.clone(
				portletURL, liferayPortletResponse);
		}

		folderPortletURL.setParameter(
			"folderId", String.valueOf(folder.getFolderId()));

		return folderPortletURL;
	}

	public static PortletURL getGroupPortletURL(
		Group group, LiferayPortletRequest liferayPortletRequest) {

		ItemSelector itemSelector = ItemSelectorUtil.getItemSelector();

		String itemSelectedEventName = ParamUtil.getString(
			liferayPortletRequest, "itemSelectedEventName");

		List<ItemSelectorCriterion> itemSelectorCriteria =
			itemSelector.getItemSelectorCriteria(
				liferayPortletRequest.getParameterMap());

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long refererGroupId = themeDisplay.getRefererGroupId();

		if (refererGroupId == 0) {
			refererGroupId = themeDisplay.getScopeGroupId();
		}

		return PortletURLBuilder.create(
			itemSelector.getItemSelectorURL(
				RequestBackedPortletURLFactoryUtil.create(
					liferayPortletRequest),
				group, refererGroupId, itemSelectedEventName,
				itemSelectorCriteria.toArray(new ItemSelectorCriterion[0]))
		).setParameter(
			"selectedTab",
			ParamUtil.getString(liferayPortletRequest, "selectedTab")
		).buildPortletURL();
	}

}