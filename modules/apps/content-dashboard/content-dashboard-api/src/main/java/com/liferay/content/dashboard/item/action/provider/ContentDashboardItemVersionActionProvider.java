/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.item.action.provider;

import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionAction;
import com.liferay.content.dashboard.item.action.exception.ContentDashboardItemVersionActionException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Stefan Tanasie
 */
public interface ContentDashboardItemVersionActionProvider<T> {

	public ContentDashboardItemVersionAction
			getContentDashboardItemVersionAction(
				T t, HttpServletRequest httpServletRequest)
		throws ContentDashboardItemVersionActionException;

	public boolean isShow(T t, HttpServletRequest httpServletRequest);

}