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

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "The definition of the row viewport.",
	value = "RowViewportDefinition"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "RowViewportDefinition")
public class RowViewportDefinition implements Serializable {

	public static RowViewportDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(RowViewportDefinition.class, json);
	}

	public static RowViewportDefinition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			RowViewportDefinition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of modules per row."
	)
	public Integer getModulesPerRow() {
		if (_modulesPerRowSupplier != null) {
			modulesPerRow = _modulesPerRowSupplier.get();

			_modulesPerRowSupplier = null;
		}

		return modulesPerRow;
	}

	public void setModulesPerRow(Integer modulesPerRow) {
		this.modulesPerRow = modulesPerRow;

		_modulesPerRowSupplier = null;
	}

	@JsonIgnore
	public void setModulesPerRow(
		UnsafeSupplier<Integer, Exception> modulesPerRowUnsafeSupplier) {

		_modulesPerRowSupplier = () -> {
			try {
				return modulesPerRowUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The number of modules per row.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer modulesPerRow;

	@JsonIgnore
	private Supplier<Integer> _modulesPerRowSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the row viewport has reverse order."
	)
	public Boolean getReverseOrder() {
		if (_reverseOrderSupplier != null) {
			reverseOrder = _reverseOrderSupplier.get();

			_reverseOrderSupplier = null;
		}

		return reverseOrder;
	}

	public void setReverseOrder(Boolean reverseOrder) {
		this.reverseOrder = reverseOrder;

		_reverseOrderSupplier = null;
	}

	@JsonIgnore
	public void setReverseOrder(
		UnsafeSupplier<Boolean, Exception> reverseOrderUnsafeSupplier) {

		_reverseOrderSupplier = () -> {
			try {
				return reverseOrderUnsafeSupplier.get();
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
		description = "A flag that indicates whether the row viewport has reverse order."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean reverseOrder;

	@JsonIgnore
	private Supplier<Boolean> _reverseOrderSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The vertical alignment property of the row viewport."
	)
	public String getVerticalAlignment() {
		if (_verticalAlignmentSupplier != null) {
			verticalAlignment = _verticalAlignmentSupplier.get();

			_verticalAlignmentSupplier = null;
		}

		return verticalAlignment;
	}

	public void setVerticalAlignment(String verticalAlignment) {
		this.verticalAlignment = verticalAlignment;

		_verticalAlignmentSupplier = null;
	}

	@JsonIgnore
	public void setVerticalAlignment(
		UnsafeSupplier<String, Exception> verticalAlignmentUnsafeSupplier) {

		_verticalAlignmentSupplier = () -> {
			try {
				return verticalAlignmentUnsafeSupplier.get();
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
		description = "The vertical alignment property of the row viewport."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String verticalAlignment;

	@JsonIgnore
	private Supplier<String> _verticalAlignmentSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RowViewportDefinition)) {
			return false;
		}

		RowViewportDefinition rowViewportDefinition =
			(RowViewportDefinition)object;

		return Objects.equals(toString(), rowViewportDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Integer modulesPerRow = getModulesPerRow();

		if (modulesPerRow != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modulesPerRow\": ");

			sb.append(modulesPerRow);
		}

		Boolean reverseOrder = getReverseOrder();

		if (reverseOrder != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reverseOrder\": ");

			sb.append(reverseOrder);
		}

		String verticalAlignment = getVerticalAlignment();

		if (verticalAlignment != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"verticalAlignment\": ");

			sb.append("\"");

			sb.append(_escape(verticalAlignment));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.RowViewportDefinition",
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