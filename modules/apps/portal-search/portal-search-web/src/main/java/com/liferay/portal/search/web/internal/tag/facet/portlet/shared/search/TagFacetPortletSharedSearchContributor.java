/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.tag.facet.portlet.shared.search;

import com.liferay.portal.search.facet.tag.TagFacetSearchContributor;
import com.liferay.portal.search.web.internal.tag.facet.constants.TagFacetPortletKeys;
import com.liferay.portal.search.web.internal.tag.facet.portlet.TagFacetPortletPreferences;
import com.liferay.portal.search.web.internal.tag.facet.portlet.TagFacetPortletPreferencesImpl;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 */
@Component(
	property = "jakarta.portlet.name=" + TagFacetPortletKeys.TAG_FACET,
	service = PortletSharedSearchContributor.class
)
public class TagFacetPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		TagFacetPortletPreferences tagFacetPortletPreferences =
			new TagFacetPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		tagFacetSearchContributor.contribute(
			portletSharedSearchSettings.getSearchRequestBuilder(),
			tagFacetBuilder -> tagFacetBuilder.aggregationName(
				portletSharedSearchSettings.getPortletId()
			).frequencyThreshold(
				tagFacetPortletPreferences.getFrequencyThreshold()
			).maxTerms(
				tagFacetPortletPreferences.getMaxTerms()
			).selectedTagNames(
				portletSharedSearchSettings.getParameterValues(
					tagFacetPortletPreferences.getParameterName())
			));
	}

	@Reference
	protected TagFacetSearchContributor tagFacetSearchContributor;

}