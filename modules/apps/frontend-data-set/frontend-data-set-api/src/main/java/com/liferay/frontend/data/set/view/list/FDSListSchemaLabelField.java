/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.view.list;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;

import java.util.Map;

/**
 * @author Mylena Monte
 */
public class FDSListSchemaLabelField {

	public String getDisplayType() {
		return _displayType;
	}

	public String getDisplayTypeKey() {
		return _displayTypeKey;
	}

	public Map<String, String> getDisplayTypeValues() {
		return _displayTypeValues;
	}

	public String getValue() {
		return _value;
	}

	public FDSListSchemaLabelField setDisplayType(String displayType) {
		_displayType = displayType;

		return this;
	}

	public FDSListSchemaLabelField setDisplayTypeKey(String displayTypeKey) {
		_displayTypeKey = displayTypeKey;

		return this;
	}

	public FDSListSchemaLabelField setDisplayTypeValues(
		Map<String, String> displayTypeValues) {

		_displayTypeValues = displayTypeValues;

		return this;
	}

	public FDSListSchemaLabelField setValue(String value) {
		_value = value;

		return this;
	}

	public JSONObject toJSONObject() {
		return JSONUtil.put(
			"displayType", getDisplayType()
		).put(
			"displayTypeKey", getDisplayTypeKey()
		).put(
			"displayTypeValues", getDisplayTypeValues()
		).put(
			"value", getValue()
		);
	}

	private String _displayType;
	private String _displayTypeKey;
	private Map<String, String> _displayTypeValues;
	private String _value;

}