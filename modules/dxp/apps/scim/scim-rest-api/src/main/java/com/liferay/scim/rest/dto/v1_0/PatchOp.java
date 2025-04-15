/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.dto.v1_0;

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
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
@GraphQLName("PatchOp")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PatchOp")
public class PatchOp implements Serializable {

	public static PatchOp toDTO(String json) {
		return ObjectMapperUtil.readValue(PatchOp.class, json);
	}

	public static PatchOp unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PatchOp.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Operation[] getOperations() {
		if (_OperationsSupplier != null) {
			Operations = _OperationsSupplier.get();

			_OperationsSupplier = null;
		}

		return Operations;
	}

	public void setOperations(Operation[] Operations) {
		this.Operations = Operations;

		_OperationsSupplier = null;
	}

	@JsonIgnore
	public void setOperations(
		UnsafeSupplier<Operation[], Exception> OperationsUnsafeSupplier) {

		_OperationsSupplier = () -> {
			try {
				return OperationsUnsafeSupplier.get();
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
	protected Operation[] Operations;

	@JsonIgnore
	private Supplier<Operation[]> _OperationsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The schema associated to this operation."
	)
	public String[] getSchemas() {
		if (_schemasSupplier != null) {
			schemas = _schemasSupplier.get();

			_schemasSupplier = null;
		}

		return schemas;
	}

	public void setSchemas(String[] schemas) {
		this.schemas = schemas;

		_schemasSupplier = null;
	}

	@JsonIgnore
	public void setSchemas(
		UnsafeSupplier<String[], Exception> schemasUnsafeSupplier) {

		_schemasSupplier = () -> {
			try {
				return schemasUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The schema associated to this operation.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String[] schemas;

	@JsonIgnore
	private Supplier<String[]> _schemasSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PatchOp)) {
			return false;
		}

		PatchOp patchOp = (PatchOp)object;

		return Objects.equals(toString(), patchOp.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Operation[] Operations = getOperations();

		if (Operations != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"Operations\": ");

			sb.append("[");

			for (int i = 0; i < Operations.length; i++) {
				sb.append(String.valueOf(Operations[i]));

				if ((i + 1) < Operations.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String[] schemas = getSchemas();

		if (schemas != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemas\": ");

			sb.append("[");

			for (int i = 0; i < schemas.length; i++) {
				sb.append("\"");

				sb.append(_escape(schemas[i]));

				sb.append("\"");

				if ((i + 1) < schemas.length) {
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
		defaultValue = "com.liferay.scim.rest.dto.v1_0.PatchOp",
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