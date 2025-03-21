/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.dto.v1_0;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
@GraphQLName("FrequentPatternRecommendation")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FrequentPatternRecommendation")
public class FrequentPatternRecommendation implements Serializable {

	public static FrequentPatternRecommendation toDTO(String json) {
		return ObjectMapperUtil.readValue(
			FrequentPatternRecommendation.class, json);
	}

	public static FrequentPatternRecommendation unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			FrequentPatternRecommendation.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long[] getAntecedentIds() {
		if (_antecedentIdsSupplier != null) {
			antecedentIds = _antecedentIdsSupplier.get();

			_antecedentIdsSupplier = null;
		}

		return antecedentIds;
	}

	public void setAntecedentIds(Long[] antecedentIds) {
		this.antecedentIds = antecedentIds;

		_antecedentIdsSupplier = null;
	}

	@JsonIgnore
	public void setAntecedentIds(
		UnsafeSupplier<Long[], Exception> antecedentIdsUnsafeSupplier) {

		_antecedentIdsSupplier = () -> {
			try {
				return antecedentIdsUnsafeSupplier.get();
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
	protected Long[] antecedentIds;

	@JsonIgnore
	private Supplier<Long[]> _antecedentIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getAntecedentIdsLength() {
		if (_antecedentIdsLengthSupplier != null) {
			antecedentIdsLength = _antecedentIdsLengthSupplier.get();

			_antecedentIdsLengthSupplier = null;
		}

		return antecedentIdsLength;
	}

	public void setAntecedentIdsLength(Long antecedentIdsLength) {
		this.antecedentIdsLength = antecedentIdsLength;

		_antecedentIdsLengthSupplier = null;
	}

	@JsonIgnore
	public void setAntecedentIdsLength(
		UnsafeSupplier<Long, Exception> antecedentIdsLengthUnsafeSupplier) {

		_antecedentIdsLengthSupplier = () -> {
			try {
				return antecedentIdsLengthUnsafeSupplier.get();
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
	protected Long antecedentIdsLength;

	@JsonIgnore
	private Supplier<Long> _antecedentIdsLengthSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "2017-07-21")
	public Date getCreateDate() {
		if (_createDateSupplier != null) {
			createDate = _createDateSupplier.get();

			_createDateSupplier = null;
		}

		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;

		_createDateSupplier = null;
	}

	@JsonIgnore
	public void setCreateDate(
		UnsafeSupplier<Date, Exception> createDateUnsafeSupplier) {

		_createDateSupplier = () -> {
			try {
				return createDateUnsafeSupplier.get();
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
	protected Date createDate;

	@JsonIgnore
	private Supplier<Date> _createDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getJobId() {
		if (_jobIdSupplier != null) {
			jobId = _jobIdSupplier.get();

			_jobIdSupplier = null;
		}

		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;

		_jobIdSupplier = null;
	}

	@JsonIgnore
	public void setJobId(
		UnsafeSupplier<String, Exception> jobIdUnsafeSupplier) {

		_jobIdSupplier = () -> {
			try {
				return jobIdUnsafeSupplier.get();
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
	protected String jobId;

	@JsonIgnore
	private Supplier<String> _jobIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The recommended product identifier."
	)
	public Long getRecommendedProductId() {
		if (_recommendedProductIdSupplier != null) {
			recommendedProductId = _recommendedProductIdSupplier.get();

			_recommendedProductIdSupplier = null;
		}

		return recommendedProductId;
	}

	public void setRecommendedProductId(Long recommendedProductId) {
		this.recommendedProductId = recommendedProductId;

		_recommendedProductIdSupplier = null;
	}

	@JsonIgnore
	public void setRecommendedProductId(
		UnsafeSupplier<Long, Exception> recommendedProductIdUnsafeSupplier) {

		_recommendedProductIdSupplier = () -> {
			try {
				return recommendedProductIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The recommended product identifier.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long recommendedProductId;

	@JsonIgnore
	private Supplier<Long> _recommendedProductIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The recommendation score."
	)
	@Valid
	public Float getScore() {
		if (_scoreSupplier != null) {
			score = _scoreSupplier.get();

			_scoreSupplier = null;
		}

		return score;
	}

	public void setScore(Float score) {
		this.score = score;

		_scoreSupplier = null;
	}

	@JsonIgnore
	public void setScore(UnsafeSupplier<Float, Exception> scoreUnsafeSupplier) {
		_scoreSupplier = () -> {
			try {
				return scoreUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The recommendation score.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Float score;

	@JsonIgnore
	private Supplier<Float> _scoreSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FrequentPatternRecommendation)) {
			return false;
		}

		FrequentPatternRecommendation frequentPatternRecommendation =
			(FrequentPatternRecommendation)object;

		return Objects.equals(
			toString(), frequentPatternRecommendation.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		Long[] antecedentIds = getAntecedentIds();

		if (antecedentIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"antecedentIds\": ");

			sb.append("[");

			for (int i = 0; i < antecedentIds.length; i++) {
				sb.append(antecedentIds[i]);

				if ((i + 1) < antecedentIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Long antecedentIdsLength = getAntecedentIdsLength();

		if (antecedentIdsLength != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"antecedentIdsLength\": ");

			sb.append(antecedentIdsLength);
		}

		Date createDate = getCreateDate();

		if (createDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(createDate));

			sb.append("\"");
		}

		String jobId = getJobId();

		if (jobId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"jobId\": ");

			sb.append("\"");

			sb.append(_escape(jobId));

			sb.append("\"");
		}

		Long recommendedProductId = getRecommendedProductId();

		if (recommendedProductId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"recommendedProductId\": ");

			sb.append(recommendedProductId);
		}

		Float score = getScore();

		if (score != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"score\": ");

			sb.append(score);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.machine.learning.dto.v1_0.FrequentPatternRecommendation",
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