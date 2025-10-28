/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.util.configuration;

import com.liferay.fragment.constants.FragmentConfigurationFieldDataType;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Lourdes Fernández Besada
 */
public class FragmentEntryConfigurationParserUtil {

	public static JSONObject getConfigurationDefaultValuesJSONObject(
		JSONObject configurationJSONObject) {

		FragmentEntryConfigurationParser fragmentEntryConfigurationParser =
			getFragmentEntryConfigurationParser();

		return fragmentEntryConfigurationParser.
			getConfigurationDefaultValuesJSONObject(configurationJSONObject);
	}

	public static Object getConfigurationFieldValue(
		JSONObject editableValuesJSONObject, String fieldName,
		FragmentConfigurationFieldDataType fragmentConfigurationFieldDataType) {

		FragmentEntryConfigurationParser fragmentEntryConfigurationParser =
			getFragmentEntryConfigurationParser();

		return fragmentEntryConfigurationParser.getConfigurationFieldValue(
			editableValuesJSONObject, fieldName,
			fragmentConfigurationFieldDataType);
	}

	public static JSONObject getConfigurationJSONObject(
			JSONObject configurationJSONObject,
			JSONObject editableValuesJSONObject, Locale locale)
		throws JSONException {

		FragmentEntryConfigurationParser fragmentEntryConfigurationParser =
			getFragmentEntryConfigurationParser();

		return fragmentEntryConfigurationParser.getConfigurationJSONObject(
			configurationJSONObject, editableValuesJSONObject, locale);
	}

	public static Map<String, Object> getContextObjects(
		JSONObject configurationValuesJSONObject,
		JSONObject configurationJSONObject, Object displayObject,
		long[] segmentsEntryIds) {

		FragmentEntryConfigurationParser fragmentEntryConfigurationParser =
			getFragmentEntryConfigurationParser();

		return fragmentEntryConfigurationParser.getContextObjects(
			configurationValuesJSONObject, configurationJSONObject,
			displayObject, segmentsEntryIds);
	}

	public static Object getFieldValue(
		JSONObject editableValuesJSONObject,
		FragmentConfigurationField fragmentConfigurationField, Locale locale) {

		FragmentEntryConfigurationParser fragmentEntryConfigurationParser =
			getFragmentEntryConfigurationParser();

		return fragmentEntryConfigurationParser.getFieldValue(
			editableValuesJSONObject, fragmentConfigurationField, locale);
	}

	public static Object getFieldValue(
		JSONObject configurationJSONObject, JSONObject editableValuesJSONObject,
		Locale locale, String name) {

		FragmentEntryConfigurationParser fragmentEntryConfigurationParser =
			getFragmentEntryConfigurationParser();

		return fragmentEntryConfigurationParser.getFieldValue(
			configurationJSONObject, editableValuesJSONObject, locale, name);
	}

	public static List<FragmentConfigurationField>
		getFragmentConfigurationFields(JSONObject configurationJSONObject) {

		FragmentEntryConfigurationParser fragmentEntryConfigurationParser =
			getFragmentEntryConfigurationParser();

		return fragmentEntryConfigurationParser.getFragmentConfigurationFields(
			configurationJSONObject);
	}

	public static FragmentEntryConfigurationParser
		getFragmentEntryConfigurationParser() {

		return _fragmentEntryConfigurationParserSnapshot.get();
	}

	public static JSONObject translateConfiguration(
		JSONObject jsonObject, ResourceBundle resourceBundle) {

		FragmentEntryConfigurationParser fragmentEntryConfigurationParser =
			getFragmentEntryConfigurationParser();

		return fragmentEntryConfigurationParser.translateConfiguration(
			jsonObject, resourceBundle);
	}

	private static final Snapshot<FragmentEntryConfigurationParser>
		_fragmentEntryConfigurationParserSnapshot = new Snapshot<>(
			FragmentEntryConfigurationParserUtil.class,
			FragmentEntryConfigurationParser.class);

}