/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.insights.portlet.shared.search;

import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.web.internal.search.insights.constants.SearchInsightsPortletKeys;
import com.liferay.portal.search.web.internal.search.insights.portlet.SearchInsightsPortletPreferences;
import com.liferay.portal.search.web.internal.search.insights.portlet.SearchInsightsPortletPreferencesImpl;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 */
@Component(
	property = "jakarta.portlet.name=" + SearchInsightsPortletKeys.SEARCH_INSIGHTS,
	service = PortletSharedSearchContributor.class
)
public class SearchInsightsPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchInsightsPortletPreferences searchInsightsPortletPreferences =
			new SearchInsightsPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(
				searchInsightsPortletPreferences.getFederatedSearchKey());

		searchRequestBuilder.explain(
			searchInsightsPortletPreferences.isExplain()
		).includeResponseString(
			true
		);
	}

}