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
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a definition of a Page Column.",
	value = "PageColumnDefinition"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PageColumnDefinition")
public class PageColumnDefinition implements Serializable {

	public static PageColumnDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(PageColumnDefinition.class, json);
	}

	public static PageColumnDefinition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PageColumnDefinition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		deprecated = true,
		description = "Deprecated as of Athanasius (7.3.x), replaced by columnViewports"
	)
	@Valid
	public ColumnViewportConfig getColumnViewportConfig() {
		if (_columnViewportConfigSupplier != null) {
			columnViewportConfig = _columnViewportConfigSupplier.get();

			_columnViewportConfigSupplier = null;
		}

		return columnViewportConfig;
	}

	public void setColumnViewportConfig(
		ColumnViewportConfig columnViewportConfig) {

		this.columnViewportConfig = columnViewportConfig;

		_columnViewportConfigSupplier = null;
	}

	@JsonIgnore
	public void setColumnViewportConfig(
		UnsafeSupplier<ColumnViewportConfig, Exception>
			columnViewportConfigUnsafeSupplier) {

		_columnViewportConfigSupplier = () -> {
			try {
				return columnViewportConfigUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@Deprecated
	@GraphQLField(
		description = "Deprecated as of Athanasius (7.3.x), replaced by columnViewports"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ColumnViewportConfig columnViewportConfig;

	@JsonIgnore
	private Supplier<ColumnViewportConfig> _columnViewportConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of column viewports of the page column definition."
	)
	@Valid
	public ColumnViewport[] getColumnViewports() {
		if (_columnViewportsSupplier != null) {
			columnViewports = _columnViewportsSupplier.get();

			_columnViewportsSupplier = null;
		}

		return columnViewports;
	}

	public void setColumnViewports(ColumnViewport[] columnViewports) {
		this.columnViewports = columnViewports;

		_columnViewportsSupplier = null;
	}

	@JsonIgnore
	public void setColumnViewports(
		UnsafeSupplier<ColumnViewport[], Exception>
			columnViewportsUnsafeSupplier) {

		_columnViewportsSupplier = () -> {
			try {
				return columnViewportsUnsafeSupplier.get();
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
		description = "A list of column viewports of the page column definition."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ColumnViewport[] columnViewports;

	@JsonIgnore
	private Supplier<ColumnViewport[]> _columnViewportsSupplier;

	@DecimalMax("12")
	@DecimalMin("1")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page column's size."
	)
	public Integer getSize() {
		if (_sizeSupplier != null) {
			size = _sizeSupplier.get();

			_sizeSupplier = null;
		}

		return size;
	}

	public void setSize(Integer size) {
		this.size = size;

		_sizeSupplier = null;
	}

	@JsonIgnore
	public void setSize(UnsafeSupplier<Integer, Exception> sizeUnsafeSupplier) {
		_sizeSupplier = () -> {
			try {
				return sizeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page column's size.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer size;

	@JsonIgnore
	private Supplier<Integer> _sizeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageColumnDefinition)) {
			return false;
		}

		PageColumnDefinition pageColumnDefinition =
			(PageColumnDefinition)object;

		return Objects.equals(toString(), pageColumnDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		ColumnViewportConfig columnViewportConfig = getColumnViewportConfig();

		if (columnViewportConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"columnViewportConfig\": ");

			sb.append(String.valueOf(columnViewportConfig));
		}

		ColumnViewport[] columnViewports = getColumnViewports();

		if (columnViewports != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"columnViewports\": ");

			sb.append("[");

			for (int i = 0; i < columnViewports.length; i++) {
				sb.append(String.valueOf(columnViewports[i]));

				if ((i + 1) < columnViewports.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Integer size = getSize();

		if (size != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"size\": ");

			sb.append(size);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.PageColumnDefinition",
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