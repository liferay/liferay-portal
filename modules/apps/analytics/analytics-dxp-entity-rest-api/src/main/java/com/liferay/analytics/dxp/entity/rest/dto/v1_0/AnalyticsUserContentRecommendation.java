/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.dxp.entity.rest.dto.v1_0;

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
 * @author Marcos Martins, Rachael Koestartyo, Riccardo Ferrari
 * @generated
 */
@Generated("")
@GraphQLName("AnalyticsUserContentRecommendation")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AnalyticsUserContentRecommendation")
public class AnalyticsUserContentRecommendation implements Serializable {

	public static AnalyticsUserContentRecommendation toDTO(String json) {
		return ObjectMapperUtil.readValue(
			AnalyticsUserContentRecommendation.class, json);
	}

	public static AnalyticsUserContentRecommendation unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			AnalyticsUserContentRecommendation.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long[] getAssetCategoryIds() {
		if (_assetCategoryIdsSupplier != null) {
			assetCategoryIds = _assetCategoryIdsSupplier.get();

			_assetCategoryIdsSupplier = null;
		}

		return assetCategoryIds;
	}

	public void setAssetCategoryIds(Long[] assetCategoryIds) {
		this.assetCategoryIds = assetCategoryIds;

		_assetCategoryIdsSupplier = null;
	}

	@JsonIgnore
	public void setAssetCategoryIds(
		UnsafeSupplier<Long[], Exception> assetCategoryIdsUnsafeSupplier) {

		_assetCategoryIdsSupplier = () -> {
			try {
				return assetCategoryIdsUnsafeSupplier.get();
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
	protected Long[] assetCategoryIds;

	@JsonIgnore
	private Supplier<Long[]> _assetCategoryIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getRank() {
		if (_rankSupplier != null) {
			rank = _rankSupplier.get();

			_rankSupplier = null;
		}

		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;

		_rankSupplier = null;
	}

	@JsonIgnore
	public void setRank(UnsafeSupplier<Integer, Exception> rankUnsafeSupplier) {
		_rankSupplier = () -> {
			try {
				return rankUnsafeSupplier.get();
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
	protected Integer rank;

	@JsonIgnore
	private Supplier<Integer> _rankSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getRecommendedAssetEntryId() {
		if (_recommendedAssetEntryIdSupplier != null) {
			recommendedAssetEntryId = _recommendedAssetEntryIdSupplier.get();

			_recommendedAssetEntryIdSupplier = null;
		}

		return recommendedAssetEntryId;
	}

	public void setRecommendedAssetEntryId(Long recommendedAssetEntryId) {
		this.recommendedAssetEntryId = recommendedAssetEntryId;

		_recommendedAssetEntryIdSupplier = null;
	}

	@JsonIgnore
	public void setRecommendedAssetEntryId(
		UnsafeSupplier<Long, Exception> recommendedAssetEntryIdUnsafeSupplier) {

		_recommendedAssetEntryIdSupplier = () -> {
			try {
				return recommendedAssetEntryIdUnsafeSupplier.get();
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
	protected Long recommendedAssetEntryId;

	@JsonIgnore
	private Supplier<Long> _recommendedAssetEntryIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Float score;

	@JsonIgnore
	private Supplier<Float> _scoreSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getUserId() {
		if (_userIdSupplier != null) {
			userId = _userIdSupplier.get();

			_userIdSupplier = null;
		}

		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;

		_userIdSupplier = null;
	}

	@JsonIgnore
	public void setUserId(
		UnsafeSupplier<Long, Exception> userIdUnsafeSupplier) {

		_userIdSupplier = () -> {
			try {
				return userIdUnsafeSupplier.get();
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
	protected Long userId;

	@JsonIgnore
	private Supplier<Long> _userIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AnalyticsUserContentRecommendation)) {
			return false;
		}

		AnalyticsUserContentRecommendation analyticsUserContentRecommendation =
			(AnalyticsUserContentRecommendation)object;

		return Objects.equals(
			toString(), analyticsUserContentRecommendation.toString());
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

		Long[] assetCategoryIds = getAssetCategoryIds();

		if (assetCategoryIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetCategoryIds\": ");

			sb.append("[");

			for (int i = 0; i < assetCategoryIds.length; i++) {
				sb.append(assetCategoryIds[i]);

				if ((i + 1) < assetCategoryIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		Integer rank = getRank();

		if (rank != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rank\": ");

			sb.append(rank);
		}

		Long recommendedAssetEntryId = getRecommendedAssetEntryId();

		if (recommendedAssetEntryId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"recommendedAssetEntryId\": ");

			sb.append(recommendedAssetEntryId);
		}

		Float score = getScore();

		if (score != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"score\": ");

			sb.append(score);
		}

		Long userId = getUserId();

		if (userId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userId\": ");

			sb.append(userId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.analytics.dxp.entity.rest.dto.v1_0.AnalyticsUserContentRecommendation",
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