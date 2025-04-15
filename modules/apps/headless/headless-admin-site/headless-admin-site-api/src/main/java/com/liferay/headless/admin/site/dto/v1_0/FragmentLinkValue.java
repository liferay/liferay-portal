/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

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
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A fragment link value.", value = "FragmentLinkValue"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FragmentLinkValue")
public class FragmentLinkValue implements Serializable {

	public static FragmentLinkValue toDTO(String json) {
		return ObjectMapperUtil.readValue(FragmentLinkValue.class, json);
	}

	public static FragmentLinkValue unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FragmentLinkValue.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment link value's hypertext reference. Can be an inline value or mapped to an external value."
	)
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

	@GraphQLField(
		description = "The fragment link value's hypertext reference. Can be an inline value or mapped to an external value."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object href;

	@JsonIgnore
	private Supplier<Object> _hrefSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment link value's target (blank, parent, self, top)."
	)
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

	@GraphQLField(
		description = "The fragment link value's target (blank, parent, self, top)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Target target;

	@JsonIgnore
	private Supplier<Target> _targetSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentLinkValue)) {
			return false;
		}

		FragmentLinkValue fragmentLinkValue = (FragmentLinkValue)object;

		return Objects.equals(toString(), fragmentLinkValue.toString());
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

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.FragmentLinkValue",
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