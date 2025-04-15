/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.low.level.search.options.portlet.shared.search;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.web.internal.low.level.search.options.constants.LowLevelSearchOptionsPortletKeys;
import com.liferay.portal.search.web.internal.low.level.search.options.portlet.preferences.LowLevelSearchOptionsPortletPreferences;
import com.liferay.portal.search.web.internal.low.level.search.options.portlet.preferences.LowLevelSearchOptionsPortletPreferencesImpl;
import com.liferay.portal.search.web.internal.util.SearchStringUtil;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(
	property = "jakarta.portlet.name=" + LowLevelSearchOptionsPortletKeys.LOW_LEVEL_SEARCH_OPTIONS,
	service = PortletSharedSearchContributor.class
)
public class LowLevelSearchOptionsPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		LowLevelSearchOptionsPortletPreferences
			lowLevelSearchOptionsPortletPreferences =
				new LowLevelSearchOptionsPortletPreferencesImpl(
					portletSharedSearchSettings.getPortletPreferences());

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(
				lowLevelSearchOptionsPortletPreferences.
					getFederatedSearchKey());

		searchRequestBuilder.connectionId(
			lowLevelSearchOptionsPortletPreferences.getConnectionId()
		).excludeContributors(
			SearchStringUtil.splitAndUnquote(
				lowLevelSearchOptionsPortletPreferences.
					getContributorsToExclude())
		).fields(
			SearchStringUtil.splitAndUnquote(
				lowLevelSearchOptionsPortletPreferences.getFieldsToReturn())
		).includeContributors(
			SearchStringUtil.splitAndUnquote(
				lowLevelSearchOptionsPortletPreferences.
					getContributorsToInclude())
		).indexes(
			SearchStringUtil.splitAndUnquote(
				lowLevelSearchOptionsPortletPreferences.getIndexes())
		).withSearchContext(
			searchContext -> {
				if (Validator.isNull(
						searchContext.getAttribute(
							"search.experiences.ip.address"))) {

					HttpServletRequest httpServletRequest =
						_portal.getHttpServletRequest(
							portletSharedSearchSettings.getRenderRequest());

					searchContext.setAttribute(
						"search.experiences.ip.address",
						httpServletRequest.getRemoteAddr());
				}

				if (Validator.isNull(
						searchContext.getAttribute(
							"search.experiences.scope.group.id"))) {

					ThemeDisplay themeDisplay =
						portletSharedSearchSettings.getThemeDisplay();

					searchContext.setAttribute(
						"search.experiences.scope.group.id",
						themeDisplay.getScopeGroupId());
				}

				_applyAttributes(
					lowLevelSearchOptionsPortletPreferences, searchContext);
			}
		);
	}

	private void _applyAttributes(
		LowLevelSearchOptionsPortletPreferences
			lowLevelSearchOptionsPortletPreferences,
		SearchContext searchContext) {

		for (Object object :
				lowLevelSearchOptionsPortletPreferences.
					getAttributesJSONArray()) {

			JSONObject jsonObject = (JSONObject)object;

			searchContext.setAttribute(
				jsonObject.getString("key"),
				(Serializable)jsonObject.get("value"));
		}
	}

	@Reference
	private Portal _portal;

}