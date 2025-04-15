/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.tag.facet.portlet;

import com.liferay.portal.search.web.internal.portlet.preferences.BasePortletPreferences;

import jakarta.portlet.PortletPreferences;

/**
 * @author Lino Alves
 */
public class TagFacetPortletPreferencesImpl
	extends BasePortletPreferences implements TagFacetPortletPreferences {

	public TagFacetPortletPreferencesImpl(
		PortletPreferences portletPreferences) {

		super(portletPreferences);
	}

	@Override
	public String getDisplayStyle() {
		return getString(
			TagFacetPortletPreferences.PREFERENCE_KEY_DISPLAY_STYLE, "cloud");
	}

	@Override
	public int getFrequencyThreshold() {
		return getInteger(
			TagFacetPortletPreferences.PREFERENCE_KEY_FREQUENCY_THRESHOLD, 1);
	}

	@Override
	public int getMaxTerms() {
		return getInteger(
			TagFacetPortletPreferences.PREFERENCE_KEY_MAX_TERMS, 10);
	}

	@Override
	public String getOrder() {
		return getString(
			TagFacetPortletPreferences.PREFERENCE_KEY_ORDER, "count:desc");
	}

	@Override
	public String getParameterName() {
		return getString(
			TagFacetPortletPreferences.PREFERENCE_KEY_PARAMETER_NAME, "tag");
	}

	@Override
	public boolean isFrequenciesVisible() {
		return getBoolean(
			TagFacetPortletPreferences.PREFERENCE_KEY_FREQUENCIES_VISIBLE,
			true);
	}

}