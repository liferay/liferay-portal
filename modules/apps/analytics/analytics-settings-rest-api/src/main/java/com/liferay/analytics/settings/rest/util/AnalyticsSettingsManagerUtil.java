/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.util;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.portal.kernel.model.Group;

import jakarta.ws.rs.BadRequestException;

/**
 * @author Rachael Koestartyo
 */
public class AnalyticsSettingsManagerUtil {

	public static void checkSiteIdSynced(
			AnalyticsSettingsManager analyticsSettingsManager, Group group)
		throws Exception {

		if (!analyticsSettingsManager.isSiteIdSynced(
				group.getCompanyId(), group.getGroupId())) {

			throw new BadRequestException(
				"Analytics Cloud is not enabled for group " +
					group.getGroupId());
		}
	}

}