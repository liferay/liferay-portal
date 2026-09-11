/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
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
	description = "The link text value for an editable fragment field.",
	value = "FragmentLinkTextValue"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "The link text value for an editable fragment field."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FragmentLinkTextValue")
public class FragmentLinkTextValue implements Serializable {

	public static FragmentLinkTextValue toDTO(String json) {
		return ObjectMapperUtil.readValue(FragmentLinkTextValue.class, json);
	}

	public static FragmentLinkTextValue unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			FragmentLinkTextValue.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment link of a fragment editable element value."
	)
	@Valid
	public FragmentEditableElementValueFragmentLink
		getFragmentEditableElementValueFragmentLink() {

		if (_fragmentEditableElementValueFragmentLinkSupplier != null) {
			fragmentEditableElementValueFragmentLink =
				_fragmentEditableElementValueFragmentLinkSupplier.get();

			_fragmentEditableElementValueFragmentLinkSupplier = null;
		}

		return fragmentEditableElementValueFragmentLink;
	}

	public void setFragmentEditableElementValueFragmentLink(
		FragmentEditableElementValueFragmentLink
			fragmentEditableElementValueFragmentLink) {

		this.fragmentEditableElementValueFragmentLink =
			fragmentEditableElementValueFragmentLink;

		_fragmentEditableElementValueFragmentLinkSupplier = null;
	}

	@JsonIgnore
	public void setFragmentEditableElementValueFragmentLink(
		UnsafeSupplier<FragmentEditableElementValueFragmentLink, Exception>
			fragmentEditableElementValueFragmentLinkUnsafeSupplier) {

		_fragmentEditableElementValueFragmentLinkSupplier = () -> {
			try {
				return fragmentEditableElementValueFragmentLinkUnsafeSupplier.
					get();
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
		description = "The fragment link of a fragment editable element value."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentEditableElementValueFragmentLink
		fragmentEditableElementValueFragmentLink;

	@JsonIgnore
	private Supplier<FragmentEditableElementValueFragmentLink>
		_fragmentEditableElementValueFragmentLinkSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment editable element's text value."
	)
	@Valid
	public TextFragmentValue getTextFragmentValue() {
		if (_textFragmentValueSupplier != null) {
			textFragmentValue = _textFragmentValueSupplier.get();

			_textFragmentValueSupplier = null;
		}

		return textFragmentValue;
	}

	public void setTextFragmentValue(TextFragmentValue textFragmentValue) {
		this.textFragmentValue = textFragmentValue;

		_textFragmentValueSupplier = null;
	}

	@JsonIgnore
	public void setTextFragmentValue(
		UnsafeSupplier<TextFragmentValue, Exception>
			textFragmentValueUnsafeSupplier) {

		_textFragmentValueSupplier = () -> {
			try {
				return textFragmentValueUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The fragment editable element's text value.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected TextFragmentValue textFragmentValue;

	@JsonIgnore
	private Supplier<TextFragmentValue> _textFragmentValueSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentLinkTextValue)) {
			return false;
		}

		FragmentLinkTextValue fragmentLinkTextValue =
			(FragmentLinkTextValue)object;

		return Objects.equals(toString(), fragmentLinkTextValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		FragmentEditableElementValueFragmentLink
			fragmentEditableElementValueFragmentLink =
				getFragmentEditableElementValueFragmentLink();

		if (fragmentEditableElementValueFragmentLink != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentEditableElementValueFragmentLink\": ");

			sb.append(String.valueOf(fragmentEditableElementValueFragmentLink));
		}

		TextFragmentValue textFragmentValue = getTextFragmentValue();

		if (textFragmentValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"textFragmentValue\": ");

			sb.append(String.valueOf(textFragmentValue));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.FragmentLinkTextValue",
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
// LIFERAY-REST-BUILDER-HASH:-1931212144