/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.sort.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.portlet.preferences.BasePortletPreferences;

import jakarta.portlet.PortletPreferences;

/**
 * @author Wade Cao
 */
public class SortPortletPreferencesImpl
	extends BasePortletPreferences implements SortPortletPreferences {

	public SortPortletPreferencesImpl(PortletPreferences portletPreferences) {
		super(portletPreferences);
	}

	@Override
	public JSONArray getFieldsJSONArray() {
		String fieldsString = getFieldsString();

		if (Validator.isBlank(fieldsString)) {
			return _getDefaultFieldsJSONArray();
		}

		try {
			return JSONFactoryUtil.createJSONArray(fieldsString);
		}
		catch (JSONException jsonException) {
			_log.error(
				"Unable to create a JSON array from: " + fieldsString,
				jsonException);

			return _getDefaultFieldsJSONArray();
		}
	}

	@Override
	public String getFieldsString() {
		return getString(
			SortPortletPreferences.PREFERENCE_KEY_FIELDS, StringPool.BLANK);
	}

	@Override
	public String getParameterName() {
		return "sort";
	}

	private JSONArray _getDefaultFieldsJSONArray() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (Preset preset : _presets) {
			jsonArray.put(
				JSONUtil.put(
					"field", preset._field
				).put(
					"label", preset._label
				));
		}

		return jsonArray;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SortPortletPreferencesImpl.class);

	private static final Preset[] _presets = {
		new Preset("", "relevance"), new Preset("title", "title"),
		new Preset("modified-", "modified"),
		new Preset("modified+", "modified-oldest-first"),
		new Preset("createDate-", "created"),
		new Preset("createDate+", "created-oldest-first"),
		new Preset("userName", "user")
	};

	private static class Preset {

		public Preset(String field, String label) {
			_field = field;
			_label = label;
		}

		private final String _field;
		private final String _label;

	}

}