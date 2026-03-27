/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.item.selector.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * @author Stefano Motta
 */
public class OrganizationItemSelectorCriterionUtil {

	public static long[] toLongArray(
		String key, HttpServletRequest httpServletRequest) {

		HttpSession session = httpServletRequest.getSession();

		Object selectedOrganizationIds = session.getAttribute(key);

		if (selectedOrganizationIds != null) {
			return (long[])selectedOrganizationIds;
		}

		return new long[0];
	}

	public static String toString(
		HttpServletRequest httpServletRequest,
		long[] selectedOrganizationIds, String suffix) {

		String key = _KEY_PREFIX + suffix;

		HttpSession session = httpServletRequest.getSession();

		session.setAttribute(key, selectedOrganizationIds);

		return key;
	}

	private static final String _KEY_PREFIX = "SELECTED_ORGANIZATION_IDS_";

}