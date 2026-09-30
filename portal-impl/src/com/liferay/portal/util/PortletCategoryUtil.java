/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.PortletCategory;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.PortletCategoryKeys;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Eudaldo Alonso
 */
public class PortletCategoryUtil {

	public static Set<String> getFirstChildPortletIds(
		PortletCategory portletCategory) {

		Set<String> portletIds = portletCategory.getPortletIds();

		if (!portletIds.isEmpty()) {
			return portletIds;
		}

		for (PortletCategory curPortletCategory :
				portletCategory.getCategories()) {

			portletIds = curPortletCategory.getPortletIds();

			if (!portletIds.isEmpty()) {
				return portletIds;
			}

			getFirstChildPortletIds(curPortletCategory);
		}

		return Collections.emptySet();
	}

	public static String getPortletCategoryKey(
		String legacyPortletCategoryKey) {

		if (Objects.equals(legacyPortletCategoryKey, "apps") ||
			Objects.equals(legacyPortletCategoryKey, "marketplace")) {

			return PortletCategoryKeys.CONTROL_PANEL_APPS;
		}
		else if (Objects.equals(legacyPortletCategoryKey, "configuration") ||
				 Objects.equals(legacyPortletCategoryKey, "portal")) {

			return PortletCategoryKeys.CONTROL_PANEL_INSTANCE;
		}
		else if (Objects.equals(legacyPortletCategoryKey, "content")) {
			return PortletCategoryKeys.SITE_ADMINISTRATION_CONTENT;
		}
		else if (Objects.equals(legacyPortletCategoryKey, "my")) {
			return PortletCategoryKeys.USER_MY_ACCOUNT;
		}
		else if (Objects.equals(legacyPortletCategoryKey, "server")) {
			return PortletCategoryKeys.CONTROL_PANEL_SYSTEM;
		}
		else if (Objects.equals(legacyPortletCategoryKey, "sites")) {
			return PortletCategoryKeys.CONTROL_PANEL_SITES;
		}
		else if (Objects.equals(legacyPortletCategoryKey, "users")) {
			return PortletCategoryKeys.CONTROL_PANEL_USERS;
		}

		return legacyPortletCategoryKey;
	}

	public static PortletCategory getRelevantPortletCategory(
			PermissionChecker permissionChecker, long companyId, Layout layout,
			PortletCategory portletCategory,
			LayoutTypePortlet layoutTypePortlet)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		Set<String> panelSelectedPortletIds = SetUtil.fromArray(
			StringUtil.split(
				typeSettingsUnicodeProperties.getProperty(
					"panelSelectedPortlets")));

		return getRelevantPortletCategory(
			permissionChecker, companyId, layout, portletCategory,
			panelSelectedPortletIds, layoutTypePortlet);
	}

	protected static PortletCategory getRelevantPortletCategory(
			PermissionChecker permissionChecker, long companyId, Layout layout,
			PortletCategory portletCategory,
			Set<String> panelSelectedPortletIds,
			LayoutTypePortlet layoutTypePortlet)
		throws Exception {

		PortletCategory relevantPortletCategory = new PortletCategory(
			portletCategory.getName(), portletCategory.getPortletIds());

		for (PortletCategory curPortletCategory :
				portletCategory.getCategories()) {

			if (curPortletCategory.isHidden()) {
				continue;
			}

			Set<String> portletIds = new HashSet<>();

			for (String portletId : curPortletCategory.getPortletIds()) {
				Portlet portlet = PortletLocalServiceUtil.getPortletById(
					companyId, portletId);

				if (portlet == null) {
					continue;
				}

				if (portlet.isSystem() || !portlet.isInclude()) {
				}
				else if (!portlet.isActive() || portlet.isUndeployedPortlet()) {
				}
				else if (layout.isTypePanel() &&
						 panelSelectedPortletIds.contains(
							 portlet.getRootPortletId())) {

					portletIds.add(portlet.getPortletId());
				}
				else if (layout.isTypePanel() &&
						 !panelSelectedPortletIds.contains(
							 portlet.getRootPortletId())) {
				}
				else if (!PortletPermissionUtil.contains(
							permissionChecker, layout, portlet,
							ActionKeys.ADD_TO_PAGE)) {
				}
				else if (!portlet.isInstanceable() &&
						 layoutTypePortlet.hasPortletId(
							 portlet.getPortletId())) {

					portletIds.add(portlet.getPortletId());
				}
				else {
					portletIds.add(portlet.getPortletId());
				}
			}

			PortletCategory curRelevantPortletCategory =
				getRelevantPortletCategory(
					permissionChecker, companyId, layout, curPortletCategory,
					panelSelectedPortletIds, layoutTypePortlet);

			curRelevantPortletCategory.setPortletIds(portletIds);

			Collection<PortletCategory> categories =
				curRelevantPortletCategory.getCategories();

			if (!categories.isEmpty() || !portletIds.isEmpty()) {
				relevantPortletCategory.addCategory(curRelevantPortletCategory);
			}
		}

		return relevantPortletCategory;
	}

}