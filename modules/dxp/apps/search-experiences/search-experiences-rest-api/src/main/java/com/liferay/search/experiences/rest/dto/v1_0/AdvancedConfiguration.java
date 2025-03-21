/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v1_0;

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
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("AdvancedConfiguration")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AdvancedConfiguration")
public class AdvancedConfiguration implements Serializable {

	public static AdvancedConfiguration toDTO(String json) {
		return ObjectMapperUtil.readValue(AdvancedConfiguration.class, json);
	}

	public static AdvancedConfiguration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			AdvancedConfiguration.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Collapse getCollapse() {
		if (_collapseSupplier != null) {
			collapse = _collapseSupplier.get();

			_collapseSupplier = null;
		}

		return collapse;
	}

	public void setCollapse(Collapse collapse) {
		this.collapse = collapse;

		_collapseSupplier = null;
	}

	@JsonIgnore
	public void setCollapse(
		UnsafeSupplier<Collapse, Exception> collapseUnsafeSupplier) {

		_collapseSupplier = () -> {
			try {
				return collapseUnsafeSupplier.get();
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
	protected Collapse collapse;

	@JsonIgnore
	private Supplier<Collapse> _collapseSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getFields() {
		if (_fieldsSupplier != null) {
			fields = _fieldsSupplier.get();

			_fieldsSupplier = null;
		}

		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;

		_fieldsSupplier = null;
	}

	@JsonIgnore
	public void setFields(
		UnsafeSupplier<String[], Exception> fieldsUnsafeSupplier) {

		_fieldsSupplier = () -> {
			try {
				return fieldsUnsafeSupplier.get();
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
	protected String[] fields;

	@JsonIgnore
	private Supplier<String[]> _fieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Source getSource() {
		if (_sourceSupplier != null) {
			source = _sourceSupplier.get();

			_sourceSupplier = null;
		}

		return source;
	}

	public void setSource(Source source) {
		this.source = source;

		_sourceSupplier = null;
	}

	@JsonIgnore
	public void setSource(
		UnsafeSupplier<Source, Exception> sourceUnsafeSupplier) {

		_sourceSupplier = () -> {
			try {
				return sourceUnsafeSupplier.get();
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
	protected Source source;

	@JsonIgnore
	private Supplier<Source> _sourceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getStored_fields() {
		if (_stored_fieldsSupplier != null) {
			stored_fields = _stored_fieldsSupplier.get();

			_stored_fieldsSupplier = null;
		}

		return stored_fields;
	}

	public void setStored_fields(String[] stored_fields) {
		this.stored_fields = stored_fields;

		_stored_fieldsSupplier = null;
	}

	@JsonIgnore
	public void setStored_fields(
		UnsafeSupplier<String[], Exception> stored_fieldsUnsafeSupplier) {

		_stored_fieldsSupplier = () -> {
			try {
				return stored_fieldsUnsafeSupplier.get();
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
	protected String[] stored_fields;

	@JsonIgnore
	private Supplier<String[]> _stored_fieldsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AdvancedConfiguration)) {
			return false;
		}

		AdvancedConfiguration advancedConfiguration =
			(AdvancedConfiguration)object;

		return Objects.equals(toString(), advancedConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Collapse collapse = getCollapse();

		if (collapse != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collapse\": ");

			sb.append(String.valueOf(collapse));
		}

		String[] fields = getFields();

		if (fields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fields\": ");

			sb.append("[");

			for (int i = 0; i < fields.length; i++) {
				sb.append("\"");

				sb.append(_escape(fields[i]));

				sb.append("\"");

				if ((i + 1) < fields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Source source = getSource();

		if (source != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"source\": ");

			sb.append(String.valueOf(source));
		}

		String[] stored_fields = getStored_fields();

		if (stored_fields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"stored_fields\": ");

			sb.append("[");

			for (int i = 0; i < stored_fields.length; i++) {
				sb.append("\"");

				sb.append(_escape(stored_fields[i]));

				sb.append("\"");

				if ((i + 1) < stored_fields.length) {
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
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.AdvancedConfiguration",
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