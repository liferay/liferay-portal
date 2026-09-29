/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.display.context;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.object.model.ObjectDefinition;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Gianmarco Brunialti Masera
 */
public class ViewAnalyticsRoomTrendAnalyticsSectionDisplayContext
	extends BaseAnalyticsSectionDisplayContext {

	public ViewAnalyticsRoomTrendAnalyticsSectionDisplayContext(
		AnalyticsSettingsManager analyticsSettingsManager,
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition) {

		super(
			analyticsSettingsManager, null, null, null, httpServletRequest,
			objectDefinition);
	}

}