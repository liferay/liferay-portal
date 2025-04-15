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
@GraphQLName("AppearsOnHistogram")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AppearsOnHistogram")
public class AppearsOnHistogram implements Serializable {

	public static AppearsOnHistogram toDTO(String json) {
		return ObjectMapperUtil.readValue(AppearsOnHistogram.class, json);
	}

	public static AppearsOnHistogram unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AppearsOnHistogram.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getCanonicalUrl() {
		if (_canonicalUrlSupplier != null) {
			canonicalUrl = _canonicalUrlSupplier.get();

			_canonicalUrlSupplier = null;
		}

		return canonicalUrl;
	}

	public void setCanonicalUrl(String canonicalUrl) {
		this.canonicalUrl = canonicalUrl;

		_canonicalUrlSupplier = null;
	}

	@JsonIgnore
	public void setCanonicalUrl(
		UnsafeSupplier<String, Exception> canonicalUrlUnsafeSupplier) {

		_canonicalUrlSupplier = () -> {
			try {
				return canonicalUrlUnsafeSupplier.get();
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
	protected String canonicalUrl;

	@JsonIgnore
	private Supplier<String> _canonicalUrlSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Metric[] getMetrics() {
		if (_metricsSupplier != null) {
			metrics = _metricsSupplier.get();

			_metricsSupplier = null;
		}

		return metrics;
	}

	public void setMetrics(Metric[] metrics) {
		this.metrics = metrics;

		_metricsSupplier = null;
	}

	@JsonIgnore
	public void setMetrics(
		UnsafeSupplier<Metric[], Exception> metricsUnsafeSupplier) {

		_metricsSupplier = () -> {
			try {
				return metricsUnsafeSupplier.get();
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
	protected Metric[] metrics;

	@JsonIgnore
	private Supplier<Metric[]> _metricsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPageTitle() {
		if (_pageTitleSupplier != null) {
			pageTitle = _pageTitleSupplier.get();

			_pageTitleSupplier = null;
		}

		return pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;

		_pageTitleSupplier = null;
	}

	@JsonIgnore
	public void setPageTitle(
		UnsafeSupplier<String, Exception> pageTitleUnsafeSupplier) {

		_pageTitleSupplier = () -> {
			try {
				return pageTitleUnsafeSupplier.get();
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
	protected String pageTitle;

	@JsonIgnore
	private Supplier<String> _pageTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getTotal() {
		if (_totalSupplier != null) {
			total = _totalSupplier.get();

			_totalSupplier = null;
		}

		return total;
	}

	public void setTotal(Double total) {
		this.total = total;

		_totalSupplier = null;
	}

	@JsonIgnore
	public void setTotal(
		UnsafeSupplier<Double, Exception> totalUnsafeSupplier) {

		_totalSupplier = () -> {
			try {
				return totalUnsafeSupplier.get();
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
	protected Double total;

	@JsonIgnore
	private Supplier<Double> _totalSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getTotalValue() {
		if (_totalValueSupplier != null) {
			totalValue = _totalValueSupplier.get();

			_totalValueSupplier = null;
		}

		return totalValue;
	}

	public void setTotalValue(Double totalValue) {
		this.totalValue = totalValue;

		_totalValueSupplier = null;
	}

	@JsonIgnore
	public void setTotalValue(
		UnsafeSupplier<Double, Exception> totalValueUnsafeSupplier) {

		_totalValueSupplier = () -> {
			try {
				return totalValueUnsafeSupplier.get();
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
	protected Double totalValue;

	@JsonIgnore
	private Supplier<Double> _totalValueSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AppearsOnHistogram)) {
			return false;
		}

		AppearsOnHistogram appearsOnHistogram = (AppearsOnHistogram)object;

		return Objects.equals(toString(), appearsOnHistogram.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String canonicalUrl = getCanonicalUrl();

		if (canonicalUrl != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"canonicalUrl\": ");

			sb.append("\"");

			sb.append(_escape(canonicalUrl));

			sb.append("\"");
		}

		Metric[] metrics = getMetrics();

		if (metrics != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metrics\": ");

			sb.append("[");

			for (int i = 0; i < metrics.length; i++) {
				sb.append(String.valueOf(metrics[i]));

				if ((i + 1) < metrics.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String pageTitle = getPageTitle();

		if (pageTitle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTitle\": ");

			sb.append("\"");

			sb.append(_escape(pageTitle));

			sb.append("\"");
		}

		Double total = getTotal();

		if (total != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"total\": ");

			sb.append(total);
		}

		Double totalValue = getTotalValue();

		if (totalValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalValue\": ");

			sb.append(totalValue);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.analytics.reports.rest.dto.v1_0.AppearsOnHistogram",
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