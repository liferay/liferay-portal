/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v1_0;

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
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("SearchHits")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SearchHits")
public class SearchHits implements Serializable {

	public static SearchHits toDTO(String json) {
		return ObjectMapperUtil.readValue(SearchHits.class, json);
	}

	public static SearchHits unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SearchHits.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Hit[] getHits() {
		if (_hitsSupplier != null) {
			hits = _hitsSupplier.get();

			_hitsSupplier = null;
		}

		return hits;
	}

	public void setHits(Hit[] hits) {
		this.hits = hits;

		_hitsSupplier = null;
	}

	@JsonIgnore
	public void setHits(UnsafeSupplier<Hit[], Exception> hitsUnsafeSupplier) {
		_hitsSupplier = () -> {
			try {
				return hitsUnsafeSupplier.get();
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
	protected Hit[] hits;

	@JsonIgnore
	private Supplier<Hit[]> _hitsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Float getMaxScore() {
		if (_maxScoreSupplier != null) {
			maxScore = _maxScoreSupplier.get();

			_maxScoreSupplier = null;
		}

		return maxScore;
	}

	public void setMaxScore(Float maxScore) {
		this.maxScore = maxScore;

		_maxScoreSupplier = null;
	}

	@JsonIgnore
	public void setMaxScore(
		UnsafeSupplier<Float, Exception> maxScoreUnsafeSupplier) {

		_maxScoreSupplier = () -> {
			try {
				return maxScoreUnsafeSupplier.get();
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
	protected Float maxScore;

	@JsonIgnore
	private Supplier<Float> _maxScoreSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getTotalHits() {
		if (_totalHitsSupplier != null) {
			totalHits = _totalHitsSupplier.get();

			_totalHitsSupplier = null;
		}

		return totalHits;
	}

	public void setTotalHits(Long totalHits) {
		this.totalHits = totalHits;

		_totalHitsSupplier = null;
	}

	@JsonIgnore
	public void setTotalHits(
		UnsafeSupplier<Long, Exception> totalHitsUnsafeSupplier) {

		_totalHitsSupplier = () -> {
			try {
				return totalHitsUnsafeSupplier.get();
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
	protected Long totalHits;

	@JsonIgnore
	private Supplier<Long> _totalHitsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SearchHits)) {
			return false;
		}

		SearchHits searchHits = (SearchHits)object;

		return Objects.equals(toString(), searchHits.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Hit[] hits = getHits();

		if (hits != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hits\": ");

			sb.append("[");

			for (int i = 0; i < hits.length; i++) {
				sb.append(String.valueOf(hits[i]));

				if ((i + 1) < hits.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Float maxScore = getMaxScore();

		if (maxScore != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxScore\": ");

			sb.append(maxScore);
		}

		Long totalHits = getTotalHits();

		if (totalHits != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalHits\": ");

			sb.append(totalHits);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.SearchHits",
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