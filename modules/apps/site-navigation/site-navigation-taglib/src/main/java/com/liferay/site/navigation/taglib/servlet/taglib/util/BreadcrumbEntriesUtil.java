/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.taglib.servlet.taglib.util;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class BreadcrumbEntriesUtil {

	public static List<BreadcrumbEntry> getBreadcrumbEntries(
		HttpServletRequest httpServletRequest, boolean showCurrentGroup,
		boolean showGuestGroup, boolean showLayout, boolean showParentGroups,
		boolean showPortletBreadcrumb) {

		List<Integer> breadcrumbEntryTypes = new ArrayList<>();

		if (showCurrentGroup) {
			breadcrumbEntryTypes.add(BreadcrumbUtil.ENTRY_TYPE_CURRENT_GROUP);
		}

		if (showGuestGroup) {
			breadcrumbEntryTypes.add(BreadcrumbUtil.ENTRY_TYPE_GUEST_GROUP);
		}

		if (showLayout) {
			breadcrumbEntryTypes.add(BreadcrumbUtil.ENTRY_TYPE_LAYOUT);
		}

		if (showParentGroups) {
			breadcrumbEntryTypes.add(BreadcrumbUtil.ENTRY_TYPE_PARENT_GROUP);
		}

		if (showPortletBreadcrumb) {
			breadcrumbEntryTypes.add(BreadcrumbUtil.ENTRY_TYPE_PORTLET);
		}

		try {
			return BreadcrumbUtil.getBreadcrumbEntries(
				httpServletRequest, ArrayUtil.toIntArray(breadcrumbEntryTypes));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return Collections.emptyList();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BreadcrumbEntriesUtil.class);

}