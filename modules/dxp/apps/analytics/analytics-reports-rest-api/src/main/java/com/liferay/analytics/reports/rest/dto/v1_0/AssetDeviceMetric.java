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
 * @author Marcos Martins
 * @generated
 */
@Generated("")
@GraphQLName("AssetDeviceMetric")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AssetDeviceMetric")
public class AssetDeviceMetric implements Serializable {

	public static AssetDeviceMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(AssetDeviceMetric.class, json);
	}

	public static AssetDeviceMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AssetDeviceMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public DeviceMetric[] getDeviceMetrics() {
		if (_deviceMetricsSupplier != null) {
			deviceMetrics = _deviceMetricsSupplier.get();

			_deviceMetricsSupplier = null;
		}

		return deviceMetrics;
	}

	public void setDeviceMetrics(DeviceMetric[] deviceMetrics) {
		this.deviceMetrics = deviceMetrics;

		_deviceMetricsSupplier = null;
	}

	@JsonIgnore
	public void setDeviceMetrics(
		UnsafeSupplier<DeviceMetric[], Exception> deviceMetricsUnsafeSupplier) {

		_deviceMetricsSupplier = () -> {
			try {
				return deviceMetricsUnsafeSupplier.get();
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
	protected DeviceMetric[] deviceMetrics;

	@JsonIgnore
	private Supplier<DeviceMetric[]> _deviceMetricsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssetDeviceMetric)) {
			return false;
		}

		AssetDeviceMetric assetDeviceMetric = (AssetDeviceMetric)object;

		return Objects.equals(toString(), assetDeviceMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DeviceMetric[] deviceMetrics = getDeviceMetrics();

		if (deviceMetrics != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deviceMetrics\": ");

			sb.append("[");

			for (int i = 0; i < deviceMetrics.length; i++) {
				sb.append(String.valueOf(deviceMetrics[i]));

				if ((i + 1) < deviceMetrics.length) {
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
		defaultValue = "com.liferay.analytics.reports.rest.dto.v1_0.AssetDeviceMetric",
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