/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.input.template.parser;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.lang.reflect.Array;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class InputTemplateNode extends LinkedHashMap<String, Object> {

	public InputTemplateNode(
		String errorMessage, String helpText, String label, boolean localizable,
		String name, boolean readOnly, boolean required, boolean showHelpText,
		boolean showLabel, String type, String value,
		Map<Locale, String> valueI18n) {

		_errorMessage = errorMessage;
		_helpText = helpText;
		_label = label;
		_localizable = localizable;
		_name = name;
		_readOnly = readOnly;
		_required = required;
		_showHelpText = showHelpText;
		_showLabel = showLabel;
		_type = type;
		_value = value;
		_valueI18n = valueI18n;

		put("errorMessage", errorMessage);
		put("helpText", helpText);
		put("label", label);
		put("localizable", localizable);
		put("name", name);
		put("readOnly", readOnly);
		put("required", required);
		put("showHelpText", showHelpText);
		put("showLabel", showLabel);
		put("type", type);
		put("value", value);
		put("valueI18n", LocalizedMapUtil.getLanguageIdMap(valueI18n));
	}

	public void addAttribute(String name, Object object) {
		_attributes.put(name, object);
	}

	public Map<String, Object> getAttributes() {
		return _attributes;
	}

	public String getErrorMessage() {
		return _errorMessage;
	}

	public String getHelpText() {
		return _helpText;
	}

	public String getInputLabel() {
		return _label;
	}

	public String getInputName() {
		return _name;
	}

	public String getInputValue() {
		return _value;
	}

	public String getType() {
		return _type;
	}

	public Map<String, String> getValueI18n() {
		return LocalizedMapUtil.getLanguageIdMap(_valueI18n);
	}

	public boolean isLocalizable() {
		return _localizable;
	}

	public boolean isReadOnly() {
		return _readOnly;
	}

	public boolean isRequired() {
		return _required;
	}

	public boolean isShowHelpText() {
		return _showHelpText;
	}

	public boolean isShowLabel() {
		return _showLabel;
	}

	public JSONObject toJSONObject() {
		return JSONUtil.put(
			"attributes",
			() -> {
				JSONObject attributesJSONObject =
					JSONFactoryUtil.createJSONObject();

				for (Map.Entry<String, Object> entry : _attributes.entrySet()) {
					attributesJSONObject.put(
						entry.getKey(), _normalizeValue(entry.getValue()));
				}

				attributesJSONObject.put("readOnly", _readOnly);

				return attributesJSONObject;
			}
		).put(
			"errorMessage", _errorMessage
		).put(
			"helpText", HtmlUtil.escape(_helpText)
		).put(
			"label", HtmlUtil.escape(_label)
		).put(
			"localizable", _localizable
		).put(
			"name", _name
		).put(
			"readOnly", _readOnly
		).put(
			"required", _required
		).put(
			"showHelpText", _showHelpText
		).put(
			"showLabel", _showLabel
		).put(
			"type", _type
		).put(
			"value", _value
		).put(
			"valueI18n",
			JSONFactoryUtil.createJSONObject(
				LocalizedMapUtil.getLanguageIdMap(_valueI18n))
		);
	}

	public static class Option {

		public Option(String label, String value) {
			_label = label;
			_value = value;
		}

		public String getLabel() {
			return _label;
		}

		public String getValue() {
			return _value;
		}

		public JSONObject toJSONObject() {
			return JSONUtil.put(
				"label", HtmlUtil.escape(_label)
			).put(
				"value", _value
			);
		}

		@Override
		public String toString() {
			JSONObject jsonObject = toJSONObject();

			return jsonObject.toString();
		}

		private final String _label;
		private final String _value;

	}

	private Object _normalizeValue(Object value) {
		if ((value == null) || (value instanceof JSONArray) ||
			(value instanceof JSONObject)) {

			return value;
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

			int length = Array.getLength(value);

			for (int i = 0; i < length; i++) {
				jsonArray.put(_normalizeValue(Array.get(value, i)));
			}

			return jsonArray;
		}

		if (value instanceof Collection) {
			JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

			for (Object element : (Collection<?>)value) {
				jsonArray.put(_normalizeValue(element));
			}

			return jsonArray;
		}

		if (value instanceof Map) {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

			Map<String, Object> map = (Map<String, Object>)value;

			for (Map.Entry<String, Object> entry : map.entrySet()) {
				jsonObject.put(
					String.valueOf(entry.getKey()),
					_normalizeValue(entry.getValue()));
			}

			return jsonObject;
		}

		if (value instanceof Option) {
			Option option = (Option)value;

			return option.toJSONObject();
		}

		return value;
	}

	private final Map<String, Object> _attributes = new HashMap<>();
	private final String _errorMessage;
	private final String _helpText;
	private final String _label;
	private final boolean _localizable;
	private final String _name;
	private final boolean _readOnly;
	private final boolean _required;
	private final boolean _showHelpText;
	private final boolean _showLabel;
	private final String _type;
	private final String _value;
	private final Map<Locale, String> _valueI18n;

}