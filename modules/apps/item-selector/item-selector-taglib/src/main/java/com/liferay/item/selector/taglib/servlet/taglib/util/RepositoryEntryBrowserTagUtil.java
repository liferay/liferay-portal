/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.servlet.taglib.util;

import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Roberto Díaz
 */
public class RepositoryEntryBrowserTagUtil {

	public static String getOrderByCol(
		HttpServletRequest httpServletRequest,
		PortalPreferences portalPreferences) {

		return SearchOrderByUtil.getOrderByCol(
			httpServletRequest,
			_TAGLIB_UI_REPOSITORY_ENTRY_BROWSER_PAGE_NAMESPACE, "title");
	}

	public static String getOrderByType(
		HttpServletRequest httpServletRequest,
		PortalPreferences portalPreferences) {

		return SearchOrderByUtil.getOrderByType(
			httpServletRequest,
			_TAGLIB_UI_REPOSITORY_ENTRY_BROWSER_PAGE_NAMESPACE, "asc");
	}

	private static final String
		_TAGLIB_UI_REPOSITORY_ENTRY_BROWSER_PAGE_NAMESPACE =
			"taglib_ui_repository_entry_browse_page";

}