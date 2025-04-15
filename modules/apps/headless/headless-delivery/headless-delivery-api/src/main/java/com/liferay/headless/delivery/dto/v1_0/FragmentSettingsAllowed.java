/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents the settings of allowed fragments in a page dropzone.",
	value = "FragmentSettingsAllowed"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FragmentSettingsAllowed")
public class FragmentSettingsAllowed implements Serializable {

	public static FragmentSettingsAllowed toDTO(String json) {
		return ObjectMapperUtil.readValue(FragmentSettingsAllowed.class, json);
	}

	public static FragmentSettingsAllowed unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			FragmentSettingsAllowed.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of allowed fragments."
	)
	@Valid
	public Fragment[] getAllowedFragments() {
		if (_allowedFragmentsSupplier != null) {
			allowedFragments = _allowedFragmentsSupplier.get();

			_allowedFragmentsSupplier = null;
		}

		return allowedFragments;
	}

	public void setAllowedFragments(Fragment[] allowedFragments) {
		this.allowedFragments = allowedFragments;

		_allowedFragmentsSupplier = null;
	}

	@JsonIgnore
	public void setAllowedFragments(
		UnsafeSupplier<Fragment[], Exception> allowedFragmentsUnsafeSupplier) {

		_allowedFragmentsSupplier = () -> {
			try {
				return allowedFragmentsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of allowed fragments.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Fragment[] allowedFragments;

	@JsonIgnore
	private Supplier<Fragment[]> _allowedFragmentsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentSettingsAllowed)) {
			return false;
		}

		FragmentSettingsAllowed fragmentSettingsAllowed =
			(FragmentSettingsAllowed)object;

		return Objects.equals(toString(), fragmentSettingsAllowed.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Fragment[] allowedFragments = getAllowedFragments();

		if (allowedFragments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allowedFragments\": ");

			sb.append("[");

			for (int i = 0; i < allowedFragments.length; i++) {
				sb.append(String.valueOf(allowedFragments[i]));

				if ((i + 1) < allowedFragments.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.FragmentSettingsAllowed",
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

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}