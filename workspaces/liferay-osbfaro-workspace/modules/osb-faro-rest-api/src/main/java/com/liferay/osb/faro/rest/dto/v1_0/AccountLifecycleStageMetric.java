/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.dto.v1_0;

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
 * @author Leslie Wong
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A stage of an account lifecycle, with how many accounts are in it and how they move through it. Use `getWorkspaceGroupChannelAccountLifecycleStageMetricsPage` to list them.",
	value = "AccountLifecycleStageMetric"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A stage of an account lifecycle, with how many accounts are in it and how they move through it. Use `getWorkspaceGroupChannelAccountLifecycleStageMetricsPage` to list them."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AccountLifecycleStageMetric")
public class AccountLifecycleStageMetric implements Serializable {

	public static AccountLifecycleStageMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(
			AccountLifecycleStageMetric.class, json);
	}

	public static AccountLifecycleStageMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			AccountLifecycleStageMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Number of accounts currently in the stage."
	)
	public Long getAccountsCount() {
		if (_accountsCountSupplier != null) {
			accountsCount = _accountsCountSupplier.get();

			_accountsCountSupplier = null;
		}

		return accountsCount;
	}

	public void setAccountsCount(Long accountsCount) {
		this.accountsCount = accountsCount;

		_accountsCountSupplier = null;
	}

	@JsonIgnore
	public void setAccountsCount(
		UnsafeSupplier<Long, Exception> accountsCountUnsafeSupplier) {

		_accountsCountSupplier = () -> {
			try {
				return accountsCountUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Number of accounts currently in the stage.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long accountsCount;

	@JsonIgnore
	private Supplier<Long> _accountsCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Average number of days accounts stayed in the stage, over the stays that started in the last 90 days and have ended. 0 when there are none."
	)
	public Double getAverageStageDuration() {
		if (_averageStageDurationSupplier != null) {
			averageStageDuration = _averageStageDurationSupplier.get();

			_averageStageDurationSupplier = null;
		}

		return averageStageDuration;
	}

	public void setAverageStageDuration(Double averageStageDuration) {
		this.averageStageDuration = averageStageDuration;

		_averageStageDurationSupplier = null;
	}

	@JsonIgnore
	public void setAverageStageDuration(
		UnsafeSupplier<Double, Exception> averageStageDurationUnsafeSupplier) {

		_averageStageDurationSupplier = () -> {
			try {
				return averageStageDurationUnsafeSupplier.get();
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
		description = "Average number of days accounts stayed in the stage, over the stays that started in the last 90 days and have ended. 0 when there are none."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double averageStageDuration;

	@JsonIgnore
	private Supplier<Double> _averageStageDurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Moves from the stage to the next stage in the last 90 days, as a percentage of the accounts that entered the stage in that time. Null for the last stage and when no account entered the stage in that time."
	)
	public Double getConversionRateToNextStage() {
		if (_conversionRateToNextStageSupplier != null) {
			conversionRateToNextStage =
				_conversionRateToNextStageSupplier.get();

			_conversionRateToNextStageSupplier = null;
		}

		return conversionRateToNextStage;
	}

	public void setConversionRateToNextStage(Double conversionRateToNextStage) {
		this.conversionRateToNextStage = conversionRateToNextStage;

		_conversionRateToNextStageSupplier = null;
	}

	@JsonIgnore
	public void setConversionRateToNextStage(
		UnsafeSupplier<Double, Exception>
			conversionRateToNextStageUnsafeSupplier) {

		_conversionRateToNextStageSupplier = () -> {
			try {
				return conversionRateToNextStageUnsafeSupplier.get();
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
		description = "Moves from the stage to the next stage in the last 90 days, as a percentage of the accounts that entered the stage in that time. Null for the last stage and when no account entered the stage in that time."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double conversionRateToNextStage;

	@JsonIgnore
	private Supplier<Double> _conversionRateToNextStageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Description of the stage."
	)
	public String getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Description of the stage.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Name of the stage, unique within the lifecycle ignoring case (e.g. 'At Risk' or 'Pipeline')."
	)
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
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
		description = "Name of the stage, unique within the lifecycle ignoring case (e.g. 'At Risk' or 'Pipeline')."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Percentage of the lifecycle's current accounts that are in this stage."
	)
	public Double getPercentage() {
		if (_percentageSupplier != null) {
			percentage = _percentageSupplier.get();

			_percentageSupplier = null;
		}

		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;

		_percentageSupplier = null;
	}

	@JsonIgnore
	public void setPercentage(
		UnsafeSupplier<Double, Exception> percentageUnsafeSupplier) {

		_percentageSupplier = () -> {
			try {
				return percentageUnsafeSupplier.get();
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
		description = "Percentage of the lifecycle's current accounts that are in this stage."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double percentage;

	@JsonIgnore
	private Supplier<Double> _percentageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Position of the stage in the lifecycle, starting at 1."
	)
	public Integer getPosition() {
		if (_positionSupplier != null) {
			position = _positionSupplier.get();

			_positionSupplier = null;
		}

		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;

		_positionSupplier = null;
	}

	@JsonIgnore
	public void setPosition(
		UnsafeSupplier<Integer, Exception> positionUnsafeSupplier) {

		_positionSupplier = () -> {
			try {
				return positionUnsafeSupplier.get();
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
		description = "Position of the stage in the lifecycle, starting at 1."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Integer position;

	@JsonIgnore
	private Supplier<Integer> _positionSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AccountLifecycleStageMetric)) {
			return false;
		}

		AccountLifecycleStageMetric accountLifecycleStageMetric =
			(AccountLifecycleStageMetric)object;

		return Objects.equals(
			toString(), accountLifecycleStageMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long accountsCount = getAccountsCount();

		if (accountsCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountsCount\": ");

			sb.append(accountsCount);
		}

		Double averageStageDuration = getAverageStageDuration();

		if (averageStageDuration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"averageStageDuration\": ");

			sb.append(averageStageDuration);
		}

		Double conversionRateToNextStage = getConversionRateToNextStage();

		if (conversionRateToNextStage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"conversionRateToNextStage\": ");

			sb.append(conversionRateToNextStage);
		}

		String description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(description));

			sb.append("\"");
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		Double percentage = getPercentage();

		if (percentage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"percentage\": ");

			sb.append(percentage);
		}

		Integer position = getPosition();

		if (position != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"position\": ");

			sb.append(position);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageMetric",
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
// LIFERAY-REST-BUILDER-HASH:1426753718