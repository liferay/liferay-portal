/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a fragment link.", value = "FragmentLink"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FragmentLink")
public class FragmentLink implements Serializable {

	public static FragmentLink toDTO(String json) {
		return ObjectMapperUtil.readValue(FragmentLink.class, json);
	}

	public static FragmentLink unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FragmentLink.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	@Valid
	public Object getHref() {
		if (_hrefSupplier != null) {
			href = _hrefSupplier.get();

			_hrefSupplier = null;
		}

		return href;
	}

	public void setHref(Object href) {
		this.href = href;

		_hrefSupplier = null;
	}

	@JsonIgnore
	public void setHref(UnsafeSupplier<Object, Exception> hrefUnsafeSupplier) {
		_hrefSupplier = () -> {
			try {
				return hrefUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object href;

	@JsonIgnore
	private Supplier<Object> _hrefSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(deprecated = true)
	@JsonGetter("target")
	@Valid
	public Target getTarget() {
		if (_targetSupplier != null) {
			target = _targetSupplier.get();

			_targetSupplier = null;
		}

		return target;
	}

	@JsonIgnore
	public String getTargetAsString() {
		Target target = getTarget();

		if (target == null) {
			return null;
		}

		return target.toString();
	}

	public void setTarget(Target target) {
		this.target = target;

		_targetSupplier = null;
	}

	@JsonIgnore
	public void setTarget(
		UnsafeSupplier<Target, Exception> targetUnsafeSupplier) {

		_targetSupplier = () -> {
			try {
				return targetUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Target target;

	@JsonIgnore
	private Supplier<Target> _targetSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment link's value."
	)
	@Valid
	public FragmentLinkValue getValue() {
		if (_valueSupplier != null) {
			value = _valueSupplier.get();

			_valueSupplier = null;
		}

		return value;
	}

	public void setValue(FragmentLinkValue value) {
		this.value = value;

		_valueSupplier = null;
	}

	@JsonIgnore
	public void setValue(
		UnsafeSupplier<FragmentLinkValue, Exception> valueUnsafeSupplier) {

		_valueSupplier = () -> {
			try {
				return valueUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment link's value.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentLinkValue value;

	@JsonIgnore
	private Supplier<FragmentLinkValue> _valueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized fragment link's values."
	)
	@Valid
	public Map<String, FragmentLinkValue> getValue_i18n() {
		if (_value_i18nSupplier != null) {
			value_i18n = _value_i18nSupplier.get();

			_value_i18nSupplier = null;
		}

		return value_i18n;
	}

	public void setValue_i18n(Map<String, FragmentLinkValue> value_i18n) {
		this.value_i18n = value_i18n;

		_value_i18nSupplier = null;
	}

	@JsonIgnore
	public void setValue_i18n(
		UnsafeSupplier<Map<String, FragmentLinkValue>, Exception>
			value_i18nUnsafeSupplier) {

		_value_i18nSupplier = () -> {
			try {
				return value_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized fragment link's values.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, FragmentLinkValue> value_i18n;

	@JsonIgnore
	private Supplier<Map<String, FragmentLinkValue>> _value_i18nSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentLink)) {
			return false;
		}

		FragmentLink fragmentLink = (FragmentLink)object;

		return Objects.equals(toString(), fragmentLink.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object href = getHref();

		if (href != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"href\": ");

			if (href instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)href));
			}
			else if (href instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)href));
				sb.append("\"");
			}
			else {
				sb.append(href);
			}
		}

		Target target = getTarget();

		if (target != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"target\": ");

			sb.append("\"");

			sb.append(target);

			sb.append("\"");
		}

		FragmentLinkValue value = getValue();

		if (value != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append(String.valueOf(value));
		}

		Map<String, FragmentLinkValue> value_i18n = getValue_i18n();

		if (value_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value_i18n\": ");

			sb.append(_toJSON(value_i18n));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.FragmentLink",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Target")
	public static enum Target {

		BLANK("Blank"), PARENT("Parent"), SELF("Self"), TOP("Top");

		@JsonCreator
		public static Target create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Target target : values()) {
				if (Objects.equals(target.getValue(), value)) {
					return target;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private Target(String value) {
			_value = value;
		}

		private final String _value;

	}

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}