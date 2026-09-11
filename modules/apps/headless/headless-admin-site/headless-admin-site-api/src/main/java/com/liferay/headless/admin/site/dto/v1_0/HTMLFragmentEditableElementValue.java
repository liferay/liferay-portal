/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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

import java.util.Arrays;
import java.util.Collection;
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
	description = "A fragment editable element value of type HTML.",
	value = "HTMLFragmentEditableElementValue"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A fragment editable element value of type HTML."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "HTMLFragmentEditableElementValue")
public class HTMLFragmentEditableElementValue
	extends FragmentEditableElementValue implements Serializable {

	public static HTMLFragmentEditableElementValue toDTO(String json) {
		return ObjectMapperUtil.readValue(
			HTMLFragmentEditableElementValue.class, json);
	}

	public static HTMLFragmentEditableElementValue unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			HTMLFragmentEditableElementValue.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment editable element's HTML. Can be inline or mapped to an external value."
	)
	@Valid
	public HTMLFragmentValue getHtmlFragmentValue() {
		if (_htmlFragmentValueSupplier != null) {
			htmlFragmentValue = _htmlFragmentValueSupplier.get();

			_htmlFragmentValueSupplier = null;
		}

		return htmlFragmentValue;
	}

	public void setHtmlFragmentValue(HTMLFragmentValue htmlFragmentValue) {
		this.htmlFragmentValue = htmlFragmentValue;

		_htmlFragmentValueSupplier = null;
	}

	@JsonIgnore
	public void setHtmlFragmentValue(
		UnsafeSupplier<HTMLFragmentValue, Exception>
			htmlFragmentValueUnsafeSupplier) {

		_htmlFragmentValueSupplier = () -> {
			try {
				return htmlFragmentValueUnsafeSupplier.get();
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
		description = "The fragment editable element's HTML. Can be inline or mapped to an external value."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected HTMLFragmentValue htmlFragmentValue;

	@JsonIgnore
	private Supplier<HTMLFragmentValue> _htmlFragmentValueSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof HTMLFragmentEditableElementValue)) {
			return false;
		}

		HTMLFragmentEditableElementValue htmlFragmentEditableElementValue =
			(HTMLFragmentEditableElementValue)object;

		return Objects.equals(
			toString(), htmlFragmentEditableElementValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		HTMLFragmentValue htmlFragmentValue = getHtmlFragmentValue();

		if (htmlFragmentValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"htmlFragmentValue\": ");

			sb.append(String.valueOf(htmlFragmentValue));
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(type);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.HTMLFragmentEditableElementValue",
		name = "x-class-name"
	)
	public String xClassName;

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

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-226702225