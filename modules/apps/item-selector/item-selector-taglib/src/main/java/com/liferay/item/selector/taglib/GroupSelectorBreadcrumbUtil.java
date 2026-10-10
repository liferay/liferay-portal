/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib;

import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.item.selector.taglib.internal.util.GroupItemSelectorProviderRegistryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Georgel Pop
 */
public class GroupSelectorBreadcrumbUtil {

	public static List<BreadcrumbEntry> getBreadcrumbEntries(
			Group group, HttpServletRequest httpServletRequest,
			LiferayPortletResponse liferayPortletResponse,
			PortletURL portletURL)
		throws PortletException {

		List<BreadcrumbEntry> breadcrumbEntries = new ArrayList<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-81022")) {

			breadcrumbEntries.add(
				_getBreadcrumbEntry(
					"site", liferayPortletResponse, portletURL,
					LanguageUtil.get(
						httpServletRequest, "sites-and-libraries")));

			return breadcrumbEntries;
		}

		breadcrumbEntries.add(
			_getBreadcrumbEntry(
				StringPool.BLANK, liferayPortletResponse, portletURL,
				LanguageUtil.get(httpServletRequest, "sites-and-libraries")));

		GroupItemSelectorProvider groupItemSelectorProvider =
			GroupItemSelectorProviderRegistryUtil.getGroupItemSelectorProvider(
				group);

		if (groupItemSelectorProvider != null) {
			breadcrumbEntries.add(
				_getBreadcrumbEntry(
					groupItemSelectorProvider.getGroupType(),
					liferayPortletResponse, portletURL,
					groupItemSelectorProvider.getLabel(
						themeDisplay.getLocale())));
		}

		return breadcrumbEntries;
	}

	private static BreadcrumbEntry _getBreadcrumbEntry(
			String groupType, LiferayPortletResponse liferayPortletResponse,
			PortletURL portletURL, String title)
		throws PortletException {

		BreadcrumbEntry breadcrumbEntry = new BreadcrumbEntry();

		breadcrumbEntry.setTitle(title);
		breadcrumbEntry.setURL(
			PortletURLBuilder.create(
				PortletURLUtil.clone(portletURL, liferayPortletResponse)
			).setParameter(
				"groupType", groupType
			).setParameter(
				"showGroupSelector", true
			).buildString());

		return breadcrumbEntry;
	}

}