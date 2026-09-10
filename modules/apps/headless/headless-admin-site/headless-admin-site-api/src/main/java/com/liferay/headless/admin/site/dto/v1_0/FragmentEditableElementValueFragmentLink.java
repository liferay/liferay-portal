/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
	description = "A fragment editable element value fragment link.",
	value = "FragmentEditableElementValueFragmentLink"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A fragment editable element value fragment link."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FragmentEditableElementValueFragmentLink")
public class FragmentEditableElementValueFragmentLink implements Serializable {

	public static FragmentEditableElementValueFragmentLink toDTO(String json) {
		return ObjectMapperUtil.readValue(
			FragmentEditableElementValueFragmentLink.class, json);
	}

	public static FragmentEditableElementValueFragmentLink unsafeToDTO(
		String json) {

		return ObjectMapperUtil.unsafeReadValue(
			FragmentEditableElementValueFragmentLink.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment link of a fragment editable element value."
	)
	@Valid
	public FragmentLink getFragmentLink() {
		if (_fragmentLinkSupplier != null) {
			fragmentLink = _fragmentLinkSupplier.get();

			_fragmentLinkSupplier = null;
		}

		return fragmentLink;
	}

	public void setFragmentLink(FragmentLink fragmentLink) {
		this.fragmentLink = fragmentLink;

		_fragmentLinkSupplier = null;
	}

	@JsonIgnore
	public void setFragmentLink(
		UnsafeSupplier<FragmentLink, Exception> fragmentLinkUnsafeSupplier) {

		_fragmentLinkSupplier = () -> {
			try {
				return fragmentLinkUnsafeSupplier.get();
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
	protected FragmentLink fragmentLink;

	@JsonIgnore
	private Supplier<FragmentLink> _fragmentLinkSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The fragment link prefix of a fragment editable element value."
	)
	@JsonGetter("prefix")
	@Valid
	public Prefix getPrefix() {
		if (_prefixSupplier != null) {
			prefix = _prefixSupplier.get();

			_prefixSupplier = null;
		}

		return prefix;
	}

	@JsonIgnore
	public String getPrefixAsString() {
		Prefix prefix = getPrefix();

		if (prefix == null) {
			return null;
		}

		return prefix.toString();
	}

	public void setPrefix(Prefix prefix) {
		this.prefix = prefix;

		_prefixSupplier = null;
	}

	@JsonIgnore
	public void setPrefix(
		UnsafeSupplier<Prefix, Exception> prefixUnsafeSupplier) {

		_prefixSupplier = () -> {
			try {
				return prefixUnsafeSupplier.get();
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
		description = "The fragment link prefix of a fragment editable element value."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Prefix prefix;

	@JsonIgnore
	private Supplier<Prefix> _prefixSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentEditableElementValueFragmentLink)) {
			return false;
		}

		FragmentEditableElementValueFragmentLink
			fragmentEditableElementValueFragmentLink =
				(FragmentEditableElementValueFragmentLink)object;

		return Objects.equals(
			toString(), fragmentEditableElementValueFragmentLink.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		FragmentLink fragmentLink = getFragmentLink();

		if (fragmentLink != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentLink\": ");

			sb.append(String.valueOf(fragmentLink));
		}

		Prefix prefix = getPrefix();

		if (prefix != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"prefix\": ");

			sb.append("\"");
			sb.append(prefix);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.FragmentEditableElementValueFragmentLink",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Prefix")
	public static enum Prefix {

		EMAIL("Email"), NONE("None"), PHONE("Phone");

		@JsonCreator
		public static Prefix create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Prefix prefix : values()) {
				if (Objects.equals(prefix.getValue(), value)) {
					return prefix;
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

		private Prefix(String value) {
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
// LIFERAY-REST-BUILDER-HASH:-578005452