/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.util.configuration;

import com.liferay.fragment.constants.FragmentConfigurationFieldDataType;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Jürgen Kappler
 */
@ProviderType
public interface FragmentEntryConfigurationParser {

	public JSONObject getConfigurationDefaultValuesJSONObject(
		JSONObject configurationJSONObject);

	public Object getConfigurationFieldValue(
		JSONObject editableValuesJSONObject, String fieldName,
		FragmentConfigurationFieldDataType fragmentConfigurationFieldDataType);

	public JSONObject getConfigurationJSONObject(
			JSONObject configurationJSONObject,
			JSONObject editableValuesJSONObject, Locale locale)
		throws JSONException;

	public Map<String, Object> getContextObjects(
		JSONObject configurationValuesJSONObject,
		JSONObject configurationJSONObject, Object displayObject,
		long[] segmentsEntryIds);

	public Object getFieldValue(
		JSONObject editableValuesJSONObject,
		FragmentConfigurationField fragmentConfigurationField, Locale locale);

	public Object getFieldValue(
		JSONObject configurationJSONObject, JSONObject editableValuesJSONObject,
		Locale locale, String name);

	public List<FragmentConfigurationField> getFragmentConfigurationFields(
		JSONObject configurationJSONObject);

	public JSONObject translateConfiguration(
		JSONObject jsonObject, ResourceBundle resourceBundle);

}