/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.item;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Jürgen Kappler
 */
public interface VersionableContentDashboardItem<T>
	extends ContentDashboardItem<T> {

	public List<ContentDashboardItemVersion> getAllContentDashboardItemVersions(
		HttpServletRequest httpServletRequest);

	public String getViewVersionsURL(HttpServletRequest httpServletRequest);

	public default boolean isShowContentDashboardItemVersions(
		HttpServletRequest httpServletRequest) {

		return true;
	}

}