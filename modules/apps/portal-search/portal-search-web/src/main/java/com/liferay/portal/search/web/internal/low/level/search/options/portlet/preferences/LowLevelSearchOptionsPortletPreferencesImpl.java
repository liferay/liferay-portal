/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.low.level.search.options.portlet.preferences;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.portlet.preferences.BasePortletPreferences;
import com.liferay.portal.search.web.internal.search.options.portlet.SearchOptionsPortletPreferences;

import jakarta.portlet.PortletPreferences;

/**
 * @author Wade Cao
 */
public class LowLevelSearchOptionsPortletPreferencesImpl
	extends BasePortletPreferences
	implements LowLevelSearchOptionsPortletPreferences {

	public LowLevelSearchOptionsPortletPreferencesImpl(
		PortletPreferences portletPreferences) {

		super(portletPreferences);
	}

	@Override
	public JSONArray getAttributesJSONArray() {
		String fieldsString = getAttributesString();

		if (Validator.isBlank(fieldsString)) {
			return _getDefaultAttributesJSONArray();
		}

		try {
			return JSONFactoryUtil.createJSONArray(fieldsString);
		}
		catch (JSONException jsonException) {
			_log.error(
				"Unable to create a JSON array from: " + fieldsString,
				jsonException);

			return _getDefaultAttributesJSONArray();
		}
	}

	@Override
	public String getAttributesString() {
		return getString(
			LowLevelSearchOptionsPortletPreferences.PREFERENCE_ATTRIBUTES,
			StringPool.BLANK);
	}

	@Override
	public String getConnectionId() {
		return getString(
			LowLevelSearchOptionsPortletPreferences.
				PREFERENCE_KEY_CONNECTION_ID,
			StringPool.BLANK);
	}

	@Override
	public String getContributorsToExclude() {
		return getString(
			LowLevelSearchOptionsPortletPreferences.
				PREFERENCE_KEY_CONTRIBUTORS_TO_EXCLUDE,
			StringPool.BLANK);
	}

	@Override
	public String getContributorsToInclude() {
		return getString(
			LowLevelSearchOptionsPortletPreferences.
				PREFERENCE_KEY_CONTRIBUTORS_TO_INCLUDE,
			StringPool.BLANK);
	}

	@Override
	public String getFederatedSearchKey() {
		return getString(
			SearchOptionsPortletPreferences.PREFERENCE_KEY_FEDERATED_SEARCH_KEY,
			StringPool.BLANK);
	}

	@Override
	public String getFieldsToReturn() {
		return getString(
			LowLevelSearchOptionsPortletPreferences.
				PREFERENCE_KEY_FIELDS_TO_RETURN,
			StringPool.BLANK);
	}

	@Override
	public String getIndexes() {
		return getString(
			LowLevelSearchOptionsPortletPreferences.PREFERENCE_KEY_INDEXES,
			StringPool.BLANK);
	}

	private JSONArray _getDefaultAttributesJSONArray() {
		return JSONUtil.put(
			JSONUtil.put(
				"key", StringPool.BLANK
			).put(
				"value", StringPool.BLANK
			));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LowLevelSearchOptionsPortletPreferencesImpl.class);

}