/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib;

import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.item.selector.taglib.internal.display.context.GroupSelectorDisplayContext;
import com.liferay.item.selector.taglib.internal.util.GroupItemSelectorProviderRegistryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.taglib.servlet.taglib.util.BreadcrumbEntryListBuilder;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

/**
 * @author Georgel Pop
 */
public class GroupSelectorBreadcrumbUtil {

	public static void addPortletBreadcrumbEntries(
		Group group, HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse, PortletURL portletURL) {

		for (BreadcrumbEntry breadcrumbEntry :
				getBreadcrumbEntries(
					group, httpServletRequest, liferayPortletResponse,
					portletURL)) {

			PortalUtil.addPortletBreadcrumbEntry(
				httpServletRequest, breadcrumbEntry.getTitle(),
				breadcrumbEntry.getURL());
		}
	}

	public static List<BreadcrumbEntry> getBreadcrumbEntries(
		Group group, HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse, PortletURL portletURL) {

		GroupItemSelectorProvider groupItemSelectorProvider =
			GroupItemSelectorProviderRegistryUtil.getGroupItemSelectorProvider(
				group);
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return BreadcrumbEntryListBuilder.add(
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(
					LanguageUtil.get(
						httpServletRequest, "sites-and-libraries"));
				breadcrumbEntry.setURL(
					getGroupSelectorURL(
						httpServletRequest, liferayPortletResponse,
						portletURL));
			}
		).add(
			() -> {
				if (!FeatureFlagManagerUtil.isEnabled(
						themeDisplay.getCompanyId(), "LPD-81022") ||
					(groupItemSelectorProvider == null)) {

					return false;
				}

				LiferayPortletRequest liferayPortletRequest =
					PortalUtil.getLiferayPortletRequest(
						(PortletRequest)httpServletRequest.getAttribute(
							JavaConstants.JAKARTA_PORTLET_REQUEST));

				GroupSelectorDisplayContext groupSelectorDisplayContext =
					new GroupSelectorDisplayContext(liferayPortletRequest);

				Set<String> groupTypes =
					groupSelectorDisplayContext.getGroupTypes();

				return groupTypes.contains(
					groupItemSelectorProvider.getGroupType());
			},
			breadcrumbEntry -> {
				breadcrumbEntry.setTitle(
					groupItemSelectorProvider.getLabel(
						themeDisplay.getLocale()));
				breadcrumbEntry.setURL(
					_getGroupSelectorURL(
						groupItemSelectorProvider.getGroupType(),
						liferayPortletResponse, portletURL));
			}
		).build();
	}

	public static String getGroupSelectorURL(
			HttpServletRequest httpServletRequest,
			LiferayPortletResponse liferayPortletResponse,
			PortletURL portletURL)
		throws PortletException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-81022")) {

			return _getGroupSelectorURL(
				StringPool.BLANK, liferayPortletResponse, portletURL);
		}

		return _getGroupSelectorURL("site", liferayPortletResponse, portletURL);
	}

	private static String _getGroupSelectorURL(
			String groupType, LiferayPortletResponse liferayPortletResponse,
			PortletURL portletURL)
		throws PortletException {

		return PortletURLBuilder.create(
			PortletURLUtil.clone(portletURL, liferayPortletResponse)
		).setParameter(
			"groupType", groupType
		).setParameter(
			"showGroupSelector", true
		).buildString();
	}

}