/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.display.context;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.digital.sales.room.web.internal.constants.DSRSiteInitializerFDSNames;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.portal.kernel.util.HashMapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Gianmarco Brunialti Masera
 */
public class ViewAnalyticsMostActiveVisitorsAnalyticsSectionDisplayContext
	extends BaseAnalyticsSectionDisplayContext {

	public ViewAnalyticsMostActiveVisitorsAnalyticsSectionDisplayContext(
		AnalyticsSettingsManager analyticsSettingsManager,
		HttpServletRequest httpServletRequest,
		ObjectDefinition objectDefinition) {

		super(
			analyticsSettingsManager, null, null, null, httpServletRequest,
			objectDefinition);
	}

	@Override
	public Map<String, Object> getProps() {
		return HashMapBuilder.<String, Object>putAll(
			super.getProps()
		).put(
			"namespace", DSRSiteInitializerFDSNames.MOST_ACTIVE_VISITORS
		).build();
	}

}