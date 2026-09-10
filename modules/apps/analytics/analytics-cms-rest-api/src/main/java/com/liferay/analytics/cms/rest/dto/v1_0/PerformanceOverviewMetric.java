/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.dto.v1_0;

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
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
@GraphQLName("PerformanceOverviewMetric")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PerformanceOverviewMetric")
public class PerformanceOverviewMetric implements Serializable {

	public static PerformanceOverviewMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(
			PerformanceOverviewMetric.class, json);
	}

	public static PerformanceOverviewMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PerformanceOverviewMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Metric getDownloadsMetric() {
		if (_downloadsMetricSupplier != null) {
			downloadsMetric = _downloadsMetricSupplier.get();

			_downloadsMetricSupplier = null;
		}

		return downloadsMetric;
	}

	public void setDownloadsMetric(Metric downloadsMetric) {
		this.downloadsMetric = downloadsMetric;

		_downloadsMetricSupplier = null;
	}

	@JsonIgnore
	public void setDownloadsMetric(
		UnsafeSupplier<Metric, Exception> downloadsMetricUnsafeSupplier) {

		_downloadsMetricSupplier = () -> {
			try {
				return downloadsMetricUnsafeSupplier.get();
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
	protected Metric downloadsMetric;

	@JsonIgnore
	private Supplier<Metric> _downloadsMetricSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Metric getImpressionsMetric() {
		if (_impressionsMetricSupplier != null) {
			impressionsMetric = _impressionsMetricSupplier.get();

			_impressionsMetricSupplier = null;
		}

		return impressionsMetric;
	}

	public void setImpressionsMetric(Metric impressionsMetric) {
		this.impressionsMetric = impressionsMetric;

		_impressionsMetricSupplier = null;
	}

	@JsonIgnore
	public void setImpressionsMetric(
		UnsafeSupplier<Metric, Exception> impressionsMetricUnsafeSupplier) {

		_impressionsMetricSupplier = () -> {
			try {
				return impressionsMetricUnsafeSupplier.get();
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
	protected Metric impressionsMetric;

	@JsonIgnore
	private Supplier<Metric> _impressionsMetricSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Metric getReadsMetric() {
		if (_readsMetricSupplier != null) {
			readsMetric = _readsMetricSupplier.get();

			_readsMetricSupplier = null;
		}

		return readsMetric;
	}

	public void setReadsMetric(Metric readsMetric) {
		this.readsMetric = readsMetric;

		_readsMetricSupplier = null;
	}

	@JsonIgnore
	public void setReadsMetric(
		UnsafeSupplier<Metric, Exception> readsMetricUnsafeSupplier) {

		_readsMetricSupplier = () -> {
			try {
				return readsMetricUnsafeSupplier.get();
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
	protected Metric readsMetric;

	@JsonIgnore
	private Supplier<Metric> _readsMetricSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Metric getViewsMetric() {
		if (_viewsMetricSupplier != null) {
			viewsMetric = _viewsMetricSupplier.get();

			_viewsMetricSupplier = null;
		}

		return viewsMetric;
	}

	public void setViewsMetric(Metric viewsMetric) {
		this.viewsMetric = viewsMetric;

		_viewsMetricSupplier = null;
	}

	@JsonIgnore
	public void setViewsMetric(
		UnsafeSupplier<Metric, Exception> viewsMetricUnsafeSupplier) {

		_viewsMetricSupplier = () -> {
			try {
				return viewsMetricUnsafeSupplier.get();
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
	protected Metric viewsMetric;

	@JsonIgnore
	private Supplier<Metric> _viewsMetricSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PerformanceOverviewMetric)) {
			return false;
		}

		PerformanceOverviewMetric performanceOverviewMetric =
			(PerformanceOverviewMetric)object;

		return Objects.equals(toString(), performanceOverviewMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Metric downloadsMetric = getDownloadsMetric();

		if (downloadsMetric != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"downloadsMetric\": ");

			sb.append(String.valueOf(downloadsMetric));
		}

		Metric impressionsMetric = getImpressionsMetric();

		if (impressionsMetric != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"impressionsMetric\": ");

			sb.append(String.valueOf(impressionsMetric));
		}

		Metric readsMetric = getReadsMetric();

		if (readsMetric != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readsMetric\": ");

			sb.append(String.valueOf(readsMetric));
		}

		Metric viewsMetric = getViewsMetric();

		if (viewsMetric != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewsMetric\": ");

			sb.append(String.valueOf(viewsMetric));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.analytics.cms.rest.dto.v1_0.PerformanceOverviewMetric",
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
// LIFERAY-REST-BUILDER-HASH:-204059774