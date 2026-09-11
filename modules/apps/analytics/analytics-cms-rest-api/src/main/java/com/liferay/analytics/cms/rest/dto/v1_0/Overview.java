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
@GraphQLName("Overview")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Overview")
public class Overview implements Serializable {

	public static Overview toDTO(String json) {
		return ObjectMapperUtil.readValue(Overview.class, json);
	}

	public static Overview unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Overview.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getCategoriesCount() {
		if (_categoriesCountSupplier != null) {
			categoriesCount = _categoriesCountSupplier.get();

			_categoriesCountSupplier = null;
		}

		return categoriesCount;
	}

	public void setCategoriesCount(Long categoriesCount) {
		this.categoriesCount = categoriesCount;

		_categoriesCountSupplier = null;
	}

	@JsonIgnore
	public void setCategoriesCount(
		UnsafeSupplier<Long, Exception> categoriesCountUnsafeSupplier) {

		_categoriesCountSupplier = () -> {
			try {
				return categoriesCountUnsafeSupplier.get();
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
	protected Long categoriesCount;

	@JsonIgnore
	private Supplier<Long> _categoriesCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getTagsCount() {
		if (_tagsCountSupplier != null) {
			tagsCount = _tagsCountSupplier.get();

			_tagsCountSupplier = null;
		}

		return tagsCount;
	}

	public void setTagsCount(Long tagsCount) {
		this.tagsCount = tagsCount;

		_tagsCountSupplier = null;
	}

	@JsonIgnore
	public void setTagsCount(
		UnsafeSupplier<Long, Exception> tagsCountUnsafeSupplier) {

		_tagsCountSupplier = () -> {
			try {
				return tagsCountUnsafeSupplier.get();
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
	protected Long tagsCount;

	@JsonIgnore
	private Supplier<Long> _tagsCountSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Trend getTrend() {
		if (_trendSupplier != null) {
			trend = _trendSupplier.get();

			_trendSupplier = null;
		}

		return trend;
	}

	public void setTrend(Trend trend) {
		this.trend = trend;

		_trendSupplier = null;
	}

	@JsonIgnore
	public void setTrend(UnsafeSupplier<Trend, Exception> trendUnsafeSupplier) {
		_trendSupplier = () -> {
			try {
				return trendUnsafeSupplier.get();
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
	protected Trend trend;

	@JsonIgnore
	private Supplier<Trend> _trendSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getVocabulariesCount() {
		if (_vocabulariesCountSupplier != null) {
			vocabulariesCount = _vocabulariesCountSupplier.get();

			_vocabulariesCountSupplier = null;
		}

		return vocabulariesCount;
	}

	public void setVocabulariesCount(Long vocabulariesCount) {
		this.vocabulariesCount = vocabulariesCount;

		_vocabulariesCountSupplier = null;
	}

	@JsonIgnore
	public void setVocabulariesCount(
		UnsafeSupplier<Long, Exception> vocabulariesCountUnsafeSupplier) {

		_vocabulariesCountSupplier = () -> {
			try {
				return vocabulariesCountUnsafeSupplier.get();
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
	protected Long vocabulariesCount;

	@JsonIgnore
	private Supplier<Long> _vocabulariesCountSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Overview)) {
			return false;
		}

		Overview overview = (Overview)object;

		return Objects.equals(toString(), overview.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long categoriesCount = getCategoriesCount();

		if (categoriesCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categoriesCount\": ");

			sb.append(categoriesCount);
		}

		Long tagsCount = getTagsCount();

		if (tagsCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tagsCount\": ");

			sb.append(tagsCount);
		}

		Long totalCount = getTotalCount();

		if (totalCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalCount\": ");

			sb.append(totalCount);
		}

		Trend trend = getTrend();

		if (trend != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trend\": ");

			sb.append(String.valueOf(trend));
		}

		Long vocabulariesCount = getVocabulariesCount();

		if (vocabulariesCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"vocabulariesCount\": ");

			sb.append(vocabulariesCount);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.analytics.cms.rest.dto.v1_0.Overview",
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

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:113166448