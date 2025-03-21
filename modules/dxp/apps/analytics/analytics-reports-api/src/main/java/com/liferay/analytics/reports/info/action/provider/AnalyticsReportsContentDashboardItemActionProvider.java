/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.info.action.provider;

import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.content.dashboard.item.action.exception.ContentDashboardItemActionException;
import com.liferay.info.item.InfoItemReference;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author David Arques
 */
public interface AnalyticsReportsContentDashboardItemActionProvider {

	public ContentDashboardItemAction getContentDashboardItemAction(
			HttpServletRequest httpServletRequest,
			InfoItemReference infoItemReference)
		throws ContentDashboardItemActionException;

	public boolean isShowContentDashboardItemAction(
			HttpServletRequest httpServletRequest,
			InfoItemReference infoItemReference)
		throws PortalException;

}