/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.item.action.provider;

import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.exception.ContentDashboardItemActionException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author David Arques
 */
public interface ContentDashboardItemActionProvider<T> {

	public ContentDashboardItemAction getContentDashboardItemAction(
			T t, HttpServletRequest httpServletRequest)
		throws ContentDashboardItemActionException;

	public String getKey();

	public ContentDashboardItemAction.Type getType();

	public boolean isShow(T t, HttpServletRequest httpServletRequest);

}