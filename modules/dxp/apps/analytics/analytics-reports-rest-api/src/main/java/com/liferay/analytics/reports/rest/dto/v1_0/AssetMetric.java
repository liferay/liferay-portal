/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.dto.v1_0;

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
 * @author Marcos Martins
 * @generated
 */
@Generated("")
@GraphQLName("AssetMetric")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AssetMetric")
public class AssetMetric implements Serializable {

	public static AssetMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(AssetMetric.class, json);
	}

	public static AssetMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AssetMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAssetId() {
		if (_assetIdSupplier != null) {
			assetId = _assetIdSupplier.get();

			_assetIdSupplier = null;
		}

		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;

		_assetIdSupplier = null;
	}

	@JsonIgnore
	public void setAssetId(
		UnsafeSupplier<String, Exception> assetIdUnsafeSupplier) {

		_assetIdSupplier = () -> {
			try {
				return assetIdUnsafeSupplier.get();
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
	protected String assetId;

	@JsonIgnore
	private Supplier<String> _assetIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAssetTitle() {
		if (_assetTitleSupplier != null) {
			assetTitle = _assetTitleSupplier.get();

			_assetTitleSupplier = null;
		}

		return assetTitle;
	}

	public void setAssetTitle(String assetTitle) {
		this.assetTitle = assetTitle;

		_assetTitleSupplier = null;
	}

	@JsonIgnore
	public void setAssetTitle(
		UnsafeSupplier<String, Exception> assetTitleUnsafeSupplier) {

		_assetTitleSupplier = () -> {
			try {
				return assetTitleUnsafeSupplier.get();
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
	protected String assetTitle;

	@JsonIgnore
	private Supplier<String> _assetTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAssetType() {
		if (_assetTypeSupplier != null) {
			assetType = _assetTypeSupplier.get();

			_assetTypeSupplier = null;
		}

		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;

		_assetTypeSupplier = null;
	}

	@JsonIgnore
	public void setAssetType(
		UnsafeSupplier<String, Exception> assetTypeUnsafeSupplier) {

		_assetTypeSupplier = () -> {
			try {
				return assetTypeUnsafeSupplier.get();
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
	protected String assetType;

	@JsonIgnore
	private Supplier<String> _assetTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDataSourceId() {
		if (_dataSourceIdSupplier != null) {
			dataSourceId = _dataSourceIdSupplier.get();

			_dataSourceIdSupplier = null;
		}

		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;

		_dataSourceIdSupplier = null;
	}

	@JsonIgnore
	public void setDataSourceId(
		UnsafeSupplier<String, Exception> dataSourceIdUnsafeSupplier) {

		_dataSourceIdSupplier = () -> {
			try {
				return dataSourceIdUnsafeSupplier.get();
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
	protected String dataSourceId;

	@JsonIgnore
	private Supplier<String> _dataSourceIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Metric getDefaultMetric() {
		if (_defaultMetricSupplier != null) {
			defaultMetric = _defaultMetricSupplier.get();

			_defaultMetricSupplier = null;
		}

		return defaultMetric;
	}

	public void setDefaultMetric(Metric defaultMetric) {
		this.defaultMetric = defaultMetric;

		_defaultMetricSupplier = null;
	}

	@JsonIgnore
	public void setDefaultMetric(
		UnsafeSupplier<Metric, Exception> defaultMetricUnsafeSupplier) {

		_defaultMetricSupplier = () -> {
			try {
				return defaultMetricUnsafeSupplier.get();
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
	protected Metric defaultMetric;

	@JsonIgnore
	private Supplier<Metric> _defaultMetricSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Metric[] getSelectedMetrics() {
		if (_selectedMetricsSupplier != null) {
			selectedMetrics = _selectedMetricsSupplier.get();

			_selectedMetricsSupplier = null;
		}

		return selectedMetrics;
	}

	public void setSelectedMetrics(Metric[] selectedMetrics) {
		this.selectedMetrics = selectedMetrics;

		_selectedMetricsSupplier = null;
	}

	@JsonIgnore
	public void setSelectedMetrics(
		UnsafeSupplier<Metric[], Exception> selectedMetricsUnsafeSupplier) {

		_selectedMetricsSupplier = () -> {
			try {
				return selectedMetricsUnsafeSupplier.get();
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
	protected Metric[] selectedMetrics;

	@JsonIgnore
	private Supplier<Metric[]> _selectedMetricsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssetMetric)) {
			return false;
		}

		AssetMetric assetMetric = (AssetMetric)object;

		return Objects.equals(toString(), assetMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String assetId = getAssetId();

		if (assetId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetId\": ");

			sb.append("\"");

			sb.append(_escape(assetId));

			sb.append("\"");
		}

		String assetTitle = getAssetTitle();

		if (assetTitle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle\": ");

			sb.append("\"");

			sb.append(_escape(assetTitle));

			sb.append("\"");
		}

		String assetType = getAssetType();

		if (assetType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetType\": ");

			sb.append("\"");

			sb.append(_escape(assetType));

			sb.append("\"");
		}

		String dataSourceId = getDataSourceId();

		if (dataSourceId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataSourceId\": ");

			sb.append("\"");

			sb.append(_escape(dataSourceId));

			sb.append("\"");
		}

		Metric defaultMetric = getDefaultMetric();

		if (defaultMetric != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultMetric\": ");

			sb.append(String.valueOf(defaultMetric));
		}

		Metric[] selectedMetrics = getSelectedMetrics();

		if (selectedMetrics != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectedMetrics\": ");

			sb.append("[");

			for (int i = 0; i < selectedMetrics.length; i++) {
				sb.append(String.valueOf(selectedMetrics[i]));

				if ((i + 1) < selectedMetrics.length) {
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
		defaultValue = "com.liferay.analytics.reports.rest.dto.v1_0.AssetMetric",
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