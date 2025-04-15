/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.options.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.search.web.internal.portlet.preferences.BasePortletPreferences;

import jakarta.portlet.PortletPreferences;

/**
 * @author Wade Cao
 */
public class SearchOptionsPortletPreferencesImpl
	extends BasePortletPreferences implements SearchOptionsPortletPreferences {

	public SearchOptionsPortletPreferencesImpl(
		PortletPreferences portletPreferences) {

		super(portletPreferences);
	}

	@Override
	public String getFederatedSearchKey() {
		return getString(
			SearchOptionsPortletPreferences.PREFERENCE_KEY_FEDERATED_SEARCH_KEY,
			StringPool.BLANK);
	}

	@Override
	public boolean isAllowEmptySearches() {
		return getBoolean(
			SearchOptionsPortletPreferences.PREFERENCE_KEY_ALLOW_EMPTY_SEARCHES,
			false);
	}

	@Override
	public boolean isBasicFacetSelection() {
		return getBoolean(
			SearchOptionsPortletPreferences.
				PREFERENCE_KEY_BASIC_FACET_SELECTION,
			false);
	}

	@Override
	public boolean isRetainFacetSelections() {
		return getBoolean(
			SearchOptionsPortletPreferences.
				PREFERENCE_KEY_RETAIN_FACET_SELECTIONS,
			false);
	}

}