/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
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
	description = "Represents the settings of unallowed fragments in a page drop zone.",
	value = "FragmentSettingsUnallowed"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FragmentSettingsUnallowed")
public class FragmentSettingsUnallowed implements Serializable {

	public static FragmentSettingsUnallowed toDTO(String json) {
		return ObjectMapperUtil.readValue(
			FragmentSettingsUnallowed.class, json);
	}

	public static FragmentSettingsUnallowed unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			FragmentSettingsUnallowed.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public ItemExternalReference[]
		getUnallowedFragmentItemExternalReferences() {

		if (_unallowedFragmentItemExternalReferencesSupplier != null) {
			unallowedFragmentItemExternalReferences =
				_unallowedFragmentItemExternalReferencesSupplier.get();

			_unallowedFragmentItemExternalReferencesSupplier = null;
		}

		return unallowedFragmentItemExternalReferences;
	}

	public void setUnallowedFragmentItemExternalReferences(
		ItemExternalReference[] unallowedFragmentItemExternalReferences) {

		this.unallowedFragmentItemExternalReferences =
			unallowedFragmentItemExternalReferences;

		_unallowedFragmentItemExternalReferencesSupplier = null;
	}

	@JsonIgnore
	public void setUnallowedFragmentItemExternalReferences(
		UnsafeSupplier<ItemExternalReference[], Exception>
			unallowedFragmentItemExternalReferencesUnsafeSupplier) {

		_unallowedFragmentItemExternalReferencesSupplier = () -> {
			try {
				return unallowedFragmentItemExternalReferencesUnsafeSupplier.
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ItemExternalReference[] unallowedFragmentItemExternalReferences;

	@JsonIgnore
	private Supplier<ItemExternalReference[]>
		_unallowedFragmentItemExternalReferencesSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentSettingsUnallowed)) {
			return false;
		}

		FragmentSettingsUnallowed fragmentSettingsUnallowed =
			(FragmentSettingsUnallowed)object;

		return Objects.equals(toString(), fragmentSettingsUnallowed.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		ItemExternalReference[] unallowedFragmentItemExternalReferences =
			getUnallowedFragmentItemExternalReferences();

		if (unallowedFragmentItemExternalReferences != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unallowedFragmentItemExternalReferences\": ");

			sb.append("[");

			for (int i = 0; i < unallowedFragmentItemExternalReferences.length;
				 i++) {

				sb.append(
					String.valueOf(unallowedFragmentItemExternalReferences[i]));

				if ((i + 1) < unallowedFragmentItemExternalReferences.length) {
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
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.FragmentSettingsUnallowed",
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