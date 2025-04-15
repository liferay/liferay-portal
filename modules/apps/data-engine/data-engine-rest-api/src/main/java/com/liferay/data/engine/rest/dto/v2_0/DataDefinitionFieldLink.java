/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.dto.v2_0;

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
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
@GraphQLName("DataDefinitionFieldLink")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DataDefinitionFieldLink")
public class DataDefinitionFieldLink implements Serializable {

	public static DataDefinitionFieldLink toDTO(String json) {
		return ObjectMapperUtil.readValue(DataDefinitionFieldLink.class, json);
	}

	public static DataDefinitionFieldLink unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			DataDefinitionFieldLink.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public DataDefinition getDataDefinition() {
		if (_dataDefinitionSupplier != null) {
			dataDefinition = _dataDefinitionSupplier.get();

			_dataDefinitionSupplier = null;
		}

		return dataDefinition;
	}

	public void setDataDefinition(DataDefinition dataDefinition) {
		this.dataDefinition = dataDefinition;

		_dataDefinitionSupplier = null;
	}

	@JsonIgnore
	public void setDataDefinition(
		UnsafeSupplier<DataDefinition, Exception>
			dataDefinitionUnsafeSupplier) {

		_dataDefinitionSupplier = () -> {
			try {
				return dataDefinitionUnsafeSupplier.get();
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
	protected DataDefinition dataDefinition;

	@JsonIgnore
	private Supplier<DataDefinition> _dataDefinitionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public DataLayout[] getDataLayouts() {
		if (_dataLayoutsSupplier != null) {
			dataLayouts = _dataLayoutsSupplier.get();

			_dataLayoutsSupplier = null;
		}

		return dataLayouts;
	}

	public void setDataLayouts(DataLayout[] dataLayouts) {
		this.dataLayouts = dataLayouts;

		_dataLayoutsSupplier = null;
	}

	@JsonIgnore
	public void setDataLayouts(
		UnsafeSupplier<DataLayout[], Exception> dataLayoutsUnsafeSupplier) {

		_dataLayoutsSupplier = () -> {
			try {
				return dataLayoutsUnsafeSupplier.get();
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
	protected DataLayout[] dataLayouts;

	@JsonIgnore
	private Supplier<DataLayout[]> _dataLayoutsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public DataListView[] getDataListViews() {
		if (_dataListViewsSupplier != null) {
			dataListViews = _dataListViewsSupplier.get();

			_dataListViewsSupplier = null;
		}

		return dataListViews;
	}

	public void setDataListViews(DataListView[] dataListViews) {
		this.dataListViews = dataListViews;

		_dataListViewsSupplier = null;
	}

	@JsonIgnore
	public void setDataListViews(
		UnsafeSupplier<DataListView[], Exception> dataListViewsUnsafeSupplier) {

		_dataListViewsSupplier = () -> {
			try {
				return dataListViewsUnsafeSupplier.get();
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
	protected DataListView[] dataListViews;

	@JsonIgnore
	private Supplier<DataListView[]> _dataListViewsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DataDefinitionFieldLink)) {
			return false;
		}

		DataDefinitionFieldLink dataDefinitionFieldLink =
			(DataDefinitionFieldLink)object;

		return Objects.equals(toString(), dataDefinitionFieldLink.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DataDefinition dataDefinition = getDataDefinition();

		if (dataDefinition != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataDefinition\": ");

			sb.append(String.valueOf(dataDefinition));
		}

		DataLayout[] dataLayouts = getDataLayouts();

		if (dataLayouts != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataLayouts\": ");

			sb.append("[");

			for (int i = 0; i < dataLayouts.length; i++) {
				sb.append(String.valueOf(dataLayouts[i]));

				if ((i + 1) < dataLayouts.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		DataListView[] dataListViews = getDataListViews();

		if (dataListViews != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataListViews\": ");

			sb.append("[");

			for (int i = 0; i < dataListViews.length; i++) {
				sb.append(String.valueOf(dataListViews[i]));

				if ((i + 1) < dataListViews.length) {
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
		defaultValue = "com.liferay.data.engine.rest.dto.v2_0.DataDefinitionFieldLink",
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