/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.dto.v1_0;

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
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
@GraphQLName("InventoryAnalysis")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "InventoryAnalysis")
public class InventoryAnalysis implements Serializable {

	public static InventoryAnalysis toDTO(String json) {
		return ObjectMapperUtil.readValue(InventoryAnalysis.class, json);
	}

	public static InventoryAnalysis unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(InventoryAnalysis.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public InventoryAnalysisItem[] getInventoryAnalysisItems() {
		if (_inventoryAnalysisItemsSupplier != null) {
			inventoryAnalysisItems = _inventoryAnalysisItemsSupplier.get();

			_inventoryAnalysisItemsSupplier = null;
		}

		return inventoryAnalysisItems;
	}

	public void setInventoryAnalysisItems(
		InventoryAnalysisItem[] inventoryAnalysisItems) {

		this.inventoryAnalysisItems = inventoryAnalysisItems;

		_inventoryAnalysisItemsSupplier = null;
	}

	@JsonIgnore
	public void setInventoryAnalysisItems(
		UnsafeSupplier<InventoryAnalysisItem[], Exception>
			inventoryAnalysisItemsUnsafeSupplier) {

		_inventoryAnalysisItemsSupplier = () -> {
			try {
				return inventoryAnalysisItemsUnsafeSupplier.get();
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
	protected InventoryAnalysisItem[] inventoryAnalysisItems;

	@JsonIgnore
	private Supplier<InventoryAnalysisItem[]> _inventoryAnalysisItemsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getInventoryAnalysisItemsCount() {
		if (_inventoryAnalysisItemsCountSupplier != null) {
			inventoryAnalysisItemsCount =
				_inventoryAnalysisItemsCountSupplier.get();

			_inventoryAnalysisItemsCountSupplier = null;
		}

		return inventoryAnalysisItemsCount;
	}

	public void setInventoryAnalysisItemsCount(
		Long inventoryAnalysisItemsCount) {

		this.inventoryAnalysisItemsCount = inventoryAnalysisItemsCount;

		_inventoryAnalysisItemsCountSupplier = null;
	}

	@JsonIgnore
	public void setInventoryAnalysisItemsCount(
		UnsafeSupplier<Long, Exception>
			inventoryAnalysisItemsCountUnsafeSupplier) {

		_inventoryAnalysisItemsCountSupplier = () -> {
			try {
				return inventoryAnalysisItemsCountUnsafeSupplier.get();
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
	protected Long inventoryAnalysisItemsCount;

	@JsonIgnore
	private Supplier<Long> _inventoryAnalysisItemsCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getTotalCount() {
		if (_totalCountSupplier != null) {
			totalCount = _totalCountSupplier.get();

			_totalCountSupplier = null;
		}

		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;

		_totalCountSupplier = null;
	}

	@JsonIgnore
	public void setTotalCount(
		UnsafeSupplier<Long, Exception> totalCountUnsafeSupplier) {

		_totalCountSupplier = () -> {
			try {
				return totalCountUnsafeSupplier.get();
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
	protected Long totalCount;

	@JsonIgnore
	private Supplier<Long> _totalCountSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof InventoryAnalysis)) {
			return false;
		}

		InventoryAnalysis inventoryAnalysis = (InventoryAnalysis)object;

		return Objects.equals(toString(), inventoryAnalysis.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		InventoryAnalysisItem[] inventoryAnalysisItems =
			getInventoryAnalysisItems();

		if (inventoryAnalysisItems != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inventoryAnalysisItems\": ");

			sb.append("[");

			for (int i = 0; i < inventoryAnalysisItems.length; i++) {
				sb.append(String.valueOf(inventoryAnalysisItems[i]));

				if ((i + 1) < inventoryAnalysisItems.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long inventoryAnalysisItemsCount = getInventoryAnalysisItemsCount();

		if (inventoryAnalysisItemsCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inventoryAnalysisItemsCount\": ");

			sb.append(inventoryAnalysisItemsCount);
		}

		Long totalCount = getTotalCount();

		if (totalCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalCount\": ");

			sb.append(totalCount);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.analytics.cms.rest.dto.v1_0.InventoryAnalysis",
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