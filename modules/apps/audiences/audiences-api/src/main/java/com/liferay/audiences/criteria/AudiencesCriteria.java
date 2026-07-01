/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.criteria;

import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class AudiencesCriteria {

	public String getIcon() {
		return _icon;
	}

	public InputType getInputType() {
		return _inputType;
	}

	public String getKey() {
		return _key;
	}

	public String getLabel() {
		return _label;
	}

	public List<Option> getOptions() {
		return _options;
	}

	public Type getType() {
		return _type;
	}

	public void setIcon(String icon) {
		_icon = icon;
	}

	public void setInputType(InputType inputType) {
		_inputType = inputType;
	}

	public void setKey(String key) {
		_key = key;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public void setOptions(List<Option> options) {
		_options = options;
	}

	public void setType(Type type) {
		_type = type;
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

		private final String _label;
		private final String _value;

	}

	public enum InputType {

		BOOLEAN("boolean"), DATE("date"), SELECT("select"), TEXT("text");

		public String getValue() {
			return _value;
		}

		private InputType(String value) {
			_value = value;
		}

		private final String _value;

	}

	public enum Type {

		BOOLEAN("boolean"), NUMBER("number"), SET("set"), STRING("string");

		public static Type parse(String value) {
			return valueOf(StringUtil.toUpperCase(value));
		}

		public String getValue() {
			return _value;
		}

		private Type(String value) {
			_value = value;
		}

		private final String _value;

	}

	private String _icon;
	private InputType _inputType;
	private String _key;
	private String _label;
	private List<Option> _options;
	private Type _type;

}