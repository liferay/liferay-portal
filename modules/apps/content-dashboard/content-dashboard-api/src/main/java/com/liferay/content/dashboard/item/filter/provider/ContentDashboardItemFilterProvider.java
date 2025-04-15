/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.item.filter.provider;

import com.liferay.content.dashboard.item.action.exception.ContentDashboardItemActionException;
import com.liferay.content.dashboard.item.filter.ContentDashboardItemFilter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Cristina González
 */
public interface ContentDashboardItemFilterProvider {

	public ContentDashboardItemFilter getContentDashboardItemFilter(
			HttpServletRequest httpServletRequest)
		throws ContentDashboardItemActionException;

	public String getKey();

	public ContentDashboardItemFilter.Type getType();

	public boolean isShow(HttpServletRequest httpServletRequest);

}