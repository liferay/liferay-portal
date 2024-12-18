/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v2_0;

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

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Generated;

import javax.validation.Valid;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("RescoreConfiguration")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "RescoreConfiguration")
public class RescoreConfiguration implements Serializable {

	public static RescoreConfiguration toDTO(String json) {
		return ObjectMapperUtil.readValue(RescoreConfiguration.class, json);
	}

	public static RescoreConfiguration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			RescoreConfiguration.class, json);
	}

	@Schema
	@Valid
	public Clause[] getClauses() {
		if (_clausesSupplier != null) {
			clauses = _clausesSupplier.get();

			_clausesSupplier = null;
		}

		return clauses;
	}

	public void setClauses(Clause[] clauses) {
		this.clauses = clauses;

		_clausesSupplier = null;
	}

	@JsonIgnore
	public void setClauses(
		UnsafeSupplier<Clause[], Exception> clausesUnsafeSupplier) {

		_clausesSupplier = () -> {
			try {
				return clausesUnsafeSupplier.get();
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
	protected Clause[] clauses;

	@JsonIgnore
	private Supplier<Clause[]> _clausesSupplier;

	@Schema
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

	@Schema
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

	@Schema
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

	@Schema
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

		if (!(object instanceof RescoreConfiguration)) {
			return false;
		}

		RescoreConfiguration rescoreConfiguration =
			(RescoreConfiguration)object;

		return Objects.equals(toString(), rescoreConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Clause[] clauses = getClauses();

		if (clauses != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clauses\": ");

			sb.append("[");

			for (int i = 0; i < clauses.length; i++) {
				sb.append(String.valueOf(clauses[i]));

				if ((i + 1) < clauses.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

	@Schema(
		accessMode = Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v2_0.RescoreConfiguration",
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