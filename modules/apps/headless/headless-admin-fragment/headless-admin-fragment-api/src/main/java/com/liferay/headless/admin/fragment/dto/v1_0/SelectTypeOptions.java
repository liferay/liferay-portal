/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.dto.v1_0;

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
	description = "The type-specific options of a `select` fragment configuration field.",
	value = "SelectTypeOptions"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "The type-specific options of a `select` fragment configuration field."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SelectTypeOptions")
public class SelectTypeOptions implements Serializable {

	public static SelectTypeOptions toDTO(String json) {
		return ObjectMapperUtil.readValue(SelectTypeOptions.class, json);
	}

	public static SelectTypeOptions unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SelectTypeOptions.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The field's visibility conditions, keyed by the name of the field each condition applies to."
	)
	@Valid
	public Map<String, Dependency> getDependency() {
		if (_dependencySupplier != null) {
			dependency = _dependencySupplier.get();

			_dependencySupplier = null;
		}

		return dependency;
	}

	public void setDependency(Map<String, Dependency> dependency) {
		this.dependency = dependency;

		_dependencySupplier = null;
	}

	@JsonIgnore
	public void setDependency(
		UnsafeSupplier<Map<String, Dependency>, Exception>
			dependencyUnsafeSupplier) {

		_dependencySupplier = () -> {
			try {
				return dependencyUnsafeSupplier.get();
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
		description = "The field's visibility conditions, keyed by the name of the field each condition applies to."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, Dependency> dependency;

	@JsonIgnore
	private Supplier<Map<String, Dependency>> _dependencySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The values the field can take."
	)
	@Valid
	public ValidValue[] getValidValues() {
		if (_validValuesSupplier != null) {
			validValues = _validValuesSupplier.get();

			_validValuesSupplier = null;
		}

		return validValues;
	}

	public void setValidValues(ValidValue[] validValues) {
		this.validValues = validValues;

		_validValuesSupplier = null;
	}

	@JsonIgnore
	public void setValidValues(
		UnsafeSupplier<ValidValue[], Exception> validValuesUnsafeSupplier) {

		_validValuesSupplier = () -> {
			try {
				return validValuesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The values the field can take.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ValidValue[] validValues;

	@JsonIgnore
	private Supplier<ValidValue[]> _validValuesSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SelectTypeOptions)) {
			return false;
		}

		SelectTypeOptions selectTypeOptions = (SelectTypeOptions)object;

		return Objects.equals(toString(), selectTypeOptions.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Map<String, Dependency> dependency = getDependency();

		if (dependency != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dependency\": ");

			sb.append(_toJSON(dependency));
		}

		ValidValue[] validValues = getValidValues();

		if (validValues != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"validValues\": ");

			sb.append("[");

			for (int i = 0; i < validValues.length; i++) {
				sb.append(String.valueOf(validValues[i]));

				if ((i + 1) < validValues.length) {
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
		defaultValue = "com.liferay.headless.admin.fragment.dto.v1_0.SelectTypeOptions",
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
// LIFERAY-REST-BUILDER-HASH:-675300029