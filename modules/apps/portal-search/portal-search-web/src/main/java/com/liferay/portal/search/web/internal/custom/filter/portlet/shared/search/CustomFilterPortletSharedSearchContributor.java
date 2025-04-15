/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.custom.filter.portlet.shared.search;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.web.internal.custom.filter.constants.CustomFilterPortletKeys;
import com.liferay.portal.search.web.internal.custom.filter.portlet.CustomFilterPortletPreferences;
import com.liferay.portal.search.web.internal.custom.filter.portlet.CustomFilterPortletPreferencesImpl;
import com.liferay.portal.search.web.internal.custom.filter.portlet.CustomFilterPortletUtil;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(
	property = "jakarta.portlet.name=" + CustomFilterPortletKeys.CUSTOM_FILTER,
	service = PortletSharedSearchContributor.class
)
public class CustomFilterPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		CustomFilterPortletPreferences customFilterPortletPreferences =
			new CustomFilterPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		SearchRequestBuilder searchRequestBuilder =
			portletSharedSearchSettings.getFederatedSearchRequestBuilder(
				customFilterPortletPreferences.getFederatedSearchKey());

		searchRequestBuilder.addComplexQueryPart(
			_complexQueryPartBuilderFactory.builder(
			).boost(
				_getBoost(customFilterPortletPreferences)
			).disabled(
				customFilterPortletPreferences.isDisabled()
			).field(
				customFilterPortletPreferences.getFilterField()
			).name(
				customFilterPortletPreferences.getQueryName()
			).occur(
				customFilterPortletPreferences.getOccur()
			).parent(
				customFilterPortletPreferences.getParentQueryName()
			).type(
				customFilterPortletPreferences.getFilterQueryType()
			).value(
				_getFilterValue(
					portletSharedSearchSettings, customFilterPortletPreferences)
			).build());
	}

	private Float _getBoost(
		CustomFilterPortletPreferences customFilterPortletPreferences) {

		String boost = customFilterPortletPreferences.getBoost();

		if (Validator.isNull(boost)) {
			return null;
		}

		return GetterUtil.getFloat(boost);
	}

	private String _getFilterValue(
		PortletSharedSearchSettings portletSharedSearchSettings,
		CustomFilterPortletPreferences customFilterPortletPreferences) {

		String filterValue = customFilterPortletPreferences.getFilterValue();

		if (customFilterPortletPreferences.isImmutable()) {
			if (Validator.isNotNull(filterValue)) {
				return filterValue;
			}

			return null;
		}

		String parameterValue = portletSharedSearchSettings.getParameter(
			CustomFilterPortletUtil.getParameterName(
				customFilterPortletPreferences));

		if (parameterValue != null) {
			return parameterValue;
		}

		if (Validator.isNotNull(filterValue)) {
			return filterValue;
		}

		return null;
	}

	@Reference
	private ComplexQueryPartBuilderFactory _complexQueryPartBuilderFactory;

}