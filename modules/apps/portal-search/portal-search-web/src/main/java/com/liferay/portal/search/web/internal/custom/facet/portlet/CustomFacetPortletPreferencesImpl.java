/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.custom.facet.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.portlet.preferences.BasePortletPreferences;

import jakarta.portlet.PortletPreferences;

/**
 * @author Wade Cao
 */
public class CustomFacetPortletPreferencesImpl
	extends BasePortletPreferences implements CustomFacetPortletPreferences {

	public CustomFacetPortletPreferencesImpl(
		PortletPreferences portletPreferences) {

		super(portletPreferences);
	}

	@Override
	public String getAggregationField() {
		return getString(
			CustomFacetPortletPreferences.PREFERENCE_KEY_AGGREGATION_FIELD,
			StringPool.BLANK);
	}

	@Override
	public String getAggregationType() {
		return getString(
			CustomFacetPortletPreferences.PREFERENCE_KEY_AGGREGATION_TYPE,
			"terms");
	}

	@Override
	public String getCustomHeading() {
		return getString(
			CustomFacetPortletPreferences.PREFERENCE_KEY_CUSTOM_HEADING,
			StringPool.BLANK);
	}

	@Override
	public String getFederatedSearchKey() {
		return getString(
			CustomFacetPortletPreferences.PREFERENCE_KEY_FEDERATED_SEARCH_KEY,
			StringPool.BLANK);
	}

	@Override
	public int getFrequencyThreshold() {
		return getInteger(
			CustomFacetPortletPreferences.PREFERENCE_KEY_FREQUENCY_THRESHOLD,
			1);
	}

	@Override
	public int getMaxTerms() {
		return getInteger(
			CustomFacetPortletPreferences.PREFERENCE_KEY_MAX_TERMS, 10);
	}

	@Override
	public String getOrder() {
		return getString(
			CustomFacetPortletPreferencesImpl.PREFERENCE_KEY_ORDER,
			"count:desc");
	}

	@Override
	public String getParameterName() {
		return getString(
			CustomFacetPortletPreferences.PREFERENCE_KEY_PARAMETER_NAME,
			StringPool.BLANK);
	}

	@Override
	public JSONArray getRangesJSONArray() {
		String rangesString = getRangesString();

		if (Validator.isBlank(rangesString)) {
			return _getDefaultRangesJSONArray();
		}

		try {
			return JSONFactoryUtil.createJSONArray(rangesString);
		}
		catch (JSONException jsonException) {
			_log.error(
				"Unable to create a JSON array from: " + rangesString,
				jsonException);

			return _getDefaultRangesJSONArray();
		}
	}

	@Override
	public String getRangesString() {
		return getString(
			CustomFacetPortletPreferences.PREFERENCE_KEY_RANGES,
			StringPool.BLANK);
	}

	@Override
	public boolean isFrequenciesVisible() {
		return getBoolean(
			CustomFacetPortletPreferences.PREFERENCE_KEY_FREQUENCIES_VISIBLE,
			true);
	}

	@Override
	public boolean isShowInputRange() {
		return getBoolean(
			CustomFacetPortletPreferences.PREFERENCE_KEY_SHOW_INPUT_RANGE,
			true);
	}

	private JSONArray _getDefaultDateRangesJSONArray() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		if (StringUtil.equals("range", getAggregationType())) {
			return jsonArray;
		}

		for (int i = 0; i < _DEFAULT_DATE_RANGE_LABELS.length; i++) {
			jsonArray.put(
				JSONUtil.put(
					"label", _DEFAULT_DATE_RANGE_LABELS[i]
				).put(
					"range", _DEFAULT_DATE_RANGE_RANGES[i]
				));
		}

		return jsonArray;
	}

	private JSONArray _getDefaultRangesJSONArray() {
		if (StringUtil.equals("dateRange", getAggregationType())) {
			return _getDefaultDateRangesJSONArray();
		}

		return JSONFactoryUtil.createJSONArray();
	}

	private static final String[] _DEFAULT_DATE_RANGE_LABELS = {
		"past-hour", "past-24-hours", "past-week", "past-month", "past-year"
	};

	private static final String[] _DEFAULT_DATE_RANGE_RANGES = {
		"[past-hour TO *]", "[past-24-hours TO *]", "[past-week TO *]",
		"[past-month TO *]", "[past-year TO *]"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		CustomFacetPortletPreferencesImpl.class);

}