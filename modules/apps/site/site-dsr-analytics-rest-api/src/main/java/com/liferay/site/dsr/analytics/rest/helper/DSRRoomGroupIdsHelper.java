/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.helper;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Tancredi Covioli
 */
public interface DSRRoomGroupIdsHelper {

	public String[] filterVisibleGroupIds(String[] groupIds)
		throws PortalException;

}