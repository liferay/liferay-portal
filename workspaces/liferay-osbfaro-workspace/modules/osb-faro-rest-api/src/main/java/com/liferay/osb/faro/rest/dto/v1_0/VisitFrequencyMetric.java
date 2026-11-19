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
	description = "A split of the unique visitors of a channel over a date range by how often they came back. Buckets compare the number of sessions of each visitor to the number of days in the range: a visitor is daily with at least as many sessions as days, weekly with at least one session per 7 days, biweekly with at least one per 14 days, monthly with at least one per 30 days, and occasional otherwise. Each threshold is at least one session, so ranges shorter than 14, 28 or 60 days leave the biweekly, monthly or occasional bucket empty. Each visitor is counted in the most frequent bucket they qualify for. Use `getWorkspaceGroupChannelVisitFrequencyMetric` to retrieve it.",
	value = "VisitFrequencyMetric"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A split of the unique visitors of a channel over a date range by how often they came back. Buckets compare the number of sessions of each visitor to the number of days in the range: a visitor is daily with at least as many sessions as days, weekly with at least one session per 7 days, biweekly with at least one per 14 days, monthly with at least one per 30 days, and occasional otherwise. Each threshold is at least one session, so ranges shorter than 14, 28 or 60 days leave the biweekly, monthly or occasional bucket empty. Each visitor is counted in the most frequent bucket they qualify for. Use `getWorkspaceGroupChannelVisitFrequencyMetric` to retrieve it."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "VisitFrequencyMetric")
public class VisitFrequencyMetric implements Serializable {

