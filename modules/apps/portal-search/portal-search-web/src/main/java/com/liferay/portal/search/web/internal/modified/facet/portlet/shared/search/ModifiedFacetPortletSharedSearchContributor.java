/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.modified.facet.portlet.shared.search;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.facet.modified.ModifiedFacetFactory;
import com.liferay.portal.search.web.internal.modified.facet.constants.ModifiedFacetPortletKeys;
import com.liferay.portal.search.web.internal.modified.facet.portlet.ModifiedFacetPortletPreferences;
import com.liferay.portal.search.web.internal.modified.facet.portlet.ModifiedFacetPortletPreferencesImpl;
import com.liferay.portal.search.web.internal.range.BaseRangeFacetPortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 * @author Adam Brandizzi
 * @author André de Oliveira
 * @author Petteri Karttunen
 */
@Component(
	property = "jakarta.portlet.name=" + ModifiedFacetPortletKeys.MODIFIED_FACET,
	service = PortletSharedSearchContributor.class
)
public class ModifiedFacetPortletSharedSearchContributor
	extends BaseRangeFacetPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		ModifiedFacetPortletPreferences modifiedFacetPortletPreferences =
			new ModifiedFacetPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		JSONArray rangesJSONArray = getDateRangesJSONArray(
			CalendarFactoryUtil.getCalendar(),
			modifiedFacetPortletPreferences.getRangesJSONArray());

		List<String> selectedRangeStrings = getSelectedRangeStrings(
			"dateRange", modifiedFacetPortletPreferences.getParameterName(),
			portletSharedSearchSettings, rangesJSONArray);

		String selectedCustomRangeString = getSelectedCustomRangeString(
			"dateRange", modifiedFacetPortletPreferences.getParameterName(),
			portletSharedSearchSettings);

		if (!Validator.isBlank(selectedCustomRangeString)) {
			addCustomRange(
				rangesJSONArray, selectedCustomRangeString,
				selectedRangeStrings);
		}

		Facet facet = _modifiedFacetFactory.newInstance(
			portletSharedSearchSettings.getSearchContext());

		facet.setFacetConfiguration(
			_buildFacetConfiguration(
				facet.getFieldName(), modifiedFacetPortletPreferences,
				rangesJSONArray));

		if (!selectedRangeStrings.isEmpty()) {
			facet.select(selectedRangeStrings.toArray(new String[0]));
		}

		portletSharedSearchSettings.addFacet(facet);
	}

	private FacetConfiguration _buildFacetConfiguration(
		String fieldName,
		ModifiedFacetPortletPreferences modifiedFacetPortletPreferences,
		JSONArray rangesJSONArray) {

		FacetConfiguration facetConfiguration = new FacetConfiguration();

		facetConfiguration.setFieldName(fieldName);
		facetConfiguration.setLabel("any-time");
		facetConfiguration.setOrder(modifiedFacetPortletPreferences.getOrder());
		facetConfiguration.setStatic(false);
		facetConfiguration.setWeight(1.0);

		JSONObject jsonObject = facetConfiguration.getData();

		jsonObject.put("ranges", rangesJSONArray);

		return facetConfiguration;
	}

	@Reference
	private ModifiedFacetFactory _modifiedFacetFactory;

}