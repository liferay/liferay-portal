/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.user.facet.portlet.shared.search;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.facet.user.UserFacetSearchContributor;
import com.liferay.portal.search.web.internal.user.facet.constants.UserFacetPortletKeys;
import com.liferay.portal.search.web.internal.user.facet.portlet.UserFacetPortletPreferences;
import com.liferay.portal.search.web.internal.user.facet.portlet.UserFacetPortletPreferencesImpl;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 */
@Component(
	property = "jakarta.portlet.name=" + UserFacetPortletKeys.USER_FACET,
	service = PortletSharedSearchContributor.class
)
public class UserFacetPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		UserFacetPortletPreferences userFacetPortletPreferences =
			new UserFacetPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		userFacetSearchContributor.contribute(
			portletSharedSearchSettings.getSearchRequestBuilder(),
			userFacetBuilder -> userFacetBuilder.aggregationName(
				portletSharedSearchSettings.getPortletId()
			).frequencyThreshold(
				userFacetPortletPreferences.getFrequencyThreshold()
			).maxTerms(
				userFacetPortletPreferences.getMaxTerms()
			).selectedUserIds(
				_toLongArray(
					portletSharedSearchSettings.getParameterValues(
						userFacetPortletPreferences.getParameterName()))
			));
	}

	@Reference
	protected UserFacetSearchContributor userFacetSearchContributor;

	private long[] _toLongArray(String[] parameterValues) {
		if (ArrayUtil.isNotEmpty(parameterValues)) {
			return ListUtil.toLongArray(
				Arrays.asList(parameterValues), GetterUtil::getLong);
		}

		return new long[0];
	}

}