	public static VisitFrequencyMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(VisitFrequencyMetric.class, json);
	}

	public static VisitFrequencyMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			VisitFrequencyMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Visitors with at least one session per 14 days of the range, who are not daily or weekly."
	)
	public Long getBiWeeklyVisitors() {
		if (_biWeeklyVisitorsSupplier != null) {
			biWeeklyVisitors = _biWeeklyVisitorsSupplier.get();

			_biWeeklyVisitorsSupplier = null;
		}

		return biWeeklyVisitors;
	}

	public void setBiWeeklyVisitors(Long biWeeklyVisitors) {
		this.biWeeklyVisitors = biWeeklyVisitors;

		_biWeeklyVisitorsSupplier = null;
	}

	@JsonIgnore
	public void setBiWeeklyVisitors(
		UnsafeSupplier<Long, Exception> biWeeklyVisitorsUnsafeSupplier) {

		_biWeeklyVisitorsSupplier = () -> {
			try {
				return biWeeklyVisitorsUnsafeSupplier.get();
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
		description = "Visitors with at least one session per 14 days of the range, who are not daily or weekly."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long biWeeklyVisitors;

	@JsonIgnore
	private Supplier<Long> _biWeeklyVisitorsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Visitors with at least as many sessions as days in the range."
	)
	public Long getDailyVisitors() {
		if (_dailyVisitorsSupplier != null) {
			dailyVisitors = _dailyVisitorsSupplier.get();

			_dailyVisitorsSupplier = null;
		}

		return dailyVisitors;
	}

	public void setDailyVisitors(Long dailyVisitors) {
		this.dailyVisitors = dailyVisitors;

		_dailyVisitorsSupplier = null;
	}

	@JsonIgnore
	public void setDailyVisitors(
		UnsafeSupplier<Long, Exception> dailyVisitorsUnsafeSupplier) {

		_dailyVisitorsSupplier = () -> {
			try {
				return dailyVisitorsUnsafeSupplier.get();
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
		description = "Visitors with at least as many sessions as days in the range."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long dailyVisitors;

	@JsonIgnore
	private Supplier<Long> _dailyVisitorsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Visitors with at least one session per 30 days of the range, who are not in a more frequent bucket."
	)
	public Long getMonthlyVisitors() {
		if (_monthlyVisitorsSupplier != null) {
			monthlyVisitors = _monthlyVisitorsSupplier.get();

			_monthlyVisitorsSupplier = null;
		}

		return monthlyVisitors;
	}

	public void setMonthlyVisitors(Long monthlyVisitors) {
		this.monthlyVisitors = monthlyVisitors;

		_monthlyVisitorsSupplier = null;
	}

	@JsonIgnore
	public void setMonthlyVisitors(
		UnsafeSupplier<Long, Exception> monthlyVisitorsUnsafeSupplier) {

		_monthlyVisitorsSupplier = () -> {
			try {
				return monthlyVisitorsUnsafeSupplier.get();
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
		description = "Visitors with at least one session per 30 days of the range, who are not in a more frequent bucket."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long monthlyVisitors;

	@JsonIgnore
	private Supplier<Long> _monthlyVisitorsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Visitors who are not in any of the other buckets."
	)
	public Long getOccasionalVisitors() {
		if (_occasionalVisitorsSupplier != null) {
			occasionalVisitors = _occasionalVisitorsSupplier.get();

			_occasionalVisitorsSupplier = null;
		}

		return occasionalVisitors;
	}

	public void setOccasionalVisitors(Long occasionalVisitors) {
		this.occasionalVisitors = occasionalVisitors;

		_occasionalVisitorsSupplier = null;
	}

	@JsonIgnore
	public void setOccasionalVisitors(
		UnsafeSupplier<Long, Exception> occasionalVisitorsUnsafeSupplier) {

		_occasionalVisitorsSupplier = () -> {
			try {
				return occasionalVisitorsUnsafeSupplier.get();
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
		description = "Visitors who are not in any of the other buckets."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long occasionalVisitors;

	@JsonIgnore
	private Supplier<Long> _occasionalVisitorsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Unique visitors in the range; the sum of the five buckets."
	)
	public Long getVisitors() {
		if (_visitorsSupplier != null) {
			visitors = _visitorsSupplier.get();

			_visitorsSupplier = null;
		}

		return visitors;
	}

	public void setVisitors(Long visitors) {
		this.visitors = visitors;

		_visitorsSupplier = null;
	}

	@JsonIgnore
	public void setVisitors(
		UnsafeSupplier<Long, Exception> visitorsUnsafeSupplier) {

		_visitorsSupplier = () -> {
			try {
				return visitorsUnsafeSupplier.get();
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
		description = "Unique visitors in the range; the sum of the five buckets."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long visitors;

	@JsonIgnore
	private Supplier<Long> _visitorsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Visitors with at least one session per 7 days of the range, who are not daily."
	)
	public Long getWeeklyVisitors() {
		if (_weeklyVisitorsSupplier != null) {
			weeklyVisitors = _weeklyVisitorsSupplier.get();

			_weeklyVisitorsSupplier = null;
		}

		return weeklyVisitors;
	}

	public void setWeeklyVisitors(Long weeklyVisitors) {
		this.weeklyVisitors = weeklyVisitors;

		_weeklyVisitorsSupplier = null;
	}

	@JsonIgnore
	public void setWeeklyVisitors(
		UnsafeSupplier<Long, Exception> weeklyVisitorsUnsafeSupplier) {

		_weeklyVisitorsSupplier = () -> {
			try {
				return weeklyVisitorsUnsafeSupplier.get();
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
		description = "Visitors with at least one session per 7 days of the range, who are not daily."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long weeklyVisitors;

	@JsonIgnore
	private Supplier<Long> _weeklyVisitorsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof VisitFrequencyMetric)) {
			return false;
		}

		VisitFrequencyMetric visitFrequencyMetric =
			(VisitFrequencyMetric)object;

		return Objects.equals(toString(), visitFrequencyMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long biWeeklyVisitors = getBiWeeklyVisitors();

		if (biWeeklyVisitors != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"biWeeklyVisitors\": ");

			sb.append(biWeeklyVisitors);
		}

		Long dailyVisitors = getDailyVisitors();

		if (dailyVisitors != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dailyVisitors\": ");

			sb.append(dailyVisitors);
		}

		Long monthlyVisitors = getMonthlyVisitors();

		if (monthlyVisitors != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"monthlyVisitors\": ");

			sb.append(monthlyVisitors);
		}

		Long occasionalVisitors = getOccasionalVisitors();

		if (occasionalVisitors != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"occasionalVisitors\": ");

			sb.append(occasionalVisitors);
		}

		Long visitors = getVisitors();

		if (visitors != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visitors\": ");

			sb.append(visitors);
		}

		Long weeklyVisitors = getWeeklyVisitors();

		if (weeklyVisitors != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"weeklyVisitors\": ");

			sb.append(weeklyVisitors);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.VisitFrequencyMetric",
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
// LIFERAY-REST-BUILDER-HASH:-1437753457