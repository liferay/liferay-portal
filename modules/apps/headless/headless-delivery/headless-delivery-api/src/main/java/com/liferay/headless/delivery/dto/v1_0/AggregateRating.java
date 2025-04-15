/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents the average rating. See [AggregateRating](https://www.schema.org/AggregateRating) for more information.",
	value = "AggregateRating"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AggregateRating")
public class AggregateRating implements Serializable {

	public static AggregateRating toDTO(String json) {
		return ObjectMapperUtil.readValue(AggregateRating.class, json);
	}

	public static AggregateRating unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AggregateRating.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The highest possible rating (by default normalized to 1.0)."
	)
	public Double getBestRating() {
		if (_bestRatingSupplier != null) {
			bestRating = _bestRatingSupplier.get();

			_bestRatingSupplier = null;
		}

		return bestRating;
	}

	public void setBestRating(Double bestRating) {
		this.bestRating = bestRating;

		_bestRatingSupplier = null;
	}

	@JsonIgnore
	public void setBestRating(
		UnsafeSupplier<Double, Exception> bestRatingUnsafeSupplier) {

		_bestRatingSupplier = () -> {
			try {
				return bestRatingUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The highest possible rating (by default normalized to 1.0)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double bestRating;

	@JsonIgnore
	private Supplier<Double> _bestRatingSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The average rating."
	)
	public Double getRatingAverage() {
		if (_ratingAverageSupplier != null) {
			ratingAverage = _ratingAverageSupplier.get();

			_ratingAverageSupplier = null;
		}

		return ratingAverage;
	}

	public void setRatingAverage(Double ratingAverage) {
		this.ratingAverage = ratingAverage;

		_ratingAverageSupplier = null;
	}

	@JsonIgnore
	public void setRatingAverage(
		UnsafeSupplier<Double, Exception> ratingAverageUnsafeSupplier) {

		_ratingAverageSupplier = () -> {
			try {
				return ratingAverageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The average rating.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double ratingAverage;

	@JsonIgnore
	private Supplier<Double> _ratingAverageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The number of ratings."
	)
	public Integer getRatingCount() {
		if (_ratingCountSupplier != null) {
			ratingCount = _ratingCountSupplier.get();

			_ratingCountSupplier = null;
		}

		return ratingCount;
	}

	public void setRatingCount(Integer ratingCount) {
		this.ratingCount = ratingCount;

		_ratingCountSupplier = null;
	}

	@JsonIgnore
	public void setRatingCount(
		UnsafeSupplier<Integer, Exception> ratingCountUnsafeSupplier) {

		_ratingCountSupplier = () -> {
			try {
				return ratingCountUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The number of ratings.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer ratingCount;

	@JsonIgnore
	private Supplier<Integer> _ratingCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The rating value."
	)
	public Double getRatingValue() {
		if (_ratingValueSupplier != null) {
			ratingValue = _ratingValueSupplier.get();

			_ratingValueSupplier = null;
		}

		return ratingValue;
	}

	public void setRatingValue(Double ratingValue) {
		this.ratingValue = ratingValue;

		_ratingValueSupplier = null;
	}

	@JsonIgnore
	public void setRatingValue(
		UnsafeSupplier<Double, Exception> ratingValueUnsafeSupplier) {

		_ratingValueSupplier = () -> {
			try {
				return ratingValueUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The rating value.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double ratingValue;

	@JsonIgnore
	private Supplier<Double> _ratingValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The lowest possible rating (by default normalized to 0.0)."
	)
	public Double getWorstRating() {
		if (_worstRatingSupplier != null) {
			worstRating = _worstRatingSupplier.get();

			_worstRatingSupplier = null;
		}

		return worstRating;
	}

	public void setWorstRating(Double worstRating) {
		this.worstRating = worstRating;

		_worstRatingSupplier = null;
	}

	@JsonIgnore
	public void setWorstRating(
		UnsafeSupplier<Double, Exception> worstRatingUnsafeSupplier) {

		_worstRatingSupplier = () -> {
			try {
				return worstRatingUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The lowest possible rating (by default normalized to 0.0)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double worstRating;

	@JsonIgnore
	private Supplier<Double> _worstRatingSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AggregateRating)) {
			return false;
		}

		AggregateRating aggregateRating = (AggregateRating)object;

		return Objects.equals(toString(), aggregateRating.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Double bestRating = getBestRating();

		if (bestRating != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bestRating\": ");

			sb.append(bestRating);
		}

		Double ratingAverage = getRatingAverage();

		if (ratingAverage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratingAverage\": ");

			sb.append(ratingAverage);
		}

		Integer ratingCount = getRatingCount();

		if (ratingCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratingCount\": ");

			sb.append(ratingCount);
		}

		Double ratingValue = getRatingValue();

		if (ratingValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratingValue\": ");

			sb.append(ratingValue);
		}

		Double worstRating = getWorstRating();

		if (worstRating != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"worstRating\": ");

			sb.append(worstRating);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.AggregateRating",
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