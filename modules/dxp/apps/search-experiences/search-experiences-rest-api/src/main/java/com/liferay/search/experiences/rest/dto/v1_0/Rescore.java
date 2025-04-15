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
import com.liferay.portal.kernel.json.JSONFactoryUtil;
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
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("Rescore")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Rescore")
public class Rescore implements Serializable {

	public static Rescore toDTO(String json) {
		return ObjectMapperUtil.readValue(Rescore.class, json);
	}

	public static Rescore unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Rescore.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getQuery() {
		if (_querySupplier != null) {
			query = _querySupplier.get();

			_querySupplier = null;
		}

		return query;
	}

	public void setQuery(Object query) {
		this.query = query;

		_querySupplier = null;
	}

	@JsonIgnore
	public void setQuery(
		UnsafeSupplier<Object, Exception> queryUnsafeSupplier) {

		_querySupplier = () -> {
			try {
				return queryUnsafeSupplier.get();
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
	protected Object query;

	@JsonIgnore
	private Supplier<Object> _querySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getQueryWeight() {
		if (_queryWeightSupplier != null) {
			queryWeight = _queryWeightSupplier.get();

			_queryWeightSupplier = null;
		}

		return queryWeight;
	}

	public void setQueryWeight(Object queryWeight) {
		this.queryWeight = queryWeight;

		_queryWeightSupplier = null;
	}

	@JsonIgnore
	public void setQueryWeight(
		UnsafeSupplier<Object, Exception> queryWeightUnsafeSupplier) {

		_queryWeightSupplier = () -> {
			try {
				return queryWeightUnsafeSupplier.get();
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
	protected Object queryWeight;

	@JsonIgnore
	private Supplier<Object> _queryWeightSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getRescoreQueryWeight() {
		if (_rescoreQueryWeightSupplier != null) {
			rescoreQueryWeight = _rescoreQueryWeightSupplier.get();

			_rescoreQueryWeightSupplier = null;
		}

		return rescoreQueryWeight;
	}

	public void setRescoreQueryWeight(Object rescoreQueryWeight) {
		this.rescoreQueryWeight = rescoreQueryWeight;

		_rescoreQueryWeightSupplier = null;
	}

	@JsonIgnore
	public void setRescoreQueryWeight(
		UnsafeSupplier<Object, Exception> rescoreQueryWeightUnsafeSupplier) {

		_rescoreQueryWeightSupplier = () -> {
			try {
				return rescoreQueryWeightUnsafeSupplier.get();
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
	protected Object rescoreQueryWeight;

	@JsonIgnore
	private Supplier<Object> _rescoreQueryWeightSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getScoreMode() {
		if (_scoreModeSupplier != null) {
			scoreMode = _scoreModeSupplier.get();

			_scoreModeSupplier = null;
		}

		return scoreMode;
	}

	public void setScoreMode(String scoreMode) {
		this.scoreMode = scoreMode;

		_scoreModeSupplier = null;
	}

	@JsonIgnore
	public void setScoreMode(
		UnsafeSupplier<String, Exception> scoreModeUnsafeSupplier) {

		_scoreModeSupplier = () -> {
			try {
				return scoreModeUnsafeSupplier.get();
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
	protected String scoreMode;

	@JsonIgnore
	private Supplier<String> _scoreModeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getWindowSize() {
		if (_windowSizeSupplier != null) {
			windowSize = _windowSizeSupplier.get();

			_windowSizeSupplier = null;
		}

		return windowSize;
	}

	public void setWindowSize(Object windowSize) {
		this.windowSize = windowSize;

		_windowSizeSupplier = null;
	}

	@JsonIgnore
	public void setWindowSize(
		UnsafeSupplier<Object, Exception> windowSizeUnsafeSupplier) {

		_windowSizeSupplier = () -> {
			try {
				return windowSizeUnsafeSupplier.get();
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
	protected Object windowSize;

	@JsonIgnore
	private Supplier<Object> _windowSizeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Rescore)) {
			return false;
		}

		Rescore rescore = (Rescore)object;

		return Objects.equals(toString(), rescore.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object query = getQuery();

		if (query != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"query\": ");

			if (query instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)query));
			}
			else if (query instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)query));
				sb.append("\"");
			}
			else {
				sb.append(query);
			}
		}

		Object queryWeight = getQueryWeight();

		if (queryWeight != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"queryWeight\": ");

			if (queryWeight instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)queryWeight));
			}
			else if (queryWeight instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)queryWeight));
				sb.append("\"");
			}
			else {
				sb.append(queryWeight);
			}
		}

		Object rescoreQueryWeight = getRescoreQueryWeight();

		if (rescoreQueryWeight != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rescoreQueryWeight\": ");

			if (rescoreQueryWeight instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject(
						(Map<?, ?>)rescoreQueryWeight));
			}
			else if (rescoreQueryWeight instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)rescoreQueryWeight));
				sb.append("\"");
			}
			else {
				sb.append(rescoreQueryWeight);
			}
		}

		String scoreMode = getScoreMode();

		if (scoreMode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scoreMode\": ");

			sb.append("\"");

			sb.append(_escape(scoreMode));

			sb.append("\"");
		}

		Object windowSize = getWindowSize();

		if (windowSize != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"windowSize\": ");

			if (windowSize instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)windowSize));
			}
			else if (windowSize instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)windowSize));
				sb.append("\"");
			}
			else {
				sb.append(windowSize);
			}
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.Rescore",
